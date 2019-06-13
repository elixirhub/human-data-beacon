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
   * Serves as a way for the user to see the full MCPD and BioSample metadata of the returned datasets.
   */
  private String info;

  /**
   * MCPD V2.1 PUID.  a persistent, unique identifier assigned to an accession. (http://www.fao.org/plant-treaty/areas-of-work/global-information-system/doi/en/)
   * The request value is a String containing the PUID of the dataset the user wants to query against.
   */
  private String puid;

  /**
   * MCPD v2.1 ACCENUMB. the identifier given when the dataset is added to Genebank.
   * The request value is a String containing the Accession Number of the dataset the user wants.
   */
  private String accenumb;

  /**
   * MCPD V2.1 ANCEST. Ancestral data, like the pedigree or parental varieties of the plant studied.
   * The request value is a String containing the ancestral information the user wants to query for.
   * Ex: "Hanna", "Hanna/7*Atlas//Turk/8*Atlas"
   */
  private String ancest;

  /**
   * MCPD V2.1 CROPNAME. common name of the crop, like "malting Barley", "Macademia"...
   * Request value is the common crop name the user is looking to filter for.
   * check does not depend on character case. AKA., "Macademia" == "macademia"
   */
  private String cropname;

  // BioSample parameters

  /**
   * BioSample Sample Type (cell culture, mixed culture, tissue sample, whole organism, single cell or metagenomic assembly)
   * String with the sample type the user wants their datasets to have (provided as list for user to choose from).
   */
  private String sampletype;


  /**
   * BioSample Tissue (Type of tissue the sample was taken from)
   * String of the tissue typethe user wants (leaves, root, ...)
   */
  private String tissue;

  /**
   * BioSample Age (At time of sampling)
   * String giving the age the user wants their samples to be older/younger than.
   * The Plant Request then returns a boolean indicating if the parameter "<=" / ">=" to the Age of the given Unit.
   * Another check consistenly returns False if the dataset's age Unit (years, days, hours...) is inconsistent with the one given by the user.
   */
  private String age;

}
