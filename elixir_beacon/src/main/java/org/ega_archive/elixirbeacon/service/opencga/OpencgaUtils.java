package org.ega_archive.elixirbeacon.service.opencga;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.opencga.client.config.ClientConfiguration;
import org.opencb.opencga.client.config.GrpcConfig;
import org.opencb.opencga.client.config.RestConfig;
import org.opencb.opencga.client.exceptions.ClientException;
import org.opencb.opencga.client.rest.OpenCGAClient;
import org.opencb.opencga.core.models.Project;
import org.opencb.opencga.core.models.Study;

import java.io.IOException;
import java.util.List;

public class OpencgaUtils {

    public static DateTime translateOpencgaDate(String dateString) {
        try {
            int year = Integer.parseInt(dateString.substring(0, 4));
            int month = Integer.parseInt(dateString.substring(4, 6));
            int day = Integer.parseInt(dateString.substring(6, 8));
            int hour = Integer.parseInt(dateString.substring(8, 10));
            int minute = Integer.parseInt(dateString.substring(10, 12));
            // int second = Integer.parseInt(dateString.substring(12, 14));
            return new DateTime(year, month, day, hour, minute);
        } catch (Exception exc) {
            return null;
        }
    }

    public static OpencgaEnrichedClient getClient() throws ClientException {
        return getClient(null);
    }

    public static OpencgaEnrichedClient getClient(String sessionToken) throws ClientException {
        GrpcConfig grpc = new GrpcConfig();
        RestConfig rest = new RestConfig(host, 100, 30000, 100);
        ClientConfiguration config = new ClientConfiguration(rest, grpc);

        if (StringUtils.isNotBlank(sessionToken)) {
            return new OpencgaEnrichedClient(sessionToken, config);
        } else {
            return new OpencgaEnrichedClient(username, password, config);
        }
    }

    public static void visitStudies(StudyVisitor visitor, OpenCGAClient client) throws IOException {
        Query params = new Query();
        QueryOptions options = QueryOptions.empty();
        List<Project> projects = client.getProjectClient().search(params, options).allResults();
        for (Project project : projects) {
            List<Study> studies = client.getProjectClient().getStudies(project.getAlias(), QueryOptions.empty())
                    .allResults();
            for (Study study : studies) {
                visitor.visit(project, study);
            }
        }
    }

    public static String parseSessionToken(String authorization) {
        return StringUtils.isNotBlank(authorization) && authorization.startsWith("Bearer ") ? authorization.substring(7).trim() : null;
    }

    // TODO: fill these and put it in properties
    private static final String username = "";
    private static final String password = "";
    private static final String host = "http://iva-enod.clinbioinfosspa.es:8080/opencga-1.3.8";

}
