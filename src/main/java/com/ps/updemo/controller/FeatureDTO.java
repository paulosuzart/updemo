package com.ps.updemo.controller;

import com.ps.updemo.model.Feature;
import io.swagger.annotations.ApiModel;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(value = "Feature", description = "Represents a Feature being served")
public class FeatureDTO {

  UUID id;
  Long timestamp;
  Long beginViewingDate;
  Long endViewingDate;
  String missionName;

  static FeatureDTO fromFeature(Feature feature) {
    return FeatureDTO.builder()
      .beginViewingDate(feature.getBeginViewingDate())
      .endViewingDate(feature.getEndViewingDate())
      .id(feature.getId())
      .missionName(feature.getMissionName())
      .timestamp(feature.getTimestamp())
      .build();
  }
}
