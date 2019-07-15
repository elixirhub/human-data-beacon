package org.ega_archive.elixircore.generic;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.ega_archive.elixircore.dto.Response;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@Getter
public class Tuple<T> {

  private final Integer value;
  private final List<T> list;
  
  public Tuple() {
    value = 0;
    list = new ArrayList<T>();
  }
  
  public Tuple(long size, List<T> list) {
    this.value = (int) size;
    this.list = list;
  }

  public Tuple(T data) {
    this.value=1;
    List<T> list = new ArrayList<T>();
    list.add(data);
    this.list=list;
  }
  
  public Tuple(List<T> list) {
    this.value = list.size();
    this.list = list;
  }
  
  public Tuple(Page<T> page) {
    value = (int) page.getTotalElements();
    list = page.getContent();
  }
  
  public Tuple(Response<T> response) {
    this.value = response.getNumTotalResults();
    this.list = response.getResult();
  }
  
}
