package com.ps.updemo.controller;

import static com.googlecode.cqengine.query.QueryFactory.equal;
import static com.ps.updemo.controller.FeatureDTO.fromFeature;

import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.query.QueryFactory;
import com.googlecode.cqengine.query.option.AttributeOrder;
import com.googlecode.cqengine.query.option.OrderByOption;
import com.ps.updemo.model.Feature;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FeatureController {

  private final IndexedCollection<Feature> featureCollection;
  private static final OrderByOption ORDER_BY_OPTION = QueryFactory.orderBy(new AttributeOrder<>(Feature.TIMESTAMP, true));

  @Value("${updemo.pagesize:100}")
  private int pageSize;

  @GetMapping(value = "/features/{id}", produces = "application/json")
  public ResponseEntity<FeatureDTO> byId(@PathVariable("id") UUID id) {
    var query = equal(Feature.ID, id);
    var result = featureCollection.retrieve(query);

    if (result.isEmpty()) {
      return ResponseEntity.notFound()
        .build();
    }

    return ResponseEntity.ok(fromFeature(result.uniqueResult()));
  }

  @GetMapping(value = "/features", produces = "application/json")
  public ResponseEntity<PaginatedResponse> list(@RequestParam(value = "page", required = false) final Integer page) {

    var result = featureCollection.retrieve(QueryFactory.all(Feature.class), QueryFactory.queryOptions(ORDER_BY_OPTION));
    var currentPage = ObjectUtils.isEmpty(page) ? 1 : page;

    var paginatedResult = result.stream()
      .skip((currentPage - 1) * pageSize)
      .limit(pageSize)
      .map(FeatureDTO::fromFeature).collect(Collectors.toList());

    var response = PaginatedResponse.builder()
      .currentPage(currentPage)
      .totalPages((result.size() + pageSize - 1) / pageSize)
      .features(paginatedResult)
      .build();

    return ResponseEntity.ok(response);
  }

  @Builder
  @Getter
  static class PaginatedResponse {

    private final int totalPages;
    private final int currentPage;
    private final List<FeatureDTO> features;
  }


}
