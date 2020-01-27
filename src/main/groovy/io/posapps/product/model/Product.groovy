package io.posapps.product.model

import groovy.transform.EqualsAndHashCode
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp

import javax.persistence.*
import java.sql.Timestamp

@Entity
@EqualsAndHashCode(excludes =["created", "updated", "version", "id"])
class Product {
  @Id
  @GeneratedValue
  @Column(columnDefinition = "int")
  Long id
  @CreationTimestamp
  Timestamp created
  @UpdateTimestamp
  Timestamp updated
  @Version
  Integer version
  String name
  String description
  String sku
  String barcode
  String barcodeType
  String imagePath
  Double sellingPrice
  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  List<Category> categories
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @OrderBy("updated")
  Set<Device> devices
  Long systemUserId
}
