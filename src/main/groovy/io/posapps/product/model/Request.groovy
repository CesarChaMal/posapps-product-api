package io.posapps.product.model

import com.amazonaws.services.lambda.runtime.Context
import groovy.transform.ToString

@ToString(includePackage = false)
class Request {
  private final Map input
  private final Context context

  Request(final Map input, final Context context) {
    this.input = input
    this.context = context
  }

  String requestId() {
    return context?.awsRequestId
  }

  String resourcePath() {
    return input?.resource ?: 'unknown'
  }

  String httpMethod() {
    return input?.httpMethod ?: 'unknown'
  }

  String queryString(String name) {
    return input?.queryStringParameters?."${name}"?.trim()
  }

  String pathParameter(String name) {
    return input?.pathParameters?."${name}"?.trim()
  }

  Map<String, String> headers() {
    return input?.headers
  }

  String body() {
    return input?.body ?: null
  }
  String profile() {
    return input?.stageVariables?.profile ?: null
  }

  String dataSourceUrl() {
    return input?.stageVariables?.dataSourceUrl ?: null
  }

  String dataSourceUsername() {
    return input?.stageVariables?.dataSourceUsername ?: null
  }

  String dataSourcePassword() {
    return input?.stageVariables?.dataSourcePassword ?: null
  }
}
