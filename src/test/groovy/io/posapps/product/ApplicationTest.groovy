package io.posapps.product

import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import io.posapps.product.model.Device
import io.posapps.product.model.Product
import io.posapps.product.model.Response
import io.posapps.product.model.Status
import io.posapps.product.model.Subscription
import io.posapps.product.model.SystemUser
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

import javax.transaction.Transactional
import java.lang.reflect.Type

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.delete
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.put
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo

@RunWith(SpringRunner)
class ApplicationTest {

  Application application

  @Rule
  public WireMockRule wireMockRule = new WireMockRule()

  @Before
  void setup() {
    application = new Application()
    initWireMock()
  }

  @Test
  void 'should get a status response'() {
    def input = new JsonSlurper().parseText(getClass().getResource('/input/get-status-input.json').text) as Map
    def response = application.handleRequest(input, null)
    assert response.statusCode == 200
    assert new JsonSlurper().parseText(response.body) as Status
  }

  @Test
  void 'should create a product'() {
    def response = createProduct(new Product(name: 'create product', sku: '123'), 'terminal1')
    assert response.statusCode == 200
    assert new Gson().fromJson(response.body, Product).name == 'create product'
  }

  @Test
  void 'should get product updates'() {
    createProduct(new Product(name: 'get product updates', sku: '456'), 'term1')
    def response = getProductUpdates('term5')
    assert response.statusCode == 200
    Type collectionType = new TypeToken<List<Product>>() {}.type
    assert (new Gson().fromJson(response.body, collectionType) as List).size() > 0
  }

  @Test
  void 'should not get product updates'() {
    createProduct(new Product(name: 'get product updates', sku: '111'), 'term0')
    def response = getProductUpdates('term0')
    assert response.statusCode == 200
    Type collectionType = new TypeToken<List<Product>>() {}.type
    assert (new Gson().fromJson(response.body, collectionType) as List).size() == 0
  }

  @Test
  void 'should update a product' () {
    def updateDevice = 'term0'
    def productCreatedResponse = createProduct(new Product(name: 'product to update', sku: '222'), updateDevice)
    assert productCreatedResponse.statusCode == 200
    def product = new Gson().fromJson(productCreatedResponse.body, Product)
    assert product.name == 'product to update' && product.description == null

    def updateProduct = new Product(name: 'product to update', sku: '222', description: 'product updated')
    def updatedProductResponse = this.updateProduct(updateProduct, 'term0')
    def updatedProduct = new Gson().fromJson(updatedProductResponse.body, Product)
    assert updatedProduct.name == 'product to update'
    assert updatedProduct.description == 'product updated'
    assert updatedProduct.devices.find { it.name == updateDevice}.event == 'updated'
  }

  @Test
  void 'should delete a product'() {
    def sku = '321'
    def deviceName = 'terminal2'
    def productName = 'Delete Product'

    def createProductResponse = createProduct(new Product(name: productName, sku: sku), deviceName)
    assert createProductResponse.statusCode == 200
    assert new Gson().fromJson(createProductResponse.body, Product).name == productName

    def deleteProductResponse = deleteProduct(sku, deviceName)
    assert deleteProductResponse.statusCode == 200
    def device = new Gson().fromJson(deleteProductResponse.body, Product).devices.find { it.event == 'deleted' }
    assert device.name == deviceName
  }

  private Response getProductUpdates(String device) {
    def input = [:]
    input.resource = '/'
    input.httpMethod = 'GET'
    input.queryStringParameters = ['device': device]
    input.stageVariables = getStageVariables()
    return application.handleRequest(input, null)
  }

  private Response updateProduct(Product product, String device) {
    def updateProductInput = [:]
    updateProductInput.body = new Gson().toJson(product)
    updateProductInput.resource = '/'
    updateProductInput.httpMethod = 'PUT'
    updateProductInput.queryStringParameters = ['device': device]
    updateProductInput.stageVariables = getStageVariables()

    return application.handleRequest(updateProductInput, null)
  }

  private Response createProduct(Product product, String device) {
    def createProductInput = [:]
    createProductInput.body = new Gson().toJson(product)
    createProductInput.resource = '/'
    createProductInput.httpMethod = 'POST'
    createProductInput.queryStringParameters = ['device': device]
    createProductInput.stageVariables = getStageVariables()
    return application.handleRequest(createProductInput, null)
  }

  private Map getStageVariables() {
    def stageVariables = [:]
    stageVariables.put('profile', 'local')
    stageVariables.put('dataSourceUrl', 'jdbc:mysql://127.0.0.1:3306/posapps_product_test?useSSL=false')
    stageVariables.put('dataSourceUsername', 'posapps_test')
    stageVariables.put('dataSourcePassword', 'password')
    return stageVariables
  }

  private Response deleteProduct(String sku, String device) {
    def deleteProductInput = [:]
    deleteProductInput.resource = '/'
    deleteProductInput.httpMethod = 'DELETE'
    deleteProductInput.queryStringParameters = ['device': device, 'sku': sku]
    deleteProductInput.stageVariables = getStageVariables()
    return application.handleRequest(deleteProductInput, null)
  }

  private static void initWireMock() {
    stubFor(get(urlPathEqualTo('/admin/authenticate'))
            .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withBody(JsonOutput.toJson(
            new SystemUser(
                    id: 1,
                    emailAddress: 'test@test.com',
                    password: 'password',
                    subscriptions: [new Subscription(name: 'CustomerApi')])))))
  }
}
