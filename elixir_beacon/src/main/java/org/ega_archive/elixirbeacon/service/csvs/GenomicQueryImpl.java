package org.ega_archive.elixirbeacon.service.csvs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.babelomics.csvs.lib.models.DiseaseCount;
import org.babelomics.csvs.lib.models.Variant;
import org.babelomics.csvs.lib.ws.QueryResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicRegionResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpRequest;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpResponse;
import org.ega_archive.elixirbeacon.dto.KeyValuePair;
import org.ega_archive.elixirbeacon.service.GenomicQuery;
import org.ega_archive.elixirbeacon.utils.ParseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GenomicQueryImpl implements GenomicQuery {

	@Autowired
	private ParseResponse parseResponse;

	@Override
	public BeaconGenomicSnpResponse queryBeaconGenomicSnp(List<String> datasetStableIds, String alternateBases,
			String referenceBases, String chromosome, Integer start, String referenceGenome,
			String includeDatasetResponses, List<String> filters) {

		// TODO: check referenceGenome is GRCh37 -> do it in checkParams
		// TODO: Add new endpoint to CSVS to return variants by dataset and use it here
		// TODO: Move all URLs to the properties file

		List<KeyValuePair> info = findRegionVariants(chromosome, start, start + 1, referenceBases, alternateBases,
				datasetStableIds, filters);

		BeaconGenomicSnpResponse beaconGenomicSnpResponse = new BeaconGenomicSnpResponse();
		beaconGenomicSnpResponse.setExists(findVariantCount(info));
		// TODO: fill the request
		BeaconGenomicSnpRequest request = new BeaconGenomicSnpRequest();
		beaconGenomicSnpResponse.setRequest(request);
		beaconGenomicSnpResponse.setInfo(info);
		return beaconGenomicSnpResponse;
	}

	@Override
	public BeaconGenomicRegionResponse queryBeaconGenomicRegion(List<String> datasetStableIds, String referenceBases,
			String chromosome, Integer start, Integer end, String referenceGenome, String includeDatasetResponses,
			List<String> filters) {

		List<KeyValuePair> info = findRegionVariants(chromosome, start, end, referenceBases, null, datasetStableIds,
				filters);

		BeaconGenomicRegionResponse response = new BeaconGenomicRegionResponse();
		// TODO: fill the request
		response.setExists(findVariantCount(info));
		response.setInfo(info);
		return response;
	}

	private boolean findVariantCount(List<KeyValuePair> info) {
		Optional<Integer> variantCount = info.stream()
				.filter(v -> StringUtils.equalsIgnoreCase(v.getKey(), "variantCount"))
				.map(v -> Integer.parseInt(v.getValue())).findFirst();
		return variantCount.isPresent() && variantCount.get() > 0;
	}

	private List<KeyValuePair> findRegionVariants(String chromosome, Integer start, Integer end, String referenceBases,
			String alternateBases, List<String> datasetStableIds, List<String> filters) {

		String technologyFilter = null;
		if (null != filters) {
			// TODO: load this data to the ontology table and check it
			String technologyFilterValues = filters.stream().filter(filter -> filter.startsWith("myDictionary"))
					.map(filter -> filter.split(":", 2)[1]).collect(Collectors.joining(","));
			technologyFilter = StringUtils.isNotBlank(technologyFilterValues)
					? "&technologies=" + technologyFilterValues
					: null;
		}

		String url = "http://csvs.clinbioinfosspa.es:8080/csvs/rest/variants/fetch?regions=";
		url = url + chromosome + ":" + start + "-" + end;

		if (datasetStableIds != null && !datasetStableIds.isEmpty()) {
			url += "&diseases=" + String.join(",", datasetStableIds);
		}
		if (StringUtils.isNotBlank(technologyFilter)) {
			url += technologyFilter;
		}

		log.debug("url {}", url);
		QueryResponse<Variant> variantQueryResponse = parseResponse.parseCsvsResponse(url, Variant.class);
		log.debug("response: {}", variantQueryResponse);

		boolean isRegionQuery = StringUtils.isBlank(alternateBases) && end != null;

		boolean exists = variantQueryResponse.getNumTotalResults() > 0 && variantQueryResponse.getResult() != null
				&& variantQueryResponse.getResult().get(0) != null;
		List<KeyValuePair> info = new ArrayList<>();
		int numVariants = 0;
		if (exists) {
			List<Variant> result = variantQueryResponse.getResult();
			for (Variant variant : result) {
				if (checkParameters(referenceBases, variant.getReference(), alternateBases, variant.getAlternate(),
						isRegionQuery)) {
					numVariants++;

					DiseaseCount stats = variant.getStats();
					info.add(new KeyValuePair("0/0", String.valueOf(stats.getGt00())));
					info.add(new KeyValuePair("0/1", String.valueOf(stats.getGt01())));
					info.add(new KeyValuePair("1/1", String.valueOf(stats.getGt11())));
					info.add(new KeyValuePair("./.", String.valueOf(stats.getGtmissing())));
					info.add(new KeyValuePair("0 Freq", String.valueOf(stats.getRefFreq())));
					info.add(new KeyValuePair("1 Freq", String.valueOf(stats.getAltFreq())));
					info.add(new KeyValuePair("MAF", String.valueOf(stats.getMaf())));
					// TODO: consider if we should show this info
//          info.add(new KeyValuePair("Num sample regions", String.valueOf(stats.getSumSampleRegions())));
//          info.add(new KeyValuePair("Total GTs", String.valueOf(stats.getTotalGts())));
				}
			}
		}
		if (numVariants > 1) {
			info = new ArrayList<>();
		}
		if (numVariants > 0) {
			info.add(new KeyValuePair("variantCount", String.valueOf(numVariants)));
		}
		return info;
	}

	private boolean checkParameters(String referenceBases, String reference, String alternateBases, String alternate,
			boolean isRegionQuery) {
		if (isRegionQuery) {
			return StringUtils.isBlank(referenceBases) || StringUtils.equalsIgnoreCase(referenceBases, reference);
		} else {
			return StringUtils.equalsIgnoreCase(referenceBases, reference)
					&& (StringUtils.equalsIgnoreCase(alternateBases, "N")
							|| StringUtils.equalsIgnoreCase(alternateBases, alternate));
		}
	}

}
