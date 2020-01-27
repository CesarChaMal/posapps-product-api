package io.posapps.product

import groovy.json.JsonOutput
import groovy.util.logging.Log4j
import io.posapps.product.model.Request
import io.posapps.product.model.Response
import io.posapps.product.endpoint.Endpoint
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Log4j
@Service
class Dispatcher {

  @Lazy
  @Autowired
  List<Endpoint> endpoints

  Response dispatch(Request request) {
    log.debug("Incoming Request: ${JsonOutput.toJson(request)}")
    def endpoint = endpoints.find { it.route(request) }
    if (!endpoint) {
      log.info("No Endpoint found using the following rquest : ${JsonOutput.toJson(request)}")
      return Response.builder().statusCode(500).body('{"error": "No endpoint for this request"}').build()
    }

    log.info("Endpoint found: ${endpoint.class}")
    final def response = endpoint.respond(request)
    response.addHeader('X-Request-Id', request?.requestId())
    return response
  }

}
