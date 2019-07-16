package org.ega_archive.elixircore.repository;

import java.io.Serializable;

import org.ega_archive.elixircore.helper.CommonQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.querydsl.core.types.Predicate;

@NoRepositoryBean
public interface CustomQuerydslMongoRepository<T, ID extends Serializable>
    extends
    MongoRepository<T, ID>,
    QueryDslPredicateExecutor<T> {

  public Page<T> findAll(Predicate predicate, CommonQuery commonQuery);

  public Page<T> findAll(CommonQuery commonQuery);
}
