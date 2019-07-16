package org.ega_archive.elixirbeacon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixirbeacon.model.elixirbeacon.BeaconDataset;
import org.ega_archive.elixirbeacon.repository.elixirbeacon.BeaconDatasetRepository;
import org.ega_archive.elixircore.enums.DatasetAccessType;
import org.ega_archive.elixircore.exception.NotFoundException;
import org.ega_archive.elixircore.exception.PreConditionFailed;
import org.ega_archive.elixircore.exception.RestRuntimeException;
import org.ega_archive.elixircore.exception.ServiceUnavailableException;
import org.ega_archive.elixircore.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

  private static final String AUTHORIZATION = "Authorization";

  @Value("${elixir.userinfo.url}")
  private String USER_INFO_URL;

  @Autowired
  private HttpServletRequest incomingRequest;

  @Autowired
  private BeaconDatasetRepository beaconDatasetRepository;

  @Override
  public String getAuthorizationHeader() {
    return incomingRequest.getHeader(AUTHORIZATION);
  }

  @Override
  public List<String> findAuthorizedDatasets(String authorizationHeader) {
    if (StringUtils.isNotBlank(authorizationHeader)) {
      return findAuthorizedDatasets(authorizationHeader, null);
    }
    return new ArrayList<>();
  }

  private List<String> findAuthorizedDatasets(String authorizationHeader, String stableId) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(AUTHORIZATION, authorizationHeader);

    String url = USER_INFO_URL;

    List<String> datasetStableIdsList = null;
    try {
      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
          new HttpEntity<>(null, headers), String.class);

      // Parse the response
      //noinspection unchecked
      Map<String,Object> result = new ObjectMapper().readValue(response.getBody(), HashMap.class);
      //noinspection unchecked
      Map<String, Object> ga4gh = (Map<String, Object>) result.get("ga4gh");
      //noinspection unchecked
      List<Map<String, Object>> controlledAccessGrants = (List<Map<String, Object>>) ga4gh.get("ControlledAccessGrants");
      datasetStableIdsList = controlledAccessGrants.parallelStream()
          .map(entry -> entry.get("value"))
          .map(val -> ((String) val).replaceFirst("https://www.ebi.ac.uk/ega/", ""))
          .collect(Collectors.toList());

    } catch(HttpServerErrorException e) {
      log.error("Service unavailable: {}", e);
      throw new ServiceUnavailableException("Service unavailable");
    } catch(HttpClientErrorException e) {
      String msg = "Token is not valid or user is not authorized to access this dataset";
      log.error(msg + ": {}", e);
      throw new UnauthorizedException(msg);
    } catch (IOException e) {
      String msg = "Unexpected exception: ";
      log.error(msg + "{}", e);
      throw new RestRuntimeException("500", msg + e.getLocalizedMessage());
    }

    if (StringUtils.isNotBlank(stableId) && datasetStableIdsList != null
        && !datasetStableIdsList.contains(stableId)) {
      throw new UnauthorizedException("User is not authorized to access this dataset: " + stableId);
    }

    return datasetStableIdsList;
  }

  @Override
  public List<Integer> checkDatasets(List<String> datasetStableIds, String referenceGenome) {

    List<Integer> datasetIds = new ArrayList<>();

    boolean isAuthenticated = false;
    String authorizationHeader = getAuthorizationHeader();
    if (StringUtils.isNotBlank(authorizationHeader)) {
      isAuthenticated = true;
    }

    if (datasetStableIds != null) {
      // Remove empty/null strings
      datasetStableIds = datasetStableIds
          .stream()
          .filter(id -> StringUtils.isNotBlank(id))
          .collect(Collectors.toList());
    }

    if (datasetStableIds != null && !datasetStableIds.isEmpty()) {
      // Filtering by dataset
      for (String datasetStableId : datasetStableIds) {
        // 1) Dataset exists
        BeaconDataset dataset = beaconDatasetRepository.findByStableId(datasetStableId);
        if (dataset == null) {
          throw new NotFoundException("Dataset not found", datasetStableId);
        }

        DatasetAccessType datasetAccessType = DatasetAccessType.parse(dataset.getAccessType());
        if (isAuthenticated && datasetAccessType == DatasetAccessType.CONTROLLED) {
          // 2) Check that user is authorized to access it
          findAuthorizedDatasets(authorizationHeader, datasetStableId);

        } else if (!isAuthenticated && datasetAccessType != DatasetAccessType.PUBLIC) {
          throw new UnauthorizedException("Unauthenticated users cannot access this dataset");
        }
        // 3) Check that the provided reference genome matches the one specified in the DB for this
        // dataset
        if (!StringUtils.equalsIgnoreCase(dataset.getReferenceGenome(), referenceGenome)) {
          throw new PreConditionFailed(
              "The reference genome of this dataset does not match the provided value");
        }
        datasetIds.add(dataset.getId());
      }
    } else { // No filtering by dataset -> get the list
      if (isAuthenticated) {
        // Retrieve the list of authorized datasets
        List<String> authorizedDatasets = findAuthorizedDatasets(authorizationHeader, null);
        datasetIds = beaconDatasetRepository
            .findIdsByStableIdInAndReferenceGenome(authorizedDatasets, referenceGenome);
        // Add the registered
        datasetIds.addAll(beaconDatasetRepository
            .findIdsByReferenceGenomeAndAccessType(referenceGenome,
                DatasetAccessType.REGISTERED.getType()));
      }
      // If user is authenticated or not, add the PUBLIC datasets
      datasetIds.addAll(beaconDatasetRepository
          .findIdsByReferenceGenomeAndAccessType(referenceGenome,
              DatasetAccessType.PUBLIC.getType()));
    }
    return datasetIds;
  }

}
