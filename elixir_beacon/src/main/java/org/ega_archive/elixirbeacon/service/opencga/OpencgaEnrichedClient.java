package org.ega_archive.elixirbeacon.service.opencga;

import org.opencb.opencga.client.config.ClientConfiguration;
import org.opencb.opencga.client.exceptions.ClientException;
import org.opencb.opencga.client.rest.OpenCGAClient;

public class OpencgaEnrichedClient extends OpenCGAClient {

    private ClientConfiguration clientConfiguration;

    public OpencgaEnrichedClient() {
        super();
    }

    public OpencgaEnrichedClient(ClientConfiguration clientConfiguration) {
        super(clientConfiguration);
        this.clientConfiguration = clientConfiguration;
    }

    public OpencgaEnrichedClient(String user, String password, ClientConfiguration clientConfiguration) throws ClientException {
        super(user, password, clientConfiguration);
        this.clientConfiguration = clientConfiguration;
    }

    public OpencgaEnrichedClient(String sessionId, ClientConfiguration clientConfiguration) {
        super(sessionId, clientConfiguration);
        this.clientConfiguration = clientConfiguration;
    }

    public OpencgaBeaconClient getBeaconClient() {
        return new OpencgaBeaconClient(super.getUserId(), super.getSessionId(), clientConfiguration);
    }
}
