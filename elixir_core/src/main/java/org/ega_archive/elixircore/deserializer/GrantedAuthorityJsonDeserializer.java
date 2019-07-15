package org.ega_archive.elixircore.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;

public class GrantedAuthorityJsonDeserializer extends JsonDeserializer<GrantedAuthority> {

  @Override
  public GrantedAuthority deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException,
             JsonProcessingException {
    Authority authority = jp.readValueAs(Authority.class);
    GrantedAuthority returnAuthority = new SimpleGrantedAuthority(authority.authority);
    return returnAuthority;
  }

  private static class Authority {

    public String authority;
  }

}