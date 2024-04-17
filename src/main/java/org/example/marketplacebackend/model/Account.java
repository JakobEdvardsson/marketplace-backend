package org.example.marketplacebackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.sql.Date;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @EqualsAndHashCode @ToString
@Entity
@Table(name = "account")
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private UUID id;

  private String first_name;
  private String last_name;
  private Date date_of_birth;
  private String email;
  private String password;
  private String username;

}
