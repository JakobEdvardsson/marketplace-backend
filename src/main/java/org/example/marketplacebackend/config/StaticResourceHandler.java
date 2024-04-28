package org.example.marketplacebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.time.Duration;

@Configuration
@EnableWebMvc
public class StaticResourceHandler implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/resources/**")
        .addResourceLocations("classpath:/images/")
        // The resources are served with a one-year future expiration to
        // ensure maximum use of the browser cache and a reduction in HTTP requests made by the browser
        .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)));
  }
}
