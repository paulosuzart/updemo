package com.ps.updemo.config;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ps.updemo.model.Feature;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionLoader {

  private final Chainr chainr;


  List<Feature> transform(final Resource source) throws IOException {
    log.info("Trying to load collection from {}", source.getFilename());
    var input = JsonUtils.jsonToList(source.getInputStream());

    var transformed = chainr.transform(input);
    var mapper = new ObjectMapper();

    var raw = ((List<Map<String, Object>>) transformed)
      .stream()
      .map(entry -> mapper.convertValue(entry, Feature.class))
      .collect(Collectors.toList());

    log.info("Loaded {} records from the provided source", raw.size());

    return raw;

  }

  public static CollectionLoader newInstance(Resource transformResource) throws IOException {
    var spec = JsonUtils.jsonToList(transformResource.getInputStream());
    var chainr = Chainr.fromSpec(spec);
    return new CollectionLoader(chainr);
  }
}
