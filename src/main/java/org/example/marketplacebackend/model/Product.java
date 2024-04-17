package org.example.marketplacebackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @EqualsAndHashCode @ToString
@Entity
@Table
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private UUID id;

  private String name;

  // todo
  private String type;

  private Integer price;

  private Integer condition;

  private Boolean isPurchased;

  private String description;

  // todo
  private Object seller;

  // todo
  private Object buyer;

  private Integer color;

  private Integer productionYear;

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
  private List<ProductImage> productImages;

}
