package org.ega_archive.elixircore.repository;

import java.io.Serializable;
import java.util.List;

import org.ega_archive.elixircore.helper.CommonQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.QueryDslMongoRepository;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;


public class CustomQuerydslMongoRepositoryImpl<T, ID extends Serializable>
    extends QueryDslMongoRepository<T, ID> implements CustomQuerydslMongoRepository<T, ID> {

  // All instance variables are available in super, but they are private
  private static final EntityPathResolver DEFAULT_ENTITY_PATH_RESOLVER =
      SimpleEntityPathResolver.INSTANCE;

  private final EntityPath<T> path;
  private final PathBuilder<T> pathBuilder;
  private final MongoOperations mongoOperations;


  public CustomQuerydslMongoRepositoryImpl(MongoEntityInformation<T, ID> entityInformation,
                                           MongoOperations mongoOperations) {
    this(entityInformation, mongoOperations, DEFAULT_ENTITY_PATH_RESOLVER);
  }

  public CustomQuerydslMongoRepositoryImpl(MongoEntityInformation<T, ID> entityInformation,
                                           MongoOperations mongoOperations,
                                           EntityPathResolver resolver) {
    super(entityInformation, mongoOperations, resolver);
    this.path = resolver.createPath(entityInformation.getJavaType());
    this.pathBuilder = new PathBuilder<T>(path.getType(), path.getMetadata());
    this.mongoOperations = mongoOperations;
  }

  public Page<T> findAll(Predicate predicate, CommonQuery commonQuery) {
    Page<T> page = null;
    if (commonQuery.getLimit() == 0 && commonQuery.getSkip() == 0) {
      // Return ALL results
      List<T> list = findAll(predicate);
      page = new PageImpl<T>(list);
    } else {
      page = findAll(predicate, commonQuery.getPageable());
    }
    return page;
  }

  public Page<T> findAll(CommonQuery commonQuery) {
    Page<T> page = null;
    if (commonQuery.getLimit() == 0 && commonQuery.getSkip() == 0) {
      // Return ALL results
      List<T> list = findAll();
      page = new PageImpl<T>(list);
    } else {
      page = findAll(null, commonQuery);
    }
    return page;
  }
}
