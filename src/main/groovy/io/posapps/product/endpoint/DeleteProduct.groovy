package io.posapps.product.endpoint

import groovy.json.JsonOutput
import groovy.util.logging.Log4j
import io.posapps.product.auth.Auth
import io.posapps.product.model.Device
import io.posapps.product.model.Request
import io.posapps.product.model.Response
import io.posapps.product.service.ApiService
import io.posapps.product.service.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Log4j
@Component
class DeleteProduct extends Endpoint {

  private int statusCode
  private String body

  @Lazy
  @Autowired
  ProductRepository productRepository

  @Lazy
  @Autowired
  Auth auth

  @Lazy
  @Autowired
  ApiService apiService

  @Override
  boolean route(Request request) {
    def method = request.httpMethod()
    def device = request.queryString(DEVICE)
    def sku = request.queryString(SKU)
    return method == DELETE && sku && device
  }

  @Override
  Response respond(Request request) {
    def systemUser = auth.authenticateUser(request?.headers()?.Authorization)

    if (systemUser) {
      log.info("Delete product request: ${request.queryString(SKU)} systemUser: ${systemUser.id}")
      def existingProduct = productRepository.findProduct(request.queryString(SKU), systemUser.id)

      if (existingProduct) {
        def device = existingProduct.devices.find { it.name == request.queryString(DEVICE) }
        device ? device.event = DELETED : existingProduct.devices.add(new Device(name: request.queryString(DEVICE), event: DELETED))
        statusCode = 200
        body = JsonOutput.toJson(productRepository.save(existingProduct))
      }
      else {
        statusCode = 400
        body = '{"error": "customer does not exist "}'
      }
    }
    return Response.builder().statusCode(statusCode).body(body).build()
  }

}
