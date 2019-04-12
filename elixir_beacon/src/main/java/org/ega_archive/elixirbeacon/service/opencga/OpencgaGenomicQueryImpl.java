package org.ega_archive.elixirbeacon.service.opencga;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ega_archive.elixirbeacon.dto.BeaconGenomicRegionResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpResponse;
import org.ega_archive.elixirbeacon.dto.DatasetAlleleResponse;
import org.ega_archive.elixirbeacon.dto.Error;
import org.ega_archive.elixirbeacon.enums.ErrorCode;
import org.ega_archive.elixirbeacon.service.GenomicQuery;
import org.ega_archive.elixircore.event.sender.RestEventSender;
import org.mortbay.log.Log;
import org.opencb.biodata.models.core.pedigree.Individual.Sex;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.protobuf.VariantProto.StudyEntry.SamplesDataInfoEntryOrBuilder;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.commons.utils.ListUtils;
import org.opencb.opencga.catalog.db.api.SampleDBAdaptor;
import org.opencb.opencga.client.exceptions.ClientException;
import org.opencb.opencga.client.rest.OpenCGAClient;
import org.opencb.opencga.core.models.Project;
import org.opencb.opencga.core.models.Sample;
import org.opencb.opencga.core.models.Individual.KaryotypicSex;
import org.opencb.opencga.core.results.VariantQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Primary
@Slf4j
@Service
public class OpencgaGenomicQueryImpl implements GenomicQuery {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RestEventSender restEventSender;

	@Override
	public BeaconGenomicSnpResponse queryBeaconGenomicSnp(List<String> datasetStableIds, String alternateBases,
			String referenceBases, String chromosome, Integer start, String referenceGenome,
			String includeDatasetResponses, List<String> filters) {

		if (ListUtils.isNotEmpty(filters)) {
			BeaconGenomicSnpResponse response = new BeaconGenomicSnpResponse();
			Error error = new Error();
			error.setErrorCode(ErrorCode.GENERIC_ERROR);
			error.setMessage("filters are not supported");
			response.setError(error);
			return response;
		} else {
			String genotypes = "1,0/1,1/1";
			Query query = new Query();
			query.put("id", chromosome + ":" + start.toString());
			query.put("chromosome", chromosome);
			query.put("reference", referenceBases);
			query.put("alternate", alternateBases);
			query.put("includeGenotype", genotypes);
			query.put("summary", true);
			// query.put("limit", 1);
			
			try {
				OpenCGAClient opencga = OpencgaUtils.getClient();
				BeaconSnpVisitor visitor = new BeaconSnpVisitor(opencga, query);
				StudyVisitor wrapper = new VisitorByDatasetId(datasetStableIds, visitor);
				wrapper = new VisitorByAssembly(referenceGenome, wrapper);
				OpencgaUtils.visitStudies(wrapper, opencga);
				BeaconGenomicSnpResponse response = new BeaconGenomicSnpResponse();
				response.setDatasetAlleleResponses(visitor.getResults());
				return response;
			} catch (IOException | ClientException e) {
				BeaconGenomicSnpResponse response = new BeaconGenomicSnpResponse();
				Error error = new Error();
				error.setErrorCode(ErrorCode.GENERIC_ERROR);
				error.setMessage(e.getMessage());
				response.setError(error);
				return response;
			}
		}
	}

	@Override
	public BeaconGenomicRegionResponse queryBeaconGenomicRegion(List<String> datasetStableIds, String referenceBases,
			String chromosome, Integer start, Integer end, String referenceGenome, String includeDatasetResponses,
			List<String> filters) {
		// TODO Auto-generated method stub
		return null;
	}

}
