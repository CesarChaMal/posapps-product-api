package io.posapps.product.endpoint

import io.posapps.product.model.Request
import io.posapps.product.model.Response

abstract class Endpoint {
    public static final DEVICE  = 'device'
    public static final SKU     = 'sku'
    public static final CREATED = 'created'
    public static final DELETED = 'deleted'
    public static final UPDATED = 'updated'

    public static final POST    = 'POST'
    public static final GET     = 'GET'
    public static final DELETE  = 'DELETE'
    public static final PUT     = 'PUT'

    abstract boolean route(Request request)
    abstract Response respond(Request request)
}
