package io.posapps.product.endpoint

import com.google.gson.Gson
import groovy.json.JsonOutput
import groovy.util.logging.Log4j
import io.posapps.product.auth.Auth
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

import java.lang.reflect.Field

@Log4j
@Component
class UpdateProduct extends Endpoint {

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
    def product = new Gson().fromJson(request.body(), Product)
    def device = request.queryString(DEVICE)
    return method == PUT && product && device
  }

  @Override
  Response respond(Request request) {
    def systemUser = auth.authenticateUser(request?.headers()?.Authorization)

    if (systemUser) {
      def productUpdate = new Gson().fromJson(request.body(), Product)
      def updateDevice = new Device(name: request.queryString(DEVICE), event: UPDATED)
      productUpdate.devices = [updateDevice]

      def existingProduct = productRepository.findProduct(productUpdate.sku, systemUser.id)
      if (existingProduct) {
        def mergedProduct = mergeObjects(existingProduct, productUpdate)
        mergedProduct.devices.each {
          if (it.name == updateDevice.name) {
            it.event = UPDATED
          }
        }
        statusCode = 200
        body = JsonOutput.toJson(productRepository.save(mergedProduct))
      }
      else {
        statusCode = HttpStatus.BAD_REQUEST.value()
        body = '{"error": "product does not exist - please create product first"}'
      }
    }
    return Response.builder().statusCode(statusCode).body(body).build()
  }

  @SuppressWarnings("unchecked")
  static <T> T mergeObjects(T first, T second) throws IllegalAccessException, InstantiationException {
    Class<?> clazz = first.getClass()
    Field[] fields = clazz.getDeclaredFields()
    Object returnValue = clazz.newInstance()
    for (Field field : fields) {
      field.setAccessible(true)
      Object value1 = field.get(first)
      Object value2 = field.get(second)
      Object value = (value1 != null) ? value1 : value2
      field.set(returnValue, value)
    }
    return (T) returnValue
  }
}
