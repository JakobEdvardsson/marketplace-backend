package org.example.marketplacebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import java.io.File;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

  public static String IMAGE_DIR;

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(Application.class);
  }

  public static void main(String[] args) {
    new File("/opt/img").mkdirs();
    IMAGE_DIR = "/opt/img/";
    SpringApplication.run(Application.class, args);
  }

}
