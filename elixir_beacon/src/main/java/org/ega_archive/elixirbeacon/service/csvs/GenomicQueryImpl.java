package org.ega_archive.elixirbeacon.service.csvs;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.babelomics.csvs.lib.models.Variant;
import org.babelomics.csvs.lib.ws.QueryResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpResponse;
import org.ega_archive.elixirbeacon.service.GenomicQuery;
import org.ega_archive.elixircore.dto.Base;
import org.ega_archive.elixircore.event.sender.RestEventSender;
import org.ega_archive.elixircore.exception.RestRuntimeException;
import org.ega_archive.elixircore.exception.ServerDownException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class GenomicQueryImpl implements GenomicQuery {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private RestEventSender restEventSender;

  @Override
  public BeaconGenomicSnpResponse queryBeaconGenomicSnp(List<String> datasetStableIds,
      String alternateBases, String referenceBases, String chromosome, Integer start,
      String referenceGenome, String includeDatasetResponses, List<String> filters) {

    // TODO: check referenceGenome is GRCh37
    // TODO: Add new endpoint to CSVS to return variants by dataset and use it here

    String technologyFilter = null;
    if (null != filters) {

      String technologyFilterValues = filters.stream()
          .filter(filter -> filter.startsWith("myDictionary"))
          .map(filter -> filter.split(":", 2)[1]).collect(Collectors.joining(","));
      technologyFilter =
          StringUtils.isNotBlank(technologyFilterValues) ? "&technologies=" + technologyFilterValues
              : null;
    }

    String url = "http://csvs.clinbioinfosspa.es:8080/csvs/rest/variants/fetch?regions=";
    url = url + chromosome + ":" + start + "-" + (start
        + 1); // + referenceBases + ":" + alternateBases + "/get";

    if (datasetStableIds != null && !datasetStableIds.isEmpty()) {
      url += "&diseases=" + String.join(",", datasetStableIds);
    }
    if (StringUtils.isNotBlank(technologyFilter)) {
      url += technologyFilter;
    }

    log.debug("url {}", url);

    ResponseEntity response = null;

    try {
      response = this.restTemplate
          .exchange(url, HttpMethod.GET, new HttpEntity(null, null), String.class, new Object[0]);
    } catch (ResourceAccessException var15) {
      throw new ServerDownException(var15.getMessage());
    }

    JavaType type = this.objectMapper.getTypeFactory()
        .constructParametricType(QueryResponse.class, new Class[]{Variant.class});
    QueryResponse basicDTO = null;

    try {
      basicDTO = (QueryResponse) this.objectMapper.readValue((String) response.getBody(), type);
//      return basicDTO;
    } catch (IOException var14) {
      throw new RestRuntimeException("500",
          "Exception deserializing object: " + (String) response.getBody() + "\n" + var14
              .getMessage());
    }
    log.debug("response: {}", basicDTO);
    BeaconGenomicSnpResponse beaconGenomicSnpResponse = new BeaconGenomicSnpResponse();
    boolean exists = basicDTO.getNumTotalResults() > 0 && basicDTO.getResult() != null
        && basicDTO.getResult().get(0) != null;
    boolean finalExist = false;
    if (exists) {
      List<Variant> result = basicDTO.getResult();
      for (Variant variant : result) {
        if (StringUtils.equalsIgnoreCase(variant.getReference(), referenceBases)
            && StringUtils.equalsIgnoreCase(variant.getAlternate(), alternateBases)) {
          finalExist = true;

          break;
        }
      }
    }
    beaconGenomicSnpResponse.setExists(finalExist);
    return beaconGenomicSnpResponse;
  }

}
