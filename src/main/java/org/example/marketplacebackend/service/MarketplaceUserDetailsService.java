package org.example.marketplacebackend.service;

import org.example.marketplacebackend.model.MarketplaceUserPrincipal;
import org.example.marketplacebackend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MarketplaceUserDetailsService implements UserDetailsService {

  private final AccountRepository accountRepository;

  @Autowired
  public MarketplaceUserDetailsService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  /**
   * This method is called by Spring Security when a user tries to authenticate.
   *
   * @param username the username identifying the user whose data is required.
   * @return a UserDetails object containing the user's data.
   * @throws UsernameNotFoundException if the user is not found.
   */

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return accountRepository
        .findByUsername(username)
        .map(MarketplaceUserPrincipal::new)
        .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));
  }

}
