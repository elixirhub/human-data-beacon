package org.ega_archive.elixircore.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class Response<T> {

  @XmlElement(name = "num_total_results")
  private Integer numTotalResults;

  @XmlElement(name = "result_type")
  private String resultType = "";

  @XmlElement(name = "result")
  private List<T> result;

  public Response() {
    this.numTotalResults = 0;
    this.result = new ArrayList<T>();
  }

  public Response(Integer totalResults, List<T> collection) {
    this.numTotalResults = totalResults;
    this.result = collection;
    this.resultType = collection.size() > 0 ? collection.get(0).getClass().getCanonicalName() : "";
  }

  public Response(T data) {
    List<T> list = new ArrayList<T>(1);
    list.add(data);
    this.result = list;
    this.numTotalResults = 1;
    this.resultType = data != null ? data.getClass().getCanonicalName() : "";
  }

  public Response(List<T> data) {
    this.resultType = data.size() > 0 ? data.get(0).getClass().getCanonicalName() : "";
    this.numTotalResults = data.size();
    this.result = data;
  }

}
