package org.ega_archive.elixirbeacon.dto.datause;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.convert.Operations;
import org.ega_archive.elixirbeacon.dto.datause.consent_code.ConsentCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataUseCondition {

  @JsonIgnore
  private String[] fields = {"consentCodeDataUse"};

  private ConsentCode consentCodeDataUse;

  @SuppressWarnings("unchecked")
  public Map<String, Object> toMap(Map<String, Object> accessLevelFields, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "dataUseConditions", accessLevelFields, isAuthenticated);
  }

}
