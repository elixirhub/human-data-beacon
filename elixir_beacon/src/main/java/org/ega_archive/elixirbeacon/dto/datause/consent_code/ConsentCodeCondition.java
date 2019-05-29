package org.ega_archive.elixirbeacon.dto.datause.consent_code;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.convert.Operations;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsentCodeCondition {

  @JsonIgnore
  private String[] fields = {"code", "description", "additionalConstraint"};

  private String code;

  private String description;

  private String additionalConstraint;

  public Map<String, Object> toMap(Map<String, Object> accessLevelFields, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "consentCodeCategory", accessLevelFields, isAuthenticated);
  }

}
