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

import javax.servlet.http.HttpServletRequest;

@Primary
@Service
public class ElixirBeaconServiceOpencgaImpl implements ElixirBeaconService {

	@Autowired
	private HttpServletRequest incomingRequest;

	@Autowired
	private GenomicQuery genomicQuery;

	@Override
	public Beacon listDatasets(CommonQuery commonQuery, String assembly) throws NotFoundException {
		try {
			String authorization = incomingRequest.getHeader("Authorization");
			String sessionToken = parseSessionToken(authorization );

			OpenCGAClient opencga = OpencgaUtils.getClient(sessionToken);
			DatasetLister datasetLister = new DatasetLister();
			StudyVisitor visitor = new VisitorByAssembly(assembly, datasetLister);
			OpencgaUtils.visitStudies(datasetLister, opencga);
			Beacon beacon = new Beacon();
			beacon.setDatasets(datasetLister.getDatasets());
			return beacon;
		} catch (IOException | ClientException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BeaconGenomicSnpResponse queryBeacon(List<String> datasetStableIds, String variantType,
			String alternateBases, String referenceBases, String chromosome, Integer start, Integer startMin,
			Integer startMax, Integer end, Integer endMin, Integer endMax, String referenceGenome,
			String includeDatasetResponses, List<String> filters) {
		String authorization = incomingRequest.getHeader("Authorization");
		String sessionToken = parseSessionToken(authorization );

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

	private static String parseSessionToken(String authorization) {
		return authorization.startsWith("Bearer ") ? authorization.substring(7).trim() : null;
	}

}
