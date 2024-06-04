package org.example.marketplacebackend.config;

import org.example.marketplacebackend.Application;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void
  addResourceHandlers(ResourceHandlerRegistry registry)
  {
    registry.addResourceHandler("/img/**")
        .addResourceLocations("file:" + Application.IMAGE_DIR);
  }
}
