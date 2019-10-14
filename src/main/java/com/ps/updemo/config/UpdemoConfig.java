package com.ps.updemo.config;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.unique.UniqueIndex;
import com.ps.updemo.model.Feature;
import java.io.IOException;
import java.util.UUID;
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
    var spec = JsonUtils.jsonToList(transform.getInputStream());
    var chainr = Chainr.fromSpec(spec);

    var input = JsonUtils.jsonToList(source.getInputStream());

    var transformed = chainr.transform(input);
    System.out.println(transformed);
    System.out.println(transformed.getClass());

    var entry = new Feature();
    entry.setId(UUID.randomUUID());
    entry.setMissionName("Sample");
    entry.setBeginViewingDate(123L);
    entry.setEndViewingDate(123L);
    entry.setTimestamp(123L);
    var collection = new ConcurrentIndexedCollection<Feature>();
    collection.addIndex(UniqueIndex.onAttribute(Feature.ID));

    collection.add(entry);
    log.info("OK {}", collection);
    return collection;
  }
}
