package io.posapps.product.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class RestTemplateConfig {

  @Value('${http.connection.timeout}')
  int connectionTimeout

  @Value('${http.read.timeout}')
  int readTimeout

  @Bean
  RestTemplate restTemplate(RestTemplateBuilder builder) {
    builder.setConnectTimeout(connectionTimeout)
    builder.setReadTimeout(readTimeout)
    return builder.build()
  }

}
