package com.ps.updemo.model;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import java.util.UUID;
import lombok.Data;

@Data
public class Feature {

  UUID id;
  Long timestamp;
  Long beginViewingDate;
  Long endViewingDate;
  String missionName;
  String quicklook;

  public static final Attribute<Feature, UUID> ID = new SimpleAttribute<>("featureId") {
    @Override
    public UUID getValue(Feature o, QueryOptions queryOptions) {
      return o.id;
    }
  };

  public static final Attribute<Feature, Long> TIMESTAMP = new SimpleAttribute<>("timestamp") {
    @Override
    public Long getValue(Feature o, QueryOptions queryOptions) {
      return o.timestamp;
    }
  };
}
