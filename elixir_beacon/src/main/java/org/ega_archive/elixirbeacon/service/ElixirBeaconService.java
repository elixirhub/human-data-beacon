package org.ega_archive.elixirbeacon.service;

import java.util.List;

import org.ega_archive.elixirbeacon.dto.Beacon;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleResponse;
import org.ega_archive.elixirbeacon.dto.BeaconRequest;
import org.ega_archive.elixirbeacon.enums.VariantType;
import org.ega_archive.elixircore.helper.CommonQuery;

import org.ega_archive.elixirbeacon.dto.BeaconPlantResponse;

import javassist.NotFoundException;

public interface ElixirBeaconService {

  /**
   * Returns the information about this beacon implementation and all datasets. It also specifies
   * the access type for each dataset:
   * <ul>
   * <li>PUBLIC: all.</li>
   * <li>REGISTERED: if the user is authenticated.</li>
   * <li>PROTECTED: if the user is authorized to access it.</li>
   *
   * @param commonQuery
   * @param referenceGenome
   * @return
   * @throws NotFoundException
   */
  public Beacon listDatasets(CommonQuery commonQuery, String referenceGenome) throws NotFoundException;

  /**
   * Executes the query against the beacon and basically answers yes or no.
   *
   * @param start
   * @param startMin
   * @param startMax
   * @param end
   * @param endMin
   * @param endMax
   * @param chromosome
   * @param referenceBases
   * @param alternateBases
   * @param datasetStableIds
   * @param referenceGenome
   * @param includeDatasetResponses
   * @return
   */
  public BeaconAlleleResponse queryBeacon(List<String> datasetStableIds, String variantType,
      String alternateBases, String referenceBases, String chromosome, Integer start,
      Integer startMin, Integer startMax, Integer end, Integer endMin, Integer endMax,
      String referenceGenome, String includeDatasetResponses);

  /**
   * Verifies that mandatory parameters are present and that all parameters are valid.
   *
   * @param result
   * @param datasetStableIds
   * @param type
   * @param alternateBases
   * @param referenceBases
   * @param chromosome
   * @param start
   * @param referenceGenome
   * @return
   */
  public List<Integer> checkParams(BeaconAlleleResponse result, List<String> datasetStableIds,
      VariantType type, String alternateBases, String referenceBases, String chromosome,
      Integer start, Integer startMin, Integer startMax, Integer end, Integer endMin,
      Integer endMax, String referenceGenome);

  public BeaconAlleleResponse queryBeacon(BeaconRequest request);





  // Plant Beacon Reuest/Response supertype elements:


  /**
   * Executes the Plant query against the beacon and answers yes or no.
   *
   * @param start
   * @param startMin
   * @param startMax
   * @param end
   * @param endMin
   * @param endMax
   * @param chromosome
   * @param referenceBases
   * @param alternateBases
   * @param datasetStableIds
   * @param referenceGenome
   *
   * @param puid
   * @param accenumb
   * @param ancest
   * @param cropname
   * @param sampletype
   * @param tissue
   * @param age
   *
   * @param includeDatasetResponses
   * @return
   */
  public BeaconPlantResponse queryPlantBeacon(List<String> datasetStableIds, String variantType,
      String alternateBases, String referenceBases, String chromosome, Integer start,
      Integer startMin, Integer startMax, Integer end, Integer endMin, Integer endMax,
      String referenceGenome, String puid, String accenumb, String ancest,
      String cropname, String sampletype, String tissue, String age,
      String includeDatasetResponses);

  /**
   * Verifies that mandatory parameters are present and that all parameters are valid.
   *
   * @param result
   * @param datasetStableIds
   * @param type
   * @param alternateBases
   * @param referenceBases
   * @param chromosome
   * @param start
   * @param referenceGenome
   *
   * @param puid
   * @param accenumb
   * @param ancest
   * @param cropname
   * @param sampletype
   * @param tissue
   * @param age
   *
   * @return
   */
  public List<Integer> checkPlantParams(BeaconPlantResponse result, List<String> datasetStableIds,
      VariantType type, String alternateBases, String referenceBases, String chromosome,
      Integer start, Integer startMin, Integer startMax, Integer end, Integer endMin,
      Integer endMax, String puid, String accenumb, String ancest,
      String cropname, String sampletype, String tissue, String age,
      String referenceGenome);

  public BeaconPlantResponse queryPlantBeacon(BeaconRequest request);

}
