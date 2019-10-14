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
import org.springframework.core.io.Resource;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionLoader {

  private final Chainr chainr;


  List<Feature> transform(final Resource source) throws IOException {
    var input = JsonUtils.jsonToList(source.getInputStream());

    var transformed = chainr.transform(input);
    var mapper = new ObjectMapper();
    return ((List<Map<String, Object>>) transformed)
      .stream()
      .map(entry -> mapper.convertValue(entry, Feature.class))
      .collect(Collectors.toList());

  }

  public static CollectionLoader newInstance(Resource transformResource) throws IOException {
    var spec = JsonUtils.jsonToList(transformResource.getInputStream());
    var chainr = Chainr.fromSpec(spec);
    return new CollectionLoader(chainr);
  }
}
