package com.martynov.route

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import javax.naming.ConfigurationException

class RoutingV1() {
    fun setup(configuration: Routing) {
        with(configuration) {
            get("/") {
                call.respondText("Server working", ContentType.Text.Plain)
            }
            route("/api/v1") {
                get("/") {
                    call.respondText("Server working", ContentType.Text.Plain)
                }
            }

        }

    }
}