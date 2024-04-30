package org.example.marketplacebackend.service;

import java.util.UUID;
import lombok.NonNull;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for handling user management operations.
 */
@Service
public class UserService {

  private final AccountRepository accountRepo;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(AccountRepository accountRepo, PasswordEncoder passwordEncoder) {
    this.accountRepo = accountRepo;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Returns a user with the given username if it exists. Else throws a UsernameNotFound exception
   * which causes a redirect to the login page to occur
   *
   * @param username The username of the user to be returned.
   * @return The user with the given username if it exists.
   */
  public Account getAccountOrException(String username) {
    return accountRepo.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
  }

  /**
   * Returns a user with the given username if it exists. Else throws a UsernameNotFound exception
   * which causes a redirect to the login page to occur
   *
   * @param id The id of the user to be returned.
   * @return The user with the given id if it exists else throws an exception.
   */
  public Account getAccountOrException(UUID id) {
    return accountRepo.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
  }

  /**
   * Returns a user with the given username if it exists else null.
   *
   * @param username The username of the user to be returned.
   * @return The user with the given username if it exists.
   */
  public Account getAccountOrNull(@NonNull String username) {
    return accountRepo.findByUsername(username).orElse(null);
  }

  /**
   * @param id The id of the user to be returned.
   * @return The user with the given id if it exists else null.
   */
  public Account getAccountOrNull(UUID id) {
    return accountRepo.findById(id).orElse(null);
  }

  /**
   * @param rawPassword     The password to be checked.
   * @param encodedPassword The password to check against.
   * @return True if the passwords match
   */
  private boolean passwordMatch(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  /**
   * @param targetUser                  The user to be updated.
   * @param currentPasswordConfirmation The old password to be checked.
   * @param newPassword                 The new password to be set.
   * @return If the password was changed
   */
  public boolean passwordChange(Account targetUser, String currentPasswordConfirmation,
      String newPassword) {
    if (!passwordMatch(currentPasswordConfirmation, targetUser.getPassword())) {
      return false;
    }

    String encodedNewPassword = passwordEncoder.encode(newPassword);
    targetUser.setPassword(encodedNewPassword);
    saveUser(targetUser);
    return true;
  }

  /**
   * Saves the given user to the database.
   *
   * @param targetUser The user to be saved.
   * @return The saved user.
   */
  public Account saveUser(Account targetUser) {
    return accountRepo.save(targetUser);
  }

  /**
   * Deletes the given user to the database.
   *
   * @param targetUser The user to be deleted.
   */
  public void deleteUser(Account targetUser) {
    accountRepo.delete(targetUser);
  }
}