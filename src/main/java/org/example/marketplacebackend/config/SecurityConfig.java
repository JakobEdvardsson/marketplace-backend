package org.example.marketplacebackend.config;

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
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
            "/v1/accounts/login",
            "/v1/accounts/logout",
            "/v1/accounts/register",
            "/images/**",
            "/v1/categories",
            "/v1/products",
            "/v1/products/**",
            "/v1/watchlist"

        )
        .permitAll()
        //require auth to access these endpoints
        .requestMatchers(
            "/v1/inbox",
            "/v1/inbox/*",
            "/v1/accounts",
            "/v1/tests/username"
        )
        .hasRole("USER")
    );

    http.formLogin(loginForm -> loginForm
            .loginProcessingUrl("/v1/accounts/login")
            .successHandler(new LoginSuccessHandlerImpl())
            .failureHandler(new LoginFailureHandlerImpl())
        )
        .logout(logoutConfigurer -> logoutConfigurer
            .logoutUrl("/v1/accounts/logout")
            .logoutSuccessHandler(new LogoutSuccessHandlerImpl())
            .deleteCookies("JSESSIONID")
        );

    // return HTTP 401 when a user tries to access an endpoint that they don't have access to,
    // instead of trying to redirect them to the default login page
    http.exceptionHandling(exceptionHandling -> exceptionHandling
        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

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
        registry.addMapping("/**").allowedOrigins("http://localhost:3000", "https://marketplace.johros.dev")
            .allowCredentials(true);
      }
    };
  }

}
