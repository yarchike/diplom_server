package com.martynov.route

import com.google.gson.Gson
import com.martynov.FILE_LOG
import com.martynov.dto.*
import com.martynov.model.AttachmentModel
import com.martynov.model.UserModel
import com.martynov.repository.IdeaRepository
import com.martynov.repository.UserRepository
import com.martynov.service.FCMService
import com.martynov.service.FileService
import com.martynov.service.IdeaService
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
import java.io.File

class RoutingV1(
    private val staticPath: String,
    private val staticPathUser: String,
    private val fileService: FileService,
    private val userService: UserService,
    private val ideaService: IdeaService,
    private val fcmService: FCMService
) {
    fun setup(configuration: Routing) {
        with(configuration) {
            val repo by kodein().instance<IdeaRepository>()
            val repoUser by kodein().instance<UserRepository>()
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
                        call.respond(ideaService.getNewIdea(request, me))
                    }
                    get("/ideas") {
                        val me = call.authentication.principal<UserModel>()
                        val response = ideaService.getAllIdea(me?.id)
                        call.respond(response)
                    }
                    get("/ideas/{id}") {
                        val id = call.parameters["id"]?.toLongOrNull()
                        val model = ideaService.getIdeaId(id) ?: throw NotFoundException()
                        call.respond(model)

                    }
                    post("/ideas/count") {
                        val request = call.receive<Long>()
                        val me = call.authentication.principal<UserModel>()
                        val response = ideaService.getCountIdea(me?.id, request)
                        call.respond(response)
                    }
                    post("ideas/{id}/like") {
                        val id =
                            call.parameters["id"]?.toLongOrNull()
                                ?: throw ParameterConversionException("id", "Long")
                        val me = call.authentication.principal<UserModel>()
                        val response = ideaService.like(id, me) ?: throw NotFoundException()
                        if (me != null) {
                            fcmService.send(
                                id,
                                userService.findTokenDeviceUser(response.autor.id),
                                "Одобрение",
                                "Вашу идею одобрил ${me.username} "
                            )
                        }
                        call.respond(response)

                    }
                    post("ideas/{id}/dislike") {
                        val id =
                            call.parameters["id"]?.toLongOrNull()
                                ?: throw ParameterConversionException("id", "Long")
                        val me = call.authentication.principal<UserModel>()
                        val response = ideaService.dislike(id, me) ?: throw NotFoundException()
                        if (me != null) {
                            fcmService.send(
                                id,
                                userService.findTokenDeviceUser(response.autor.id),
                                "Неодобрение",
                                "Вашу идею неодобрил ${me.username}"
                            )
                        }
                        call.respond(response)

                    }

                    get("/me") {
                        val me = call.authentication.principal<UserModel>()
                        call.respond(AutorIdeaRequest.fromModel(me!!))
                    }
                    post("user/changePassword") {
                        val request = call.receive<PasswordChangeRequestDto>()
                        val me = call.authentication.principal<UserModel>()
                        if (me != null) {
                            val response = userService.changePassword(me.id, request)
                            call.respond(response)
                        }
                    }
                    post("user/changeImage") {
                        val request = call.receive<AttachmentModel>()
                        val me = call.authentication.principal<UserModel>()
                        File(FILE_LOG).writeText(Gson().toJson(me))
                        if (me != null) {
                            val response = repoUser.userChangeImg(me.id, request)
                            call.respond(response)

                        }
                    }
                    post("/push") {
                        val input = call.receive<TokenDeviceDto>()
                        val input2 = call.request.header("Id")?.toLong()
                        val user = userService.addTokenDevice(input2, input.token)
                        call.respond(user)

                    }
                }

            }


        }

    }

}