package com.martynov.route

import com.martynov.dto.AuthenticationRequestDto
import com.martynov.dto.RegistrationRequestDto
import com.martynov.exception.UserAddException
import com.martynov.service.FileService
import com.martynov.service.UserService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

class RoutingV1(
        private val staticPath: String,
        private val staticPathUser: String,
        private val fileService: FileService,
        private val userService: UserService
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
                static("/static/user") {
                    files(staticPathUser)
                }
                post("/authentication"){
                    val input = call.receive<AuthenticationRequestDto>()
                    val response = userService.authenticate(input)
                    call.respond(response)
                }
                post("/registration"){
                    val input = call.receive<RegistrationRequestDto>()
                    val response = userService.registration(input)
                    call.respond(response)
                }
                post("/media") {
                    val multipart = call.receiveMultipart()
                    val response = fileService.save(multipart)
                    call.respond(response)

                }
                post("/media/user") {
                    val multipart = call.receiveMultipart()
                    val response = fileService.saveUser(multipart)
                    call.respond(response)

                }
            }

        }

    }

}