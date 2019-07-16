package org.ega_archive.elixircore.dto.auth;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "postgres_submitter_user")
public class PostgresSubmitterUser {

  @XmlElement(name = "dir_name")
  private String dirName;

  @XmlElement(name = "audit_osuser")
  private String auditOsuser;

  @XmlElement(name = "audit_time")
  private Timestamp auditTime;

  @XmlElement(name = "audit_user")
  private String auditUser;

  @XmlElement(name = "center_name")
  private String centerName;

  @XmlElement(name = "submission_account_id")
  private String submissionAccountId;

  @XmlElement(name = "password")
  private String password;

}
