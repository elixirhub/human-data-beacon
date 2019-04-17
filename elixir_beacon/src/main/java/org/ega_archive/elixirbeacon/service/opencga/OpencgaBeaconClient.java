package org.ega_archive.elixirbeacon.service.opencga;

import org.opencb.commons.datastore.core.ObjectMap;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.commons.datastore.core.QueryResponse;
import org.opencb.opencga.client.config.ClientConfiguration;
import org.opencb.opencga.client.rest.AbstractParentClient;
import org.opencb.opencga.core.models.Sample;

import java.io.IOException;
import java.util.Objects;


public class OpencgaBeaconClient extends AbstractParentClient {

    private static final String BEACON_SERVICE_URL = "analysis/variant";

    public OpencgaBeaconClient(String userId, String sessionId, ClientConfiguration configuration) {
        super(userId, sessionId, configuration);
    }

    public QueryResponse<Sample> getSamplesWithVariant(ObjectMap params, QueryOptions options) throws IOException {
        if (Objects.nonNull(options)) {
            params = new ObjectMap(params);
            params.putAll(options);
        }
        return execute(BEACON_SERVICE_URL, "samples", params, GET, Sample.class);
    }

}
