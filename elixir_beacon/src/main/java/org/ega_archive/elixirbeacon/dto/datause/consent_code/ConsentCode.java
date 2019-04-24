package org.ega_archive.elixirbeacon.dto.datause.consent_code;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.ega_archive.elixirbeacon.convert.Operations;

@Data
@Builder
@AllArgsConstructor
public class ConsentCode {

  @JsonIgnore
  private String[] fields = {"primaryCategory", "secondaryCategories", "requirements", "version"};

  private ConsentCodeCondition primaryCategory;

  private List<ConsentCodeCondition> secondaryCategories;

  private List<ConsentCodeCondition> requirements;

  private String version;

  public ConsentCode() {
    this.primaryCategory = null;
    this.secondaryCategories = new ArrayList<>();
    this.requirements = new ArrayList<>();
  }

  public void addSecondaryCategory(ConsentCodeCondition condition) {
    if (secondaryCategories == null) {
      secondaryCategories = new ArrayList<ConsentCodeCondition>();
    }
    secondaryCategories.add(condition);
  }

  public void addRequirement(ConsentCodeCondition condition) {
    if (requirements == null) {
      requirements = new ArrayList<ConsentCodeCondition>();
    }
    requirements.add(condition);
  }

  public Map<String, Object> toMap(Map<String, Object> accessLevelFields, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "consentCodeDataUse", accessLevelFields, isAuthenticated);
  }

}
