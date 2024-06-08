package org.example.marketplacebackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${IMAGE_UPLOAD_DIRECTORY}")
  private String IMAGE_UPLOAD_DIRECTORY;

  @Override
  public void
  addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/img/**")
        .addResourceLocations("file:" + IMAGE_UPLOAD_DIRECTORY + "/");
  }
}
