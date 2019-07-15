package org.ega_archive.elixircore.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonQuery {

  private List<String> include = null;

  private List<String> exclude = null;

  private Integer limit = 10;

  private Integer skip = 0;

  private Sort sort = null;

  public CommonQuery(Integer limit, Integer skip) {
    this.limit = limit;
    this.skip = skip;
  }
  
  public Pageable getPageable() {
    if (skip == 0 && limit == 0) {
      return new PageRequest(skip, Integer.MAX_VALUE, sort);
    }
    return new PageRequest(skip, limit, sort);
  }
  
}
