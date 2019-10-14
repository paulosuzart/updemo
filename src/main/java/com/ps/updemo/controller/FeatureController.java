package com.ps.updemo.controller;

import static com.googlecode.cqengine.query.QueryFactory.equal;

import com.googlecode.cqengine.IndexedCollection;
import com.ps.updemo.model.Feature;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FeatureController {

  private final IndexedCollection<Feature> featureCollection;

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


}
