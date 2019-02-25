package com.github.mduesterhoeft.router

class Router {

    private val routes = mutableMapOf<RequestPredicate ,RouterFunction<*, *>>()

    fun <I, T> GET(pattern: String, handlerFunction: HandlerFunction<I, T>) =
        append(handlerFunction, RequestPredicate(method = "GET", pathPattern = pattern, consumes = emptySet()))

    fun <I, T> POST(pattern: String, handlerFunction: HandlerFunction<I, T>) =
        append(handlerFunction, RequestPredicate("POST", pattern))

    fun <I, T> PUT(pattern: String, handlerFunction: HandlerFunction<I, T>) =
        append(handlerFunction, RequestPredicate("PUT", pattern))

    fun <I, T> DELETE(pattern: String, handlerFunction: HandlerFunction<I, T>) =
        RequestPredicate("DELETE", pattern)
            .also { append(handlerFunction, it) }

    companion object {
        fun router(routes: Router.() -> Unit) = Router().apply(routes)
    }

    private fun <I, T> append(handlerFunction: HandlerFunction<I, T>, pred: RequestPredicate) =
        routes.merge(pred, RouterFunction(pred, handlerFunction)) { _, _ -> throw RuntimeException("pattern already registered") }

    fun findExactRouterFunction(request: ApiRequest) = routes.values.find { it.requestPredicate.match(request).match }

    fun findNonMatchingMatches(request: ApiRequest) = routes.values.map { it.requestPredicate.match(request) }.filter { !it.match }

}

typealias HandlerFunction<I, T> = (request: Request<I>) -> ResponseEntity<T>

data class RouterFunction<I, T>(
    val requestPredicate: RequestPredicate,
    val handler: HandlerFunction<I, T>
)

data class Request<I>(val apiRequest: ApiRequest, val body: I)
