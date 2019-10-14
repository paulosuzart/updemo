package com.ps.updemo.config;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.index.unique.UniqueIndex;
import com.ps.updemo.model.Feature;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Slf4j
@Configuration
public class UpdemoConfig {

  @Bean
  public IndexedCollection<Feature> featureCollection(@Value("${updemo.source}") Resource source,
    @Value("${updemo.transform}") Resource transform)
    throws IOException {

    var loader = CollectionLoader.newInstance(transform);

    var rawCollection = loader.transform(source);

    var collection = new ConcurrentIndexedCollection<Feature>();
    collection.addIndex(UniqueIndex.onAttribute(Feature.ID));
    collection.addIndex(NavigableIndex.onAttribute(Feature.TIMESTAMP));

    collection.addAll(rawCollection);

    return collection;
  }
}
