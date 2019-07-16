package org.ega_archive.elixircore.repository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.ega_archive.elixircore.helper.CommonQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;


public class CustomQuerydslJpaRepositoryImpl<T, ID extends Serializable>
    extends QueryDslJpaRepository<T, ID> implements CustomQuerydslJpaRepository<T, ID> {

  private final JpaEntityInformation<T, ?> entityInformation;
  private final EntityManager entityManager;
  private final EntityPath<T> path;
  private final PathBuilder<T> builder;
  private final Querydsl querydsl;

  public CustomQuerydslJpaRepositoryImpl(JpaEntityInformation<T, ID> entityInformation,
                                         EntityManager entityManager) {
    super(entityInformation, entityManager);

    this.entityManager = entityManager;
    this.entityInformation = entityInformation;
    this.path = SimpleEntityPathResolver.INSTANCE.createPath(entityInformation.getJavaType());
    this.builder = new PathBuilder<T>(path.getType(), path.getMetadata());
    this.querydsl = new Querydsl(entityManager, builder);
  }

  public Page<T> findAll(Predicate predicate, CommonQuery commonQuery) {
    JPQLQuery countQuery = createQuery(predicate);
    JPQLQuery query = null;
    QPageRequest pagination = null;
    if(commonQuery.getLimit() == 0 && commonQuery.getSkip() == 0) {
      query = createQuery(predicate);
    } else {
      pagination = new QPageRequest(commonQuery.getSkip(), commonQuery.getLimit());
      query = querydsl.applyPagination(pagination, createQuery(predicate));  
    }

    if (commonQuery.getSort() != null) {
      query = querydsl.applySorting(commonQuery.getSort(), query);
    }

    Long total = countQuery.fetchCount();
    List<T> content =
        pagination == null || total > pagination.getOffset() ? query.fetch() : Collections
            .<T>emptyList();

    return new PageImpl<T>(content, pagination, total);
  }

  public Page<T> findAll(CommonQuery commonQuery) {
    Page<T> page = null;
    if (commonQuery.getLimit() == 0 && commonQuery.getSkip() == 0) {
      // Return ALL results
      List<T> list = null;
      if(commonQuery.getSort() != null) {
        list = findAll(commonQuery.getSort());
      } else {
        list = findAll();
      }
      page = new PageImpl<T>(list);
    } else {
      page = findAll(null, commonQuery);
    }
    return page;
  }

}