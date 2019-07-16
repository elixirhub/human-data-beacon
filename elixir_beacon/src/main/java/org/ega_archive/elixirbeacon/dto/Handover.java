package org.ega_archive.elixirbeacon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Handover {

  private HandoverType handoverType;

  private String note;

  private String url;

}
