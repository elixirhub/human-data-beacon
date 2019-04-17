package org.ega_archive.elixirbeacon.service.opencga;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicRegionResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpResponse;
import org.ega_archive.elixirbeacon.dto.DatasetAlleleResponse;
import org.ega_archive.elixirbeacon.dto.Error;
import org.ega_archive.elixirbeacon.enums.ErrorCode;
import org.ega_archive.elixirbeacon.service.GenomicQuery;
import org.ega_archive.elixircore.event.sender.RestEventSender;
import org.opencb.commons.utils.ListUtils;
import org.opencb.opencga.client.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Autowired
    private HttpServletRequest incomingRequest;

    @Override
    public BeaconGenomicSnpResponse queryBeaconGenomicSnp(List<String> datasetStableIds, String alternateBases,
                                                          String referenceBases, String chromosome, Integer start, String referenceGenome,
                                                          String includeDatasetResponsesString, List<String> filters) {

        String variantId = getVariantId(chromosome, start, referenceBases, alternateBases);
        BeaconGenomicSnpResponse response = new BeaconGenomicSnpResponse();
        try {
            IncludeDatasetResponses includeDatasetResponses = parseIncludeDatasetResponses(includeDatasetResponsesString);

            Filter filter = ListUtils.isEmpty(filters) ? null : Filter.parse(filters);

            String authorization = incomingRequest.getHeader("Authorization");
            String sessionToken = OpencgaUtils.parseSessionToken(authorization );
            OpencgaEnrichedClient opencga = OpencgaUtils.getClient(sessionToken);

            BeaconSnpVisitor visitor = new BeaconSnpVisitor(opencga, variantId, filter);
            StudyVisitor wrapper = new VisitorByDatasetId(datasetStableIds, visitor);
            wrapper = new VisitorByAssembly(referenceGenome, wrapper);
            OpencgaUtils.visitStudies(wrapper, opencga);
            List<DatasetAlleleResponse> datasetResponses = visitor.getResults();
            response.setExists(datasetResponses.stream().anyMatch(DatasetAlleleResponse::isExists));
            response.setDatasetAlleleResponses(filterDatasetResults(datasetResponses, includeDatasetResponses));
        } catch (IOException | ClientException e) {
            Error error = new Error();
            error.setErrorCode(ErrorCode.GENERIC_ERROR);
            error.setMessage(e.getMessage());
            response.setError(error);
            return response;
        }
        return response;
    }

    @Override
    public BeaconGenomicRegionResponse queryBeaconGenomicRegion(List<String> datasetStableIds, String referenceBases,
                                                                String chromosome, Integer start, Integer end, String referenceGenome, String includeDatasetResponsesString,
                                                                List<String> filters) {
        // TODO Auto-generated method stub
        throw new NotImplementedException("queryBeaconGenomicRegion");
    }

    private List<DatasetAlleleResponse> filterDatasetResults(List<DatasetAlleleResponse> datasetResponses, IncludeDatasetResponses includeDatasetResponses) {
        switch (includeDatasetResponses) {
            case HIT:
                return datasetResponses.stream().filter(DatasetAlleleResponse::isExists).collect(Collectors.toList());
            case MISS:
                return datasetResponses.stream().filter(response -> !response.isExists()).collect(Collectors.toList());
            case NULL:
                return datasetResponses.stream().filter(response -> Objects.nonNull(response.getError())).collect(Collectors.toList());
            case NONE:
                return new ArrayList<>();
            // case ALL:
            default:
                return datasetResponses;
        }
    }

    private static IncludeDatasetResponses parseIncludeDatasetResponses(String value) throws IOException {
        if (Objects.isNull(value)) {
            return IncludeDatasetResponses.ALL;
        } else {
            switch(value.toLowerCase()) {
                case "all":
                     return IncludeDatasetResponses.ALL;
                case "hit":
                    return IncludeDatasetResponses.HIT;
                case "miss":
                    return IncludeDatasetResponses.MISS;
                case "null":
                    return IncludeDatasetResponses.NULL;
                case "none":
                    return IncludeDatasetResponses.NONE;
                default:
                    throw new IOException("invalid parameter: includeDatasetResponses");

            }
        }
    }

    private static String getVariantId(String chromosome, Integer start, String reference, String alternate) {
        return String.format("%s:%s:%s:%s", chromosome, start, reference, alternate);
    }

}
