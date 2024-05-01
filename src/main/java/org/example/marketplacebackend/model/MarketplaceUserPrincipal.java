package org.example.marketplacebackend.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.example.marketplacebackend.model.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MarketplaceUserPrincipal implements UserDetails {

  private final Account account;

  public MarketplaceUserPrincipal(Account account) {
    this.account = account;
  }

  /**
   * @return the username of the user.
   */
  @Override
  public String getUsername() {
    return account.getUsername();
  }

  /**
   * @return the encrypted/hashed password of the user.
   */
  @Override
  public String getPassword() {
    return account.getPassword();
  }

  /**
   * This method is used to get the roles of the user.
   *
   * @return a list of authorities that the user has.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  /**
   * Not used.
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Not used.
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Not used.
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Not used.
   */
  @Override
  public boolean isEnabled() {
    return true;
  }

}
