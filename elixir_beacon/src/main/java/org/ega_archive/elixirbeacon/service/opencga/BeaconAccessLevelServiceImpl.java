package org.ega_archive.elixirbeacon.service.opencga;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.dto.AccessLevelResponse;
import org.ega_archive.elixirbeacon.dto.Error;
import org.ega_archive.elixirbeacon.enums.ErrorCode;
import org.ega_archive.elixirbeacon.service.BeaconAccessLevelService;
import org.ega_archive.elixircore.exception.NotImplementedException;
import org.opencb.commons.utils.ListUtils;
import org.opencb.opencga.client.exceptions.ClientException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Primary
public class BeaconAccessLevelServiceImpl implements BeaconAccessLevelService {

//    public static final String ACCESS_LEVEL_SUMMARY = "accessLevelSummary";
//    @Autowired
//    private DatasetAccessLevelRepository datasetAccessLevelRepository;

    @Override
    public AccessLevelResponse listAccessLevels(List<String> fields,
                                                List<String> datasetStableIds, String level, boolean includeFieldDetails,
                                                boolean includeDatasetDetails) {
        // TODO implement search by "level"
        if (StringUtils.isNotBlank(level)) {
            throw new NotImplementedException("Searching by 'level' is not implemented yet!");
        }

        AccessLevelResponse response = new AccessLevelResponse();
        response.setBeaconId(BeaconConstants.BEACON_ID);

        // HACK: we are hardwiring this; it depends on the implmenetation, so its ok; but it would be better to...
        // TODO: implement a more flexible solution

        Map<String, Object> fieldAccessLevels = getFieldAccessLevels(fields, includeFieldDetails);
        response.setFields(fieldAccessLevels);

        try {
            Map<String, Object> datasetAccessLevels = getDatasetAccessLevels(datasetStableIds, includeDatasetDetails);
            response.setDatasets(datasetAccessLevels);
        } catch (ClientException | IOException e) {
            Error error = new Error();
            error.setErrorCode(ErrorCode.GENERIC_ERROR);
            error.setMessage(e.getMessage());
            response.setError(new Error());
        }

        return response;
    }

    private Map<String, Object> getDatasetAccessLevels(List<String> datasetIds, boolean includeDatasetDetails) throws ClientException, IOException {
        DatasetAccessLevelVisitor visitor = new DatasetAccessLevelVisitor();
        OpencgaEnrichedClient opencga = OpencgaUtils.getClient();
        VisitorByDatasetId wrapper = new VisitorByDatasetId(datasetIds, visitor);
        OpencgaUtils.visitStudies(wrapper, opencga);
        Map<String, Object> accessLevels = visitor.getDatasetAccessLevels();
        if (ListUtils.isNotEmpty(datasetIds)) {
            for (String datasetId : datasetIds) {
                if (!accessLevels.containsKey(datasetId)) {
                    accessLevels.put(datasetId, AccessLevels.NOT_SUPPORTED);
                }
            }
        }
        return accessLevels;
    }

