package org.ega_archive.elixirbeacon.dto;

import java.util.List;

import org.ega_archive.elixirbeacon.enums.FilterDatasetResponse;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Allele request as interpreted by the beacon.
 */
@JsonPropertyOrder({"referenceName", "start", "startMin",
    "startMax", "end", "endMin", "endMax", "referenceBases", "alternateBases", "variantType", "assemblyId", "datasetIds",
    "includeDatasetResponses"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeaconPlantRequest {

  /**
   * The bases that appear instead of the reference bases. Accepted values: [ACGT]* or N. <br>
   * Symbolic ALT alleles (DEL, INS, DUP, INV, CNV, DUP:TANDEM, DEL:ME, INS:ME) will be represented
   * in {@code variantType}. <br> Optional: either {@code alternateBases} or {@code variantType} is
   * required.
   */
  private String alternateBases;

  /**
   * Reference bases for this variant (starting from start). Accepted values: [ACGT]* <br> When
   * querying for variants without specific base alterations (e.g. imprecise structural variants
   * with separate variant_type as well as start_min & end_min ... parameters), the use of a single
   * "N" value is required.
   */
  private String referenceBases;

  /**
   * Chromosome identifier. Accepted values: 1-22, X, Y
   */
  private String referenceName;

  /**
   * Precise start coordinate position, allele locus (0-based, inclusive). <br>
   *  <ul>
   *   <li>start only: <br> for single positions, e.g. the start of a specified sequence alteration
   *   where the size is given through the specified {@code alternateBases} typical use are queries
   *   for SNV and small InDels the use of {@code start} without an {@code end} parameter requires
   *   the use of {@code referenceBases} </li>
   *   <li>start and end: <br> special use case for exactly determined structural changes</li>
   * </ul>
   */
  private Integer start;

  /**
   * Minimum start coordinate <br>
   *   <ul>
   *   <li>startMin + startMax + endMin + endMax: for querying imprecise positions (e.g. identifying all
   *   structural variants starting anywhere between {@code startMin <-> startMax}, and ending
   *   anywhere between {@code endMin <-> endMax} </li>
   *   <li>single or double sided precise matches can be achieved by setting {@code startMin = startMax
   *   XOR endMin = endMax} </li>
   *   </ul>
   * @see BeaconAlleleRequest#getStart()
   */
  private Integer startMin;

  /**
   * Maximum start coordinate.
   * @see BeaconAlleleRequest#getStartMin()
   */
  private Integer startMax;

  /**
   * Precise end coordinate (0-based, exclusive).
   * @see BeaconAlleleRequest#getStart()
   */
  private Integer end;

  /**
   * Minimum end coordinate.
   * @see BeaconAlleleRequest#getStartMin()
   */
  private Integer endMin;

  /**
   * Maximum end coordinate.
   * @see BeaconAlleleRequest#getStartMin()
   */
  private Integer endMax;

  /**
   * The "variant_type" is used to denote e.g. structural variants. Examples:
   * <ul>
   *   <li>DUP : duplication of sequence following "start"; not necessarily in situ </li>
   *   <li>DEL : deletion of sequence following "start" </li>
   * </ul>
   * Optional (either {@code alternate_bases} or {@code variant_type} is required)
   * @see BeaconAlleleRequest#getAlternateBases()
   */
  private String variantType;

  /**
   * Assembly identifier (GRC notation, e.g. `GRCh37`).
   */
  private String assemblyId;

  /**
   * Identifiers of datasets, as defined in `BeaconDataset`. If this field is null/not specified,
   * all datasets should be queried.
   */
  private List<String> datasetIds;

  /**
   * Indicator of whether responses for individual datasets ({@code datasetAlleleResponses}) should be
   * included in the response ({@code BeaconAlleleResponse}) to this request or not.
   */
  private FilterDatasetResponse includeDatasetResponses;

  /**
   * Contains the metadata JSON string
   * this is a test while checking if it works in the original Request/Response pair.
   * Once done, I'll copy it into a separate Request and figure out how to call different requests depending on need.
   */
  private String info; // MODIFIED

  private String test;

}
