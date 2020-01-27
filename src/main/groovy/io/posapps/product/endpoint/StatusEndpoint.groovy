package io.posapps.product.endpoint

import groovy.json.JsonOutput
import groovy.util.logging.Log4j
import io.posapps.product.model.Request
import io.posapps.product.model.Response
import io.posapps.product.model.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Log4j
@Component
class StatusEndpoint extends Endpoint {

    @Autowired
    private Environment environment

    @Override
    boolean route(Request request) {
        return request.resourcePath() == '/status' && request.httpMethod() == GET
    }

    @Override
    Response respond(Request request) {
        return Response.builder()
                .statusCode(200)
                .body(JsonOutput.toJson(new Status(service: 'posApps Product API', state: 'running', profiles: environment.activeProfiles.join(','))))
                .build()
    }
}