    private Map<String, Object> getFieldAccessLevels(List<String> fields, boolean includeFieldDetails) {
        // fields: public, registered, not_supported
        Map<String, Object> accessLevels = new HashMap<>();
        if (includeFieldDetails) {
            Map<String, String> beaconFieldAccessLevels = new HashMap<>();
            beaconFieldAccessLevels.put("accessLevelSummary", AccessLevels.PUBLIC);
            beaconFieldAccessLevels.put("id", AccessLevels.PUBLIC);
            beaconFieldAccessLevels.put("name", AccessLevels.PUBLIC);
            beaconFieldAccessLevels.put("apiVersion", AccessLevels.PUBLIC);
            beaconFieldAccessLevels.put("organization", AccessLevels.PUBLIC);
            beaconFieldAccessLevels.put("description", AccessLevels.PUBLIC);
            beaconFieldAccessLevels.put("version", AccessLevels.PUBLIC);
            beaconFieldAccessLevels.put("welcomeUrl", AccessLevels.PUBLIC);
            beaconFieldAccessLevels.put("alternativeUrl", AccessLevels.PUBLIC);
            beaconFieldAccessLevels.put("createDateTime", AccessLevels.PUBLIC);
            beaconFieldAccessLevels.put("updateDateTime", AccessLevels.PUBLIC);
            beaconFieldAccessLevels.put("datasets", AccessLevels.PUBLIC);
            beaconFieldAccessLevels.put("sampleAlleleRequests", AccessLevels.PUBLIC);
            beaconFieldAccessLevels.put("info", AccessLevels.PUBLIC);
            accessLevels.put("beacon", beaconFieldAccessLevels);

            Map<String, String> beaconOrganizationFieldAccessLevels = new HashMap<>();
            beaconOrganizationFieldAccessLevels.put("accessLevelSummary", AccessLevels.PUBLIC);
            beaconOrganizationFieldAccessLevels.put("id", AccessLevels.PUBLIC);
            beaconOrganizationFieldAccessLevels.put("name", AccessLevels.PUBLIC);
            beaconOrganizationFieldAccessLevels.put("description", AccessLevels.PUBLIC);
            beaconOrganizationFieldAccessLevels.put("address", AccessLevels.PUBLIC);
            beaconOrganizationFieldAccessLevels.put("welcomeUrl", AccessLevels.PUBLIC);
            beaconOrganizationFieldAccessLevels.put("contactUrl", AccessLevels.PUBLIC);
            beaconOrganizationFieldAccessLevels.put("logoUrl", AccessLevels.PUBLIC);
            beaconOrganizationFieldAccessLevels.put("info", AccessLevels.PUBLIC);
            accessLevels.put("beaconOrganization", beaconOrganizationFieldAccessLevels);

            Map<String, String> beaconDatasetFieldAccessLevels = new HashMap<>();
            beaconDatasetFieldAccessLevels.put("accessLevelSummary", AccessLevels.PUBLIC);
            beaconDatasetFieldAccessLevels.put("id", AccessLevels.PUBLIC);
            beaconDatasetFieldAccessLevels.put("name", AccessLevels.PUBLIC);
            beaconDatasetFieldAccessLevels.put("description", AccessLevels.PUBLIC);
            beaconDatasetFieldAccessLevels.put("assemblyId", AccessLevels.PUBLIC);
            beaconDatasetFieldAccessLevels.put("createDateTime", AccessLevels.PUBLIC);
            beaconDatasetFieldAccessLevels.put("updateDateTime", AccessLevels.PUBLIC);
            beaconDatasetFieldAccessLevels.put("version", AccessLevels.PUBLIC);
            beaconDatasetFieldAccessLevels.put("variantCount", AccessLevels.PUBLIC);
            beaconDatasetFieldAccessLevels.put("callCount", AccessLevels.PUBLIC);
            beaconDatasetFieldAccessLevels.put("sampleCount", AccessLevels.PUBLIC);
            beaconDatasetFieldAccessLevels.put("externalUrl", AccessLevels.PUBLIC);
            beaconDatasetFieldAccessLevels.put("info", AccessLevels.PUBLIC);
            beaconDatasetFieldAccessLevels.put("dataUseConditions", AccessLevels.PUBLIC);
            accessLevels.put("beaconDataset", beaconDatasetFieldAccessLevels);

            Map<String, String> beaconAlleleRequestFieldAccessLevels = new HashMap<>();
            beaconAlleleRequestFieldAccessLevels.put("accessLevelSummary", AccessLevels.PUBLIC);
            beaconAlleleRequestFieldAccessLevels.put("referenceName", AccessLevels.PUBLIC);
            beaconAlleleRequestFieldAccessLevels.put("start", AccessLevels.PUBLIC);
            beaconAlleleRequestFieldAccessLevels.put("end", AccessLevels.NOT_SUPPORTED);
            beaconAlleleRequestFieldAccessLevels.put("startMin", AccessLevels.NOT_SUPPORTED);
            beaconAlleleRequestFieldAccessLevels.put("startMax", AccessLevels.NOT_SUPPORTED);
            beaconAlleleRequestFieldAccessLevels.put("endMin", AccessLevels.NOT_SUPPORTED);
            beaconAlleleRequestFieldAccessLevels.put("endMax", AccessLevels.NOT_SUPPORTED);
            beaconAlleleRequestFieldAccessLevels.put("referenceBases", AccessLevels.PUBLIC);
            beaconAlleleRequestFieldAccessLevels.put("alternateBases", AccessLevels.PUBLIC);
            beaconAlleleRequestFieldAccessLevels.put("variantType", AccessLevels.NOT_SUPPORTED);
            beaconAlleleRequestFieldAccessLevels.put("assemblyId", AccessLevels.PUBLIC);
            beaconAlleleRequestFieldAccessLevels.put("datasetIds", AccessLevels.PUBLIC);
            beaconAlleleRequestFieldAccessLevels.put("includeDatasetResponses", AccessLevels.PUBLIC);
            accessLevels.put("beaconAlleleRequest", beaconAlleleRequestFieldAccessLevels);

            Map<String, String> beaconAlleleResponseFieldAccessLevels = new HashMap<>();
            beaconAlleleResponseFieldAccessLevels.put("accessLevelSummary", AccessLevels.PUBLIC);
            beaconAlleleResponseFieldAccessLevels.put("beaconId", AccessLevels.PUBLIC);
            beaconAlleleResponseFieldAccessLevels.put("apiVersion", AccessLevels.PUBLIC);
            beaconAlleleResponseFieldAccessLevels.put("exists", AccessLevels.PUBLIC);
            beaconAlleleResponseFieldAccessLevels.put("error", AccessLevels.PUBLIC);
            beaconAlleleResponseFieldAccessLevels.put("info", AccessLevels.PUBLIC);
            beaconAlleleResponseFieldAccessLevels.put("datasetAlleleResponses", AccessLevels.PUBLIC);
            beaconAlleleResponseFieldAccessLevels.put("alleleRequest", AccessLevels.PUBLIC);
            accessLevels.put("beaconAlleleResponse", beaconAlleleResponseFieldAccessLevels);

            Map<String, String> datasetAlleleResponsesFieldAccessLevels = new HashMap<>();
            datasetAlleleResponsesFieldAccessLevels.put("accessLevelSummary", AccessLevels.PUBLIC);
            datasetAlleleResponsesFieldAccessLevels.put("datasetId", AccessLevels.PUBLIC);
            datasetAlleleResponsesFieldAccessLevels.put("exists", AccessLevels.PUBLIC);
            datasetAlleleResponsesFieldAccessLevels.put("error", AccessLevels.PUBLIC);
            datasetAlleleResponsesFieldAccessLevels.put("frequency", AccessLevels.PUBLIC);
            datasetAlleleResponsesFieldAccessLevels.put("variantCount", AccessLevels.PUBLIC);
            datasetAlleleResponsesFieldAccessLevels.put("callCount", AccessLevels.PUBLIC);
            datasetAlleleResponsesFieldAccessLevels.put("sampleCount", AccessLevels.PUBLIC);
            datasetAlleleResponsesFieldAccessLevels.put("note", AccessLevels.PUBLIC);
            datasetAlleleResponsesFieldAccessLevels.put("externalUrl", AccessLevels.PUBLIC);
            datasetAlleleResponsesFieldAccessLevels.put("info", AccessLevels.PUBLIC);
            accessLevels.put("datasetAlleleResponses", datasetAlleleResponsesFieldAccessLevels);

            Map<String, String> beaconErrorFieldAccessLevels = new HashMap<>();
            beaconErrorFieldAccessLevels.put("accessLevelSummary", AccessLevels.PUBLIC);
            beaconErrorFieldAccessLevels.put("errorCode", AccessLevels.PUBLIC);
            beaconErrorFieldAccessLevels.put("errorMessage", AccessLevels.PUBLIC);
            accessLevels.put("beaconErrorField", beaconErrorFieldAccessLevels);

            Map<String, String> dataUseConditionsFieldAccessLevels = new HashMap<>();
            dataUseConditionsFieldAccessLevels.put("accessLevelSummary", AccessLevels.NOT_SUPPORTED);
            dataUseConditionsFieldAccessLevels.put("consentCodeDataUse", AccessLevels.NOT_SUPPORTED);
            dataUseConditionsFieldAccessLevels.put("adamDataUse", AccessLevels.NOT_SUPPORTED);
            accessLevels.put("dataUseConditions", dataUseConditionsFieldAccessLevels);
        } else {
            accessLevels.put("beacon", AccessLevels.PUBLIC);
            accessLevels.put("beaconOrganization", AccessLevels.PUBLIC);
            accessLevels.put("beaconError", AccessLevels.PUBLIC);
            accessLevels.put("beaconAlleleResponse", AccessLevels.PUBLIC);
            accessLevels.put("beaconAlleleRequest", AccessLevels.PUBLIC);
            accessLevels.put("beaconDataset", AccessLevels.PUBLIC);
            accessLevels.put("datasetAlleleResponses", AccessLevels.PUBLIC);
            accessLevels.put("dataUseConditions", AccessLevels.PUBLIC);
        }
        if (ListUtils.isNotEmpty(fields)) {
            for (String field : fields) {
                if (!accessLevels.containsKey(field)) {
                    accessLevels.put(field, AccessLevels.NOT_SUPPORTED);
                }
            }
            List<String> toRemove = new ArrayList<>();
            for (String accessLevelField : accessLevels.keySet()) {
                if (!fields.contains(accessLevelField)) {
                    toRemove.add(accessLevelField);
                }
            }
            for (String accessLevelField : toRemove) {
                accessLevels.remove(accessLevelField);
            }
        }

        return accessLevels;
    }

    private static class AccessLevels {
        public static final String PUBLIC = "PUBLIC";
        public static final String REGISTERED = "REGISTERED";
        public static final String NOT_SUPPORTED = "NOT_SUPPORTED";
    }
}
