package com.ps.updemo.repository;

import static com.googlecode.cqengine.query.QueryFactory.equal;

import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.query.QueryFactory;
import com.googlecode.cqengine.query.option.AttributeOrder;
import com.googlecode.cqengine.query.option.OrderByOption;
import com.ps.updemo.model.Feature;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@RequiredArgsConstructor
public class FeatureRepository {

  private final IndexedCollection<Feature> featureCollection;
  private static final OrderByOption ORDER_BY_OPTION = QueryFactory.orderBy(new AttributeOrder<>(Feature.TIMESTAMP, true));

  @Value("${updemo.pagesize:100}")
  private int pageSize;

  public Optional<Feature> findById(UUID id) {
    var query = equal(Feature.ID, id);
    var result = featureCollection.retrieve(query);

    if (result.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(result.uniqueResult());
  }

  public PaginatedResult list(Integer page) {

    var result = featureCollection.retrieve(QueryFactory.all(Feature.class), QueryFactory.queryOptions(ORDER_BY_OPTION));
    var currentPage = ObjectUtils.isEmpty(page) ? 1 : page;

    var paginatedResult = result.stream()
      .skip((currentPage - 1) * pageSize)
      .limit(pageSize);

    var features = PaginatedResult.builder()
      .currentPage(currentPage)
      .totalPages((result.size() + pageSize - 1) / pageSize)
      .features(paginatedResult)
      .build();

    return features;
  }

  @Builder
  @Getter
  public static class PaginatedResult {

    private final int totalPages;
    private final int currentPage;
    private final Stream<Feature> features;
  }
}
