package org.ega_archive.elixirbeacon.service;

import java.util.List;

public interface AuthService {

  String getAuthorizationHeader();

  List<String> findAuthorizedDatasets(String authorizationHeader);

  List<Integer> checkDatasets(List<String> datasetStableIds, String referenceGenome);

}
