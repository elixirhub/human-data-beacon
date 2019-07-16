package org.ega_archive.elixirbeacon.enums;

import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixircore.exception.TypeNotFoundException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VariantType {

  INS("INS"),
  DEL("DEL"),
  DUP("DUP"),
  INV("INV"),
  CNV("CNV"),
  DUP_TANDEM("DUP:TANDEM"),
  DEL_ME("DEL:ME"),
  INS_ME("INS:ME");

  private String type;

  public static VariantType parse(String value) {
    VariantType type = null;

    if (StringUtils.isNotBlank(value)) {
      if (value.equalsIgnoreCase(INS.type)) {
        type = INS;
      } else if (value.equalsIgnoreCase(DEL.type)) {
        type = DEL;
      } else if (value.equalsIgnoreCase(DUP.type)) {
        type = DUP;
      } else if (value.equalsIgnoreCase(INV.type)) {
        type = INV;
      } else if (value.equalsIgnoreCase(CNV.type)) {
        type = CNV;
      } else if (value.equalsIgnoreCase(DUP_TANDEM.type)) {
        type = DUP_TANDEM;
      } else if (value.equalsIgnoreCase(DEL_ME.type)) {
        type = DEL_ME;
      } else if (value.equalsIgnoreCase(INS_ME.type)) {
        type = INS_ME;
      } else {
        throw new TypeNotFoundException("Structural variant type not implemented yet", value);
      }
    }
    return type;
  }

}
