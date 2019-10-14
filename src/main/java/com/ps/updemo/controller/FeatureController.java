package com.ps.updemo.controller;

import static com.googlecode.cqengine.query.QueryFactory.equal;

import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.query.QueryFactory;
import com.googlecode.cqengine.query.option.AttributeOrder;
import com.googlecode.cqengine.query.option.OrderByOption;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.ps.updemo.model.Feature;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FeatureController {

  private final IndexedCollection<Feature> featureCollection;
  private static final OrderByOption ORDER_BY_OPTION = QueryFactory.orderBy(new AttributeOrder<>(Feature.TIMESTAMP, true));


  @GetMapping("/features/{id}")
  public ResponseEntity<Feature> byId(@PathVariable("id") UUID id) {
    var query = equal(Feature.ID, id);
    var result = featureCollection.retrieve(query);

    if (result.isEmpty()) {
      return ResponseEntity.notFound()
        .build();
    }

    return ResponseEntity.ok(result.uniqueResult());
  }

  @GetMapping("/features")
  public ResponseEntity<List<Feature>> list(@RequestParam("page") int page) {
    var result = featureCollection.retrieve(QueryFactory.all(Feature.class), QueryFactory.queryOptions(ORDER_BY_OPTION));
    var paginatedResult = result.stream().skip(page * 100).limit(100).collect(Collectors.toList());
    return ResponseEntity.ok(paginatedResult);
  }


}
