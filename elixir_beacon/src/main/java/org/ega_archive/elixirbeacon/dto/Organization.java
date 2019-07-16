package org.ega_archive.elixirbeacon.dto;

import java.util.Map;

import org.ega_archive.elixirbeacon.constant.BeaconConstants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Organization {

  // Unique identifier of the organization. Use reverse domain name notation
  // (e.g. org.ga4gh)
  private String id = BeaconConstants.ORGANIZATION_ID;

  // Name of the organization
  private String name = BeaconConstants.ORGANIZATION_NAME;;

  // Description of the organization
  private String description = BeaconConstants.ORGANIZATION_DESCRIPTION;

  // Address of the organization
  private String address = BeaconConstants.ORGANIZATION_ADDRESS;

  // URL of the website of the organization (RFC 3986 format).
  private String welcomeUrl = BeaconConstants.ORGANIZATION_HOMEPAGE;

  // URL with the contact for the beacon operator/maintainer, e.g. link to
  // a contact form (RFC 3986 format) or an email (RFC 2368 format).
  private String contactUrl = BeaconConstants.ORGANIZATION_CONTACT;

  // URL to the logo (PNG/JPG format) of the organization (RFC 3986 format).
  private String logoUrl = BeaconConstants.ORGANIZATION_LOGO;

  // Additional structured metadata, key-value pairs
  private Map<String, String> info;

}
