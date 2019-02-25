package com.github.mduesterhoeft.router

import assertk.assert
import assertk.assertions.containsAll
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.catch
import com.github.mduesterhoeft.router.Router.Companion.router
import org.junit.jupiter.api.Test

class RouterTest {

    @Test
    fun `should register get route with default accept header`() {
        val router = router {
            GET("/some") { r: Request<Unit> ->
                ResponseEntity.ok("""{"hello": "world", "request":"${r.body}"}""")
            }
        }

        val request = ApiRequest(
            path = "/some",
            httpMethod = "GET",
            headers = mutableMapOf("accept" to "application/json")
        )
        assert(router.findExactRouterFunction(request)).isNotNull()
        with(router.findExactRouterFunction(request)!!.requestPredicate) {
            assert(method).isEqualTo("GET")
            assert(pathPattern).isEqualTo("/some")
            assert(consumes).isEmpty()
            assert(produces).containsAll("application/json", "application/x-protobuf")
        }
    }

    @Test
    fun `should throw when route is defined twice`() {
        val exception = catch {
            router {
                GET("/some") { _: Request<Unit> -> ResponseEntity.ok("first") }
                GET("/some") { _: Request<Unit> -> ResponseEntity.ok("second") }
            }
        }
        assert(exception).isNotNull()
    }

}