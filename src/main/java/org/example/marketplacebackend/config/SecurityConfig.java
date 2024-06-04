package org.example.marketplacebackend.config;

import java.util.List;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
// @EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(Customizer.withDefaults());

    // TODO: fix later
    http.csrf(AbstractHttpConfigurer::disable);

    http.authorizeHttpRequests(auth -> auth
        // allow OPTION requests
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        // don't require auth for these endpoints
        .requestMatchers(
            "/v1/accounts/login",
            "/v1/accounts/logout",
            "/v1/accounts/register",
            "/v1/accounts/*",
            "/v1/categories",
            "/v1/products",
            "/v1/products/**",
            "/img/**"
        )
        .permitAll()
        // require auth to access these endpoints
        .requestMatchers(
            "/v1/inbox",
            "/v1/inbox/*",
            "/v1/accounts",
            "/v1/accounts/password",
            "/v1/accounts/me",
            "/v1/tests/username",
            "/v1/orders",
            "/v1/orders/**",
            "/v1/watchlist",
            "/v1/watchlist/**"
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

  // TODO: Investigate why CORS credentials can't be configured selectively:
  //  When only allowing credentials for login/logout globally,
  //  it seems to override the class level CORS configurations in controllers.
  //  This results in credentials not being allowed in any other endpoints than login/logout.
  //  As such, the following is a workaround which simply allows credentials for everything.
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration withCreds = new CorsConfiguration();
    withCreds.setAllowedOrigins(List.of("http://localhost:3000", "https://marketplace.johros.dev"));
    withCreds.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    withCreds.setAllowCredentials(true);
    withCreds.setAllowedHeaders(List.of("Content-Type"));

    UrlBasedCorsConfigurationSource config = new UrlBasedCorsConfigurationSource();
    config.registerCorsConfiguration("/**", withCreds);
    config.registerCorsConfiguration("/v1/accounts/login", withCreds);
    config.registerCorsConfiguration("/v1/accounts/logout", withCreds);

    return config;
  }

}