package io.posapps.product.endpoint

import com.google.gson.Gson
import groovy.json.JsonOutput
import groovy.util.logging.Log4j
import io.posapps.product.auth.Auth
import io.posapps.product.endpoint.Endpoint
import io.posapps.product.model.Device
import io.posapps.product.model.Product
import io.posapps.product.model.Request
import io.posapps.product.model.Response
import io.posapps.product.service.ApiService
import io.posapps.product.service.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

import javax.transaction.Transactional

@Log4j
@Component
class CreateProduct extends Endpoint {

  private int statusCode
  private String body

  @Lazy
  @Autowired
  Auth auth

  @Lazy
  @Autowired
  ApiService apiService

  @Lazy
  @Autowired
  ProductRepository productRepository

  @Override
  boolean route(Request request) {
    def method = request.httpMethod()
    def product = getProduct(request)
    def device = request.queryString(DEVICE)
    return method == POST && product && device
  }

  @Override
  @Transactional
  Response respond(Request request) {
    def systemUser = auth.authenticateUser(request?.headers()?.Authorization)

    if (systemUser) {
      def product = new Gson().fromJson(request.body(), Product) as Product
      def existingProduct = productRepository.findProduct(product.sku, systemUser.id)
      def device = new Device(name: request.queryString(DEVICE), event: CREATED)
      if (existingProduct) {
        if (existingProduct.devices.contains(device)) {
          statusCode = HttpStatus.BAD_REQUEST.value()
          body = '{"error": "Product has already been created - please use an update"}'
        } else {
          statusCode = HttpStatus.OK.value()
          existingProduct.devices.add(device)
          body = new Gson().toJson(productRepository.save(existingProduct))
        }
        statusCode = HttpStatus.OK.value()
        body = JsonOutput.toJson(existingProduct)
      } else {
        product.systemUserId = systemUser.id
        product.devices = [device]
        statusCode = HttpStatus.OK.value()
        body = JsonOutput.toJson(productRepository.save(product))
        // need to send the customer to the users configured apis
      }

    } else {
      statusCode = HttpStatus.FORBIDDEN.value()
      body = '{"error": "No Subscription"}'
    }

    return Response.builder().statusCode(statusCode).body(body).build()
  }

  static Product getProduct(Request request) {
    try {
      new Gson().fromJson(request.body(), Product)
    }
    catch (Exception e) {
      log.error("Error deserializing object $e")
    }
  }
}
