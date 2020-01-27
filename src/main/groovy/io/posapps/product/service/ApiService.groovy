package io.posapps.product.service

import groovy.util.logging.Log4j
import io.posapps.product.auth.Auth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Log4j
@Component
class ApiService {

  @Lazy
  @Autowired
  RestTemplate restTemplate

  @Lazy
  @Autowired
  Auth auth

  @Value('${posapps.woocom.adapter.endpoint}')
  String woocomAdapter

}
