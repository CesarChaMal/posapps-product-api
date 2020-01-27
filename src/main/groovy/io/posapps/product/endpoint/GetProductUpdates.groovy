package io.posapps.product.endpoint

import groovy.json.JsonOutput
import io.posapps.product.auth.Auth
import io.posapps.product.model.Device
import io.posapps.product.model.Product
import io.posapps.product.model.Request
import io.posapps.product.model.Response
import io.posapps.product.service.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class GetProductUpdates extends Endpoint {

  int statusCode
  String body

  @Lazy
  @Autowired
  Auth auth

  @Lazy
  @Autowired
  ProductRepository productRepository

  @Override
  boolean route(Request request) {
    request.resourcePath() == '/' && request.httpMethod() == GET && request.queryString(DEVICE)
  }

  @Override
  Response respond(Request request) {
    def authorized = auth.authenticateUser(request?.headers()?.Authorization)

    if (authorized) {
      def products = productRepository.findProducts(authorized.id)
      def productUpdates = findProductUpdates(products, request.queryString(DEVICE))
      statusCode = HttpStatus.OK.value()
      body = JsonOutput.toJson(productUpdates)
    } else {
      statusCode = HttpStatus.FORBIDDEN.value()
      body = '{"error": "No Subscription"}'
    }

    return Response.builder().statusCode(statusCode).body(body).build()
  }

  List<Product> findProductUpdates(List<Product> products, String deviceName) {
    def productUpdates = products.findAll { product ->
      !product.devices.find { it.name == deviceName } || productHasChanged(deviceName, product.devices)
    }
    // This device has made the change so it does not need to know about the change
    productUpdates.removeIf { it.devices.last().name == deviceName }

    return productUpdates
  }

  private boolean productHasChanged(String deviceName, Set<Device> existingDevices) {
    def currentDevice = existingDevices.find { it.name == deviceName }
    return existingDevices.find { it.event != currentDevice.event}
  }
}
