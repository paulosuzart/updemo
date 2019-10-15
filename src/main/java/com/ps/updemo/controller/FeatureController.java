package com.ps.updemo.controller;

import com.ps.updemo.repository.FeatureRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "features")
@RequiredArgsConstructor
@RestController
public class FeatureController {

  private final FeatureRepository featureRepository;

  @ApiOperation(value = "Retrieves an existing feature by id")
  @ApiResponses(value = {
    @ApiResponse(code = 400, message = "Request is invalid")
  })
  @GetMapping(value = "/features/{id}", produces = "application/json")
  public ResponseEntity<FeatureDTO> byId(@PathVariable("id") UUID id) {
    return ResponseEntity.of(featureRepository.findById(id).map(FeatureDTO::fromFeature));
  }

  @ApiOperation(value = "List all stored features. The response is paginated and returned Features are sorted descending by timestamp")
  @GetMapping(value = "/features", produces = "application/json")
  public ResponseEntity<PaginatedResponse> list(@RequestParam(value = "page", required = false) final Integer page) {

    var result = featureRepository.list(page);

    var response = PaginatedResponse.builder()
      .currentPage(result.getCurrentPage())
      .totalPages(result.getTotalPages())
      .features(result.getFeatures().map(FeatureDTO::fromFeature).collect(Collectors.toList()))
      .build();

    return ResponseEntity.ok(response);
  }

  @ApiOperation(value = "Retrieves the quicklook image of a feature")
  @GetMapping(value = "/features/{id}/quicklook", produces = "image/jpeg")
  public ResponseEntity<byte[]> quicklook(@PathVariable("id") UUID id) {
    return featureRepository.findById(id).map(feature -> {
      HttpHeaders headers = new HttpHeaders();
      headers.setCacheControl(CacheControl.noCache().getHeaderValue());
      var bytes = Base64.getDecoder().decode(feature.getQuicklook());
      headers.setCacheControl(CacheControl.noCache().getHeaderValue());

      return new ResponseEntity<>(bytes, headers, HttpStatus.OK);

    }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Builder
  @Getter
  static class PaginatedResponse {

    private final int totalPages;
    private final int currentPage;
    private final List<FeatureDTO> features;
  }


}
