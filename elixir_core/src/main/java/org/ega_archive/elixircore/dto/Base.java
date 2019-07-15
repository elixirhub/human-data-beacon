package org.ega_archive.elixircore.dto;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixircore.dto.auth.Session;
import org.ega_archive.elixircore.dto.auth.SessionUser;
import org.ega_archive.elixircore.dto.auth.User;
import org.ega_archive.elixircore.dto.version.GitRepositoryState;
import org.ega_archive.elixircore.dto.version.MavenEntry;
import org.ega_archive.elixircore.generic.Tuple;

@Log4j
@AllArgsConstructor
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "base")
@XmlSeeAlso(value = { GitRepositoryState.class,
                     MavenEntry.class,  
                     ServiceLocation.class,
                     SessionUser.class, User.class, Session.class})
public class Base<T> {

  @XmlElement(name = "header")
  private Header header;

  @XmlElement(name = "response")
  private Response<T> response;

  public Base() {
    this.header = new Header();
    this.response = new Response<T>();
  }

  public Base(Response<T> data) {
    this.header = new Header();
    this.response = data;
  }

  public Base(T data) {
    this.header = new Header();
    List<T> list = new ArrayList<T>(1);
    list.add(data);
    this.response = new Response<T>(data);
  }

  public Base(List<T> data) {
    this(new Response<T>(data));
  }

  public Base(int totalResults, List<T> data) {
    this(new Response<T>(totalResults, data));
  }

  // Method for exceptions
  public Base(String code, Exception e) {
    this(code, e, true);
  }

  public Base(String code, Exception e, Boolean debug) {
    Header header = new Header();
    header.setCode(code);
    header.setErrorCode(code);
    header.setDeveloperMessage(e.toString());
    header.setUserMessage((StringUtils.isNotBlank(e.getLocalizedMessage()) ? e
        .getLocalizedMessage() : e.getClass().toString()));

    //Set ErrorStack
    if (debug) {
      try (
          StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw);
      ) {
        e.printStackTrace(pw);
        String exceptionAsString = sw.toString();
        header.setErrorStack("-- " + exceptionAsString);
      } catch (IOException ex) {
        log.error("Exception getting stack trace", ex);
      }
    }
    this.header = header;
  }

  public Base(Tuple<T> tuple) {
   this(tuple.getValue(), tuple.getList()); 
  }
  
}
