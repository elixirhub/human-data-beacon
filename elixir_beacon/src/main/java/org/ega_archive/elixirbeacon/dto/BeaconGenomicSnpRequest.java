package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.enums.FilterDatasetResponse;

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
public class BeaconGenomicSnpRequest {

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

  private List<String> filters;

}
