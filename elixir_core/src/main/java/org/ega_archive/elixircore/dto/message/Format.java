package org.ega_archive.elixircore.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Format {

  private String urlToDescriptionDoc = "http://ega.crg.eu/doc";

  // Version of the message format
  private String version = "v1";
}
