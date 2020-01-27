package io.posapps.product.auth

import com.google.gson.Gson
import io.posapps.product.model.SystemUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate


@Component
class Auth {

    @Lazy
    @Value('${posapps.auth.endpoint}')
    String authEndpoint

    @Lazy
    @Autowired
    RestTemplate restTemplate

    SystemUser authenticateUser(String credentials) {
        def systemUser = getSystemUser(credentials)
        return subscriptionActive(systemUser) ? systemUser : null
    }

    String buildBasicAuth(String userName, String password) {
        return 'Basic ' + Base64.getEncoder().encodeToString((userName + ':' + password).getBytes())
    }

    private boolean subscriptionActive(SystemUser systemUser) {
        return systemUser.subscriptions?.name?.flatten()?.contains('CustomerApi')
    }

    private SystemUser getSystemUser(String credentials) {
        HttpHeaders headers = new HttpHeaders()
        headers.add('Authorization', credentials)
        HttpEntity<String> request = new HttpEntity<String>(headers)
        ResponseEntity<String> response = restTemplate.exchange(authEndpoint, HttpMethod.GET, request, String)
        return new Gson().fromJson(response.getBody(), SystemUser)
    }

}
