package org.ega_archive.elixirbeacon.controller;

import java.util.List;
import java.util.Map;

import org.ega_archive.elixirbeacon.dto.Beacon;
import org.ega_archive.elixirbeacon.dto.BeaconRequest;
import org.ega_archive.elixirbeacon.service.ElixirBeaconService;
import org.ega_archive.elixircore.constant.ParamName;
import org.ega_archive.elixircore.helper.CommonQueryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javassist.NotFoundException;

@RestController
@RequestMapping("/beacon")
public class ElixirBeaconController {

	@Autowired
	private ElixirBeaconService elixirBeaconService;

	@GetMapping(value = "/")
	public Beacon listDatasets(Sort sort, @RequestParam(required = false) Map<String, String> params)
			throws NotFoundException {

		return elixirBeaconService.listDatasets(CommonQueryHelper.parseQuery(params, sort), null);
	}

	@GetMapping(value = "/query")
	public Object queryBeacon(
			@RequestParam(value = ParamName.BEACON_DATASET_IDS, required = false) List<String> datasetStableIds,
			@RequestParam(value = ParamName.BEACON_ALTERNATE_BASES, required = false) String alternateBases,
			@RequestParam(value = ParamName.BEACON_REFERENCE_BASES, required = false) String referenceBases,
			@RequestParam(value = ParamName.BEACON_CHROMOSOME, required = false) String chromosome,
			@RequestParam(value = ParamName.BEACON_START, required = false) Integer start,
			@RequestParam(value = ParamName.BEACON_START_MIN, required = false) Integer startMin,
			@RequestParam(value = ParamName.BEACON_START_MAX, required = false) Integer startMax,
			@RequestParam(value = ParamName.BEACON_END, required = false) Integer end,
			@RequestParam(value = ParamName.BEACON_END_MIN, required = false) Integer endMin,
			@RequestParam(value = ParamName.BEACON_END_MAX, required = false) Integer endMax,
			@RequestParam(value = ParamName.VARIANT_TYPE, required = false) String variantType,
			@RequestParam(value = ParamName.BEACON_REFERENCE_GENOME, required = false) String referenceGenome,
			@RequestParam(value = ParamName.BEACON_INCLUDE_DATASET_RESPONSES, required = false) String includeDatasetResponses,
			@RequestParam(value = ParamName.BEACON_FILTERS, required = false) List<String> filters,
			@RequestParam(value = "auth", required = false) String auth) {

		return elixirBeaconService.queryBeacon(datasetStableIds, variantType, alternateBases, referenceBases,
				chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome, includeDatasetResponses,
				filters);
	}

	@PostMapping(value = "/query")
	public Object queryBeaconPost(@RequestBody BeaconRequest request) {

		return elixirBeaconService.queryBeacon(request);
	}

}
