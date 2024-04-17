package org.example.marketplacebackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Objects;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "product_image")
public class ProductImage {

  @Id
  private Long id;
  private String imageUrl;

  public ProductImage(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o)
      return true;
    if (!(o instanceof ProductImage image))
      return false;

    return Objects.equals(this.id, image.id) && Objects.equals(this.imageUrl, image.imageUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.imageUrl);
  }

  @Override
  public String toString() {
    return "ProductImage{" + "id=" + this.id + ", url='" + this.imageUrl + '}';
  }
}
