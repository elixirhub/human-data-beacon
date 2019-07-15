package org.ega_archive.elixirbeacon.dto.datause.consent_code;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Data use of a resource based on consent codes.
 *
 */
@Data
@Builder
@AllArgsConstructor
public class ConsentCode { 
  
  // Primary data use category.
  private ConsentCodeCondition primaryCategory;
  
  //  Secondary data use categories.
  private List<ConsentCodeCondition> secondaryCategories;
  
  // Data use requirements.
  private List<ConsentCodeCondition> requirements;
  
  // Version of the data use specification
  private String version;
  
  public ConsentCode() {
    this.primaryCategory = null;
    this.secondaryCategories = new ArrayList<>();
    this.requirements = new ArrayList<>();
  }
  
  public void addSecondaryCategory(ConsentCodeCondition condition) {
    if(secondaryCategories == null) {
      secondaryCategories = new ArrayList<ConsentCodeCondition>();
    }
    secondaryCategories.add(condition);
  }
  
  public void addRequirement(ConsentCodeCondition condition) {
    if(requirements == null) {
      requirements = new ArrayList<ConsentCodeCondition>();
    }
    requirements.add(condition);
  }
  
}
