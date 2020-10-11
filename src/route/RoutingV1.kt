package com.martynov.route

import com.martynov.dto.AuthenticationRequestDto
import com.martynov.dto.AutorIdeaRequest
import com.martynov.dto.IdeaResponseDto
import com.martynov.dto.RegistrationRequestDto
import com.martynov.model.IdeaModel
import com.martynov.model.UserModel
import com.martynov.repository.IdeaRepository
import com.martynov.service.FileService
import com.martynov.service.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein

class RoutingV1(
    private val staticPath: String,
    private val staticPathUser: String,
    private val fileService: FileService,
    private val userService: UserService
) {
    fun setup(configuration: Routing) {
        with(configuration) {
            val repo by kodein().instance<IdeaRepository>()
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
                post("/authentication") {
                    val input = call.receive<AuthenticationRequestDto>()
                    val response = userService.authenticate(input)
                    call.respond(response)
                }
                post("/registration") {
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
                authenticate {


                    post("/ideas/new") {
                        val request = call.receive<IdeaResponseDto>()
                        val me = call.authentication.principal<UserModel>()
                        val autor = AutorIdeaRequest(id = me?.id, username = me?.username, attachment = me?.attachment)
                        val response = repo.newIdea(
                            IdeaModel(
                                autor = autor,
                                date = request.date,
                                ideaText = request.ideaText,
                                attachment = request.attachment,
                                like = request.like,
                                disLike = request.disLike
                            )
                        ) ?: throw NotFoundException()
                        call.respond(response)
                    }
                    get("/ideas") {
                        val me = call.authentication.principal<UserModel>()
                        val response = repo.getAll(me?.id)
                        call.respond(response)
                    }
                    post("ideas/{id}/like") {
                        val id =
                            call.parameters["id"]?.toLongOrNull()
                                ?: throw ParameterConversionException("id", "Long")
                        val me = call.authentication.principal<UserModel>()
                        val response = repo.like(id, me?.id) ?: throw NotFoundException()
                        call.respond(response)

                    }
                    post("ideas/{id}/dislike") {
                        val id =
                            call.parameters["id"]?.toLongOrNull()
                                ?: throw ParameterConversionException("id", "Long")
                        val me = call.authentication.principal<UserModel>()
                        val response = repo.disLike(id, me?.id) ?: throw NotFoundException()
                        call.respond(response)

                    }
                }
            }


        }

    }

}