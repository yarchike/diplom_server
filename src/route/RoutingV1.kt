package com.martynov.route

import com.martynov.service.FileService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

class RoutingV1(
        private val staticPath: String,
        private val fileService: FileService
) {
    fun setup(configuration: Routing) {
        with(configuration) {
            get("/") {
                call.respondText("Server working", ContentType.Text.Plain)
            }
            route("/api/v1") {
                static("/static") {
                    files(staticPath)
                }


                route("/media") {
                    post {
                        val multipart = call.receiveMultipart()
                        val response = fileService.save(multipart)
                        call.respond(response)
                    }
                }
            }

        }

    }

}