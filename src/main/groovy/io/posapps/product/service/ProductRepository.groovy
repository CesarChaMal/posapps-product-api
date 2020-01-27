package io.posapps.product.service

import io.posapps.product.model.Product
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface ProductRepository extends CrudRepository<Product, Long> {

  @Query('from Product where sku = :sku and systemUserId = :systemUserId')
  Product findProduct(@Param('sku')String sku, @Param('systemUserId')Long systemUserId)

  @Query('from Product where systemUserId = :systemUserId')
  List<Product> findProducts(@Param('systemUserId') Long systemUserId)

}
