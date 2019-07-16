package org.ega_archive.elixirbeacon.dto.datause;

import org.ega_archive.elixirbeacon.dto.datause.consent_code.ConsentCode;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataUseCondition {

  private ConsentCode consentCodedataUse;
  
}
