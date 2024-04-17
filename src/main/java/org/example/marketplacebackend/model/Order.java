package org.example.marketplacebackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @EqualsAndHashCode @ToString
@Entity
@Table
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private UUID order;

  @ManyToOne
  private Account account;

  private Instant timeOfPurchase;

}
