package com.ps.updemo.config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CollectionLoaderTest {

  @Value("${updemo.source}")
  Resource source;

  @Value("${updemo.transform}")
  Resource transform;

  @Test
  public void transform() throws IOException {
    var collectionLoader = CollectionLoader.newInstance(transform);
    var raw = collectionLoader.transform(source);
    Assert.assertThat(raw.size(), is(2));

  }
}