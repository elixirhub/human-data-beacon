package org.ega_archive.elixirbeacon.dto.datause.consent_code;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsentCodeCondition {

  /**
   * <p>Consent code abbreviation.</p>
   * Primary data use categories:
   * <ul>
   * <li>NRES: no restrictions - No restrictions on data use.</li>
   * <li>GRU(CC): general research use and clinical care - For health/medical/biomedical purposes
   * and other biological research, including the study of population origins or ancestry.</li>
   * <li>HMB(CC): health/medical/biomedical research and clinical care - Use of the data is limited
   * to health/medical/biomedical purposes, does not include the study of population origins or
   * ancestry.</li>
   * <li>DS-[XX](CC): disease-specific research and clinical care - Use of the data must be related
   * to [disease].</li>
   * <li>POA: population origins/ancestry research - Use of the data is limited to the study of
   * population origins or ancestry.</li>
   * </ul>
   * Secondary data use categories:
   * <ul>
   * <li>RS-[XX]: other research-specific restrictions - Use of the data is limited to studies of
   * [research type] (e.g., pediatric research).</li>
   * <li>RUO: research use only - Use of data is limited to research purposes (e.g., does not
   * include its use in clinical care).</li>
   * <li>NMDS: no “general methods” research - Use of the data includes methods development research
   * (e.g., development of software or algorithms) ONLY within the bounds of other data use
   * limitations.</li>
   * <li>GSO: genetic studies only - Use of the data is limited to genetic studies only (i.e., no
   * research using only the phenotype data).</li>
   * </ul>
   * Data use requirements:
   * <ul>
   * <li>NPU: not-for-profit use only - Use of the data is limited to not-for-profit
   * organizations.</li>
   * <li>PUB: publication required - Requestor agrees to make results of studies using the data
   * available to the larger scientific community.</li>
   * <li>COL-[XX]: collaboration required - Requestor must agree to collaboration with the primary
   * study investigator(s).</li>
   * <li>RTN: return data to database/resource - Requestor must return derived/enriched data to the
   * database/resource.</li>
   * <li>IRB: ethics approval required - Requestor must provide documentation of local IRB/REC
   * approval.</li>
   * <li>GS-[XX]: geographical restrictions - Use of the data is limited to within [geographic
   * region].</li>
   * <li>MOR-[XX]: publication moratorium/embargo - Requestor agrees not to publish results of
   * studies until [date].</li>
   * <li>TS-[XX]: time limits on use - Use of data is approved for [x months].</li>
   * <li>US: user-specific restrictions - Use of data is limited to use by approved users.</li>
   * <li>PS: project-specific restrictions - Use of data is limited to use within an approved
   * project.</li>
   * <li>IS: institution-specific restrictions - Use of data is limited to use within an approved
   * institution.</li>
   * </ul>
   */
  private String code;

  /**
   * Description of the condition.
   */
  private String description;

  /**
   * Additional constraint on the condition (e.g. 12 months in TS-[XX])
   */
  private String additionalConstraint;

}
