package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.ega_archive.elixircore.constant.CoreConstants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatasetPlantResponse {

  // Identifier of the dataset, as defined in `BeaconDataset`
  private String datasetId;

  // Indicator of whether the given allele was observed in the dataset. This should be non-null,
  // unless there was an error, in which case `error` has to be not null.
  private boolean exists;

  // Dataset-specific error. This should be non-null in exceptional situations only, in which case
  // `exists` has to be null.
  private Error error;

  // Frequency of this allele in the dataset. Between 0 and 1, inclusive.
  private BigDecimal frequency;

  // Number of variants matching the allele request in the dataset.
  private BigInteger variantCount;

  // Number of calls matching the allele request in the dataset.
  private BigInteger callCount;

  // Number of samples matching the allele request in the dataset.
  private BigInteger sampleCount;

  // Additional note or description of the response.
  private String note = CoreConstants.OK;

  // URL to an external system, such as a secured beacon or a system providing
  // more information about a given allele (RFC 3986 format).
  private String externalUrl;

  // Additional structured metadata, Object
  //private List<KeyValuePair> info;
  private Map<String,Object> info;

  // MCPD V2.1 PUID.  a persistent, unique identifier assigned to an accession. (http://www.fao.org/plant-treaty/areas-of-work/global-information-system/doi/en/)
  // The return value is a boolean indicating if the dataset has the correct PUID.
  private boolean puid;

  // MCPD v2.1 ACCENUMB. the identifier given when the dataset is added to Genebank.
  // The return value is a boolean indicating if the dataset has the correct Accession Number.
  private boolean accenumb;

  // MCPD V2.1 ANCEST. Ancestral data, like the pedigree or parental varieties of the plant studied.
  // The return value is a boolean indicating if the searched for terms were present in the Ancestral Data field of the dataset.
  // AKA., was variety term provided by the user (ex.: "Hanna") in the dataset's ANCEST field (ex.: "Hanna/7*Atlas//Turk/8*Atlas")
  private boolean ancest;

  // MCPD V2.1 CROPNAME. common name of the crop, like "malting Barley", "Macademia"...
  // Boolean value indicating if the common name is present in the dataset's cropname field.
  // check does not depend on character case. AKA., "Macademia" == "macademia"
  private boolean cropname;

  // BioSample parameters

  // BioSample Sample Type (cell culture, mixed culture, tissue sample, whole organism, single cell or metagenomic assembly)
  // Boolean indicating if the sample type asked for (provided as list to user to choose from) is the same as the datasets.
  private boolean sampletype;


  // BioSample Tissue (Type of tissue the sample was taken from)
  // Boolean indicating if the tissue type is the same as the one in the dataset.
  private boolean tissue;

  // BioSample Age (At time of sampling)
  // Boolean indicating if the age is the same. format of both fields in dataset and web is "<value> <unit>"
  private boolean age;
  
}
