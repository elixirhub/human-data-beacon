package org.ega_archive.elixirbeacon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Handover {

  private HandoverType handoverType;

  private String note;

  private String url;

}
