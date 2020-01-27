package io.posapps.product.model

class SystemUser {
  Long id
  String emailAddress
  String password
  List<Subscription> subscriptions
  List<ApiConfiguration> apiConfigurations
}
