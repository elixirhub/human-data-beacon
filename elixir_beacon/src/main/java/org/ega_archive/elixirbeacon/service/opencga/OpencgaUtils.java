package org.ega_archive.elixirbeacon.service.opencga;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.mortbay.log.Log;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.commons.utils.ListUtils;
import org.opencb.opencga.client.config.ClientConfiguration;
import org.opencb.opencga.client.config.GrpcConfig;
import org.opencb.opencga.client.config.RestConfig;
import org.opencb.opencga.client.exceptions.ClientException;
import org.opencb.opencga.client.rest.OpenCGAClient;
import org.opencb.opencga.core.models.Project;

public class OpencgaUtils {

	public static DateTime translateOpencgaDate(String dateString) {

		// 20190329112010
		try {
			int year = Integer.parseInt(dateString.substring(0, 4));
			int month = Integer.parseInt(dateString.substring(4, 6));
			int day = Integer.parseInt(dateString.substring(6, 8));
			int hour = Integer.parseInt(dateString.substring(8, 10));
			int minute = Integer.parseInt(dateString.substring(10, 12));
			// int second = Integer.parseInt(dateString.substring(12, 14));
			return new DateTime(year, month, day, hour, minute);
		} catch (Exception exc) {
			Log.debug(exc.getMessage());
			return null;
		}
	}

	private static boolean isVariantPresent(String sampleGenotype) {
		return 0 <= sampleGenotype.indexOf("1");
	}

	public static OpenCGAClient getClient() throws ClientException {
		GrpcConfig grpc = new GrpcConfig();
		RestConfig rest = new RestConfig(host, 100, 30000, 100);
		ClientConfiguration config = new ClientConfiguration(rest, grpc);
		return new OpenCGAClient(username, password, config);
	}

	public static Stream<Project> getProjects(OpenCGAClient client, String referenceGenome) throws IOException {
		
		String[] invalidProjectArray = {				
				"ENOD_Genomes_qGenomics_GRCh37",
				"ENOD_Genomes_qGenomics_GRCh37_2",
				"ENOD_Genomes_qGenomics_GRCh37_3"
		};	
		List<String> invalidProjects = Arrays.asList(invalidProjectArray);
		
		// TODO: filter projects by referenceGenome
		
		
		Query params = new Query();
		QueryOptions options = QueryOptions.empty();
		List<Project> projects = client.getProjectClient().search(params, options).allResults();
		return projects.stream().filter(project -> !invalidProjects.contains(project.getName()));
	}

	public static Stream<Project> getProjectSubset(OpenCGAClient client, String referenceGenome,
			List<String> datasetIds) throws IOException {
		Stream<Project> projects = getProjects(client, referenceGenome);
		return ListUtils.isEmpty(datasetIds) ? projects
				: projects.filter(project -> datasetIds.stream()
						.anyMatch(datasetId -> Long.toString(project.getId()).equals(datasetId)));
	}

	private static final String username = "enod";
	private static final String password = "3n0d";
	private static final String host = "http://iva-enod.clinbioinfosspa.es:8080/opencga-1.3.6";

}
