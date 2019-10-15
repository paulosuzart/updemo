package com.ps.updemo.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FeatureControllerTest {

  @Autowired
  private MockMvc mockMvc;


  @Test
  public void byId() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
      .get("/features/39c2f29e-c0f8-4a39-a98b-deed547d6aea"))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.timestamp", is(1554831167697L)))
      .andExpect(jsonPath("$.missionName", is("Sentinel-1B")));
  }

  @Test
  public void list() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
      .get("/features"))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalPages", is(1)))
      .andExpect(jsonPath("$.currentPage", is(1)))
      .andExpect(jsonPath("$.features[0].id", is("cf5dbe37-ab95-4af1-97ad-2637aec4ddf0")));
  }

  @Test
  public void listEmpty() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
      .get("/features?page=6"))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalPages", is(1)))
      .andExpect(jsonPath("$.currentPage", is(6)))
      .andExpect(jsonPath("$.features", hasSize(0)));

  }

  @Test
  public void quicklook() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/features/39c2f29e-c0f8-4a39-a98b-deed547d6aea/quicklook"))
      .andExpect(status().isOk())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_JPEG))
      .andExpect(MockMvcResultMatchers.header().longValue("Content-Length", 236829));
  }
}