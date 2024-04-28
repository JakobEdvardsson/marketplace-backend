package org.example.marketplacebackend.config;

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //TODO: fix later
    http.cors(Customizer.withDefaults());

    //TODO: fix later
    http.csrf(AbstractHttpConfigurer::disable);

    http.authorizeHttpRequests(auth -> auth
        //allow OPTION requests
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        //don't require auth for these endpoints
        .requestMatchers(
            "/auth-not-required",
            "/v1/accounts/login",
            "/login",
            "/v1/accounts/register",
            "/resources/**"
        )
        .permitAll()
        //require auth to access these endpoints
        .requestMatchers(
            "/auth-required",
            "/v1/accounts"
        )
        .hasRole("USER")
    );

    http.formLogin(loginForm -> loginForm
            .loginProcessingUrl("/v1/accounts/login"))
        .logout(logoutConfigurer -> logoutConfigurer
            .deleteCookies("JSESSIONID")
        );

    /* TODO: add this code after adding a login page in frontend
        http.exceptionHandling(exceptionHandling -> exceptionHandling
        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
     */

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:80, http://localhost:8080, http://127.0.0.1:8080, http://localhost:3000")
            .allowCredentials(true);
      }
    };
  }

}
