package org.ega_archive.elixirbeacon.service.opencga;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpResponse;
import org.ega_archive.elixirbeacon.dto.DatasetAlleleResponse;
import org.ega_archive.elixirbeacon.dto.KeyValuePair;
import org.ega_archive.elixirbeacon.service.BeaconGenomicRegionResponse;
import org.ega_archive.elixirbeacon.service.GenomicQuery;
import org.ega_archive.elixircore.dto.Base;
import org.ega_archive.elixircore.event.sender.RestEventSender;
import org.ega_archive.elixircore.exception.RestRuntimeException;
import org.ega_archive.elixircore.exception.ServerDownException;
import org.mortbay.log.Log;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.commons.datastore.core.ObjectMap;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.commons.datastore.core.QueryResponse;
import org.opencb.commons.utils.ListUtils;
import org.opencb.opencga.client.config.ClientConfiguration;
import org.opencb.opencga.client.config.GrpcConfig;
import org.opencb.opencga.client.config.RestConfig;
import org.opencb.opencga.client.exceptions.ClientException;
import org.opencb.opencga.client.rest.OpenCGAClient;
import org.opencb.opencga.core.models.Project;
import org.opencb.opencga.core.models.Sample;
import org.opencb.opencga.core.results.VariantQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Primary
@Slf4j
@Service
public class OpencgaGenomicQueryImpl implements GenomicQuery {

	private static enum Zygosity {
		ANY, HOMOZYGOSITY, HETEROZYGOSITY,
	}

	private static class Filter {
		public boolean genotypicMale = false;
		public boolean genotypicFemale = false;
		public boolean phenotypicMale = false;
		public boolean phenotypicFemale = false;
		public Zygosity zygosity = Zygosity.ANY;
	}

	private static Filter parseFilters(List<String> filters) {
		Filter filter = new Filter();
		filter.genotypicMale = filters.contains("PATO:0020001");
		filter.genotypicFemale = filters.contains("PATO:0020002");
		filter.phenotypicFemale = filters.contains("PATO:0000383");
		filter.phenotypicMale = filters.contains("PATO:0000384");
		return filter;
	}

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RestEventSender restEventSender;

	private static boolean queryProjectVariants(DatasetAlleleResponse response, OpenCGAClient client, Project project,
			String chromosome, Integer start, String reference, String alternate, Query queryTemplate,
			List<String> filters) throws IOException {

		Query query = new Query(queryTemplate);		
		query.put("project", project.getAlias());
		QueryOptions options = QueryOptions.empty();		
		VariantQueryResult<Variant> result = client.getVariantClient().query2(query, options);

		int totalSampleCount = 0;
		List<Sample> samplesWithVariant = new ArrayList<>();
		for (Variant variant : result.getResult()) {
			List<StudyEntry> studyEntries = variant.getStudies();
			for (StudyEntry studyEntry : studyEntries) {
				// TODO: in the actual World this should support large sets of samples per study
				// and project
				// far beyond what can be retrieved in one fetch
				List<Sample> samples = client.getStudyClient().getSamples(studyEntry.getStudyId(), QueryOptions.empty())
						.allResults();
				totalSampleCount += samples.size();
				for (int idx = 0; idx < samples.size(); ++idx) {
					String sampleGenotype = studyEntry.getSamplesData().get(idx).get(0);
					boolean isVariantPresent = (0 <= sampleGenotype.indexOf("1"));
					if (isVariantPresent) {
						Sample sample = samples.get(idx);
						sample.getIndividual().getSex();
						samplesWithVariant.add(samples.get(idx));
					}
				}
			}
		}
		// DatasetAlleleResponse response = new DatasetAlleleResponse();
		response.setDatasetId(Long.toString(project.getId()));
		response.setExists(0 < samplesWithVariant.size());
		// response.setError(false);
		double frequency = 0 == totalSampleCount ? 0 : (double) samplesWithVariant.size() / (double) totalSampleCount;
		response.setFrequency(new BigDecimal(frequency));
		response.setVariantCount(0 < samplesWithVariant.size() ? 1L : 0L);
		// response.setCallCount(0);
		response.setSampleCount((long) samplesWithVariant.size());

		Map<String, Object> info = new HashMap<String, Object>();
		String sampleIds = samplesWithVariant.stream().map(sample -> Long.toString(sample.getId()))
				.collect(Collectors.joining(","));
		info.put("sampleIds", sampleIds);
		response.setInfo(info);

		return 0 < samplesWithVariant.size();
	}

