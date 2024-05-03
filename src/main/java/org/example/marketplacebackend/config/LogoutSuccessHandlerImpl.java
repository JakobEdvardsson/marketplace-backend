package org.example.marketplacebackend.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * This class is used to override the default behavior of Spring Security's logout success handler.
 * This implementation returns HTTP 200 on successful logout instead of issuing a redirect.
 */
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_OK);
  }
}
