package org.ega_archive.elixirbeacon.service.opencga;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixirbeacon.dto.Beacon;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpResponse;
import org.ega_archive.elixirbeacon.dto.BeaconRequest;
import org.ega_archive.elixirbeacon.dto.Dataset;
import org.ega_archive.elixirbeacon.dto.Organization;
import org.ega_archive.elixirbeacon.enums.VariantType;
import org.ega_archive.elixirbeacon.service.ElixirBeaconService;
import org.ega_archive.elixirbeacon.service.GenomicQuery;
import org.ega_archive.elixircore.exception.NotImplementedException;
import org.ega_archive.elixircore.helper.CommonQuery;
import org.joda.time.DateTime;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.opencga.client.config.ClientConfiguration;
import org.opencb.opencga.client.config.GrpcConfig;
import org.opencb.opencga.client.config.RestConfig;
import org.opencb.opencga.client.exceptions.ClientException;
import org.opencb.opencga.client.rest.OpenCGAClient;
import org.opencb.opencga.core.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javassist.NotFoundException;

@Primary
@Service
public class ElixirBeaconServiceOpencgaImpl implements ElixirBeaconService {

	@Autowired
	private GenomicQuery genomicQuery;

	@Override
	public Beacon listDatasets(CommonQuery commonQuery, String referenceGenome) throws NotFoundException {
		try {

			OpenCGAClient opencga = OpencgaUtils.getClient();
			Stream<Project> projects = OpencgaUtils.getProjects(opencga, referenceGenome);

			List<Dataset> datasets = projects.map(project -> {
				Dataset dataset = new Dataset();
				dataset.setId(Long.toString(project.getId()));
				dataset.setName(project.getName());
				dataset.setDescription(project.getDescription());
				dataset.setAssemblyId("GRCh37");
				dataset.setCreateDateTime(OpencgaUtils.translateOpencgaDate(project.getCreationDate()));
				dataset.setUpdateDateTime(OpencgaUtils.translateOpencgaDate(project.getLastModified()));
				return dataset;
			}).collect(Collectors.toList());

			Beacon beacon = new Beacon();
			beacon.setDatasets(datasets);
			return beacon;
		} catch (IOException |

				ClientException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BeaconGenomicSnpResponse queryBeacon(List<String> datasetStableIds, String variantType,
			String alternateBases, String referenceBases, String chromosome, Integer start, Integer startMin,
			Integer startMax, Integer end, Integer endMin, Integer endMax, String referenceGenome,
			String includeDatasetResponses, List<String> filters) {

		if (StringUtils.isNotBlank(alternateBases) && StringUtils.isNotBlank(referenceBases)
				&& StringUtils.isNotBlank(chromosome) && start != null && StringUtils.isBlank(variantType)
				&& end == null && startMin == null && startMax == null && endMin == null && endMax == null) {

			return genomicQuery.queryBeaconGenomicSnp(datasetStableIds, alternateBases, referenceBases, chromosome,
					start, referenceGenome, includeDatasetResponses, filters);
		}
		throw new NotImplementedException("Query not implemented");
	}

	@Override
	public List<Integer> checkParams(BeaconAlleleResponse result, List<String> datasetStableIds, VariantType type,
			String alternateBases, String referenceBases, String chromosome, Integer start, Integer startMin,
			Integer startMax, Integer end, Integer endMin, Integer endMax, String referenceGenome, List<String> filters,
			List<String> translatedFilters) {
		return null;
	}

	@Override
	public BeaconAlleleResponse queryBeacon(BeaconRequest request) {
		return null;
	}
}