	@Override
	public BeaconGenomicSnpResponse queryBeaconGenomicSnp(List<String> datasetStableIds, String alternateBases,
			String referenceBases, String chromosome, Integer start, String referenceGenome,
			String includeDatasetResponses, List<String> filters) {

		Filter filter = parseFilters(filters);

		String genotypes;
		switch (filter.zygosity) {
		case HOMOZYGOSITY:
			genotypes = "1/1";
			break;
		case HETEROZYGOSITY:
			genotypes = "0/1";
			break;
		default:
			genotypes = "1,0/1,1/1,0/0/1,0/1/1,1/1/1";
		}

		// build filter
		Query query = new Query();
		query.put("id", chromosome + ":" + start.toString());
		query.put("chromosome", chromosome);
		query.put("reference", referenceBases);
		query.put("alternate", alternateBases);
		query.put("includeGenotype", genotypes);
		// params.put("count", true);
		// params.put("summary", true);
		// params.put("limit", 10);
		// params.put("type", "");

		// filters.stream().anyMatch(filter -> "")

		// TODO: filter projects by referenceGenome
		try {
			OpenCGAClient client = OpencgaUtils.getClient();
			List<Project> projects = OpencgaUtils.getProjectSubset(client, referenceGenome, datasetStableIds)
					.collect(Collectors.toList());
			boolean finalRes = false;
			BeaconGenomicSnpResponse response = new BeaconGenomicSnpResponse();
			for (Project project : projects) {
				DatasetAlleleResponse result = new DatasetAlleleResponse();
				boolean res = queryProjectVariants(result, client, project, chromosome, start, referenceBases,
						alternateBases, query, filters);
				response.addDatasetAlleleResponse(result);
				finalRes = finalRes || res;
			}
			response.setExists(finalRes);
			return response;
		} catch (IOException | ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

//		String technologyFilter = null;
//		if (null != filters) {
//
//			String technologyFilterValues = filters.stream().filter(filter -> filter.startsWith("myDictionary"))
//					.map(filter -> filter.split(":", 2)[1]).collect(Collectors.joining(","));
//			technologyFilter = StringUtils.isNotBlank(technologyFilterValues)
//					? "&technologies=" + technologyFilterValues
//					: null;
//		}

//		try {
//			response = this.restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(null, null), String.class,
//					new Object[0]);
//		} catch (ResourceAccessException var15) {
//			throw new ServerDownException(var15.getMessage());
//		}
//
//		JavaType type = this.objectMapper.getTypeFactory().constructParametricType(QueryResponse.class,
//				new Class[] { Variant.class });
//		QueryResponse basicDTO = null;
//
//		try {
//			basicDTO = (QueryResponse) this.objectMapper.readValue((String) response.getBody(), type);
////      return basicDTO;
//		} catch (IOException var14) {
//			throw new RestRuntimeException("500",
//					"Exception deserializing object: " + (String) response.getBody() + "\n" + var14.getMessage());
//		}		

	}

	@Override
	public BeaconGenomicRegionResponse queryBeaconGenomicRegion(List<String> datasetStableIds, String referenceBases,
			String chromosome, Integer start, Integer end, String referenceGenome, String includeDatasetResponses,
			List<String> filters) {
		// TODO Auto-generated method stub
		return null;
	}

}
