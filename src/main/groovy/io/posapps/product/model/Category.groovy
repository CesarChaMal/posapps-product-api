package io.posapps.product.model

import groovy.transform.EqualsAndHashCode
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Version
import java.sql.Timestamp

@Entity
@EqualsAndHashCode(excludes =["created", "updated", "version", "id"])
class Category {
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
  @OneToOne
  Category parent
  String name
}
