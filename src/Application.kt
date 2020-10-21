package com.martynov

import com.martynov.exception.PasswordChangeException
import com.martynov.exception.UserAddException
import com.martynov.repository.IdeaRepository
import com.martynov.repository.IdeaRepositoryMutex
import com.martynov.repository.UserRepository
import com.martynov.repository.UserRepositoryInMemoryWithMutexImpl
import com.martynov.route.RoutingV1
import com.martynov.service.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.util.*
import org.kodein.di.generic.*
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import javax.naming.ConfigurationException


fun main(args: Array<String>) {
    EngineMain.main(args)
}

@KtorExperimentalAPI
@Suppress(" ") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }

    install(KodeinFeature) {
        constant(tag = "fcm-password") with (environment.config.propertyOrNull("myv.fcm.password")?.getString()
            ?: throw ConfigurationException("FCM Password is not specified"))
        constant(tag = "fcm-salt") with (environment.config.propertyOrNull("myv.fcm.salt")?.getString()
            ?: throw ConfigurationException("FCM Salt is not specified"))
        constant(tag = "fcm-db-url") with (environment.config.propertyOrNull("myv.fcm.db-url")?.getString()
            ?: throw ConfigurationException("FCM DB Url is not specified"))
        constant(tag = "fcm-path") with (environment.config.propertyOrNull("myv.fcm.path")?.getString()
            ?: throw ConfigurationException("FCM JSON Path is not specified"))
        constant(tag = "upload-dir") with (environment.config.propertyOrNull("myv.upload.dir")?.getString()
            ?: throw ConfigurationException("Upload dir is not specified")
                )
        constant(tag = "upload-user") with (environment.config.propertyOrNull("myv.uploadUser.dir")?.getString()
            ?: throw ConfigurationException("Upload dir is not specified")
                )
        bind<RoutingV1>() with eagerSingleton {
            RoutingV1(
                instance(tag = "upload-dir"),
                instance(tag = "upload-user"),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
        bind<FileService>() with eagerSingleton {
            FileService(
                instance(tag = "upload-dir"),
                instance(tag = "upload-user")
            )
        }
        bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }
        bind<JWTTokenService>() with eagerSingleton { JWTTokenService() }
        bind<UserService>() with eagerSingleton { UserService(instance(), instance(), instance()) }
        bind<IdeaService>() with eagerSingleton { IdeaService(instance(), instance()) }
        bind<UserRepository>() with eagerSingleton { UserRepositoryInMemoryWithMutexImpl() }
        bind<IdeaRepository>() with singleton { IdeaRepositoryMutex() }
        bind<FCMService>() with eagerSingleton {
            FCMService(
                instance(tag = "fcm-db-url"),
                instance(tag = "fcm-password"),
                instance(tag = "fcm-salt"),
                instance(tag = "fcm-path")
            )
        }

    }
    install(Authentication) {
        jwt {
            val jwtServisce by kodein().instance<JWTTokenService>()
            verifier(jwtServisce.verifier)
            val userService by kodein().instance<UserService>()

            validate {
                val id = it.payload.getClaim("id").asLong()
                userService.getModelByid(id)
            }
        }
    }
    install(Routing) {
        val routingV1 by kodein().instance<RoutingV1>()
        routingV1.setup(this)
    }

    install(StatusPages) {
        exception<UserAddException> { e ->
            call.respond(
                HttpStatusCode.BadRequest,
                Error("\"error\": Пользователь с таким логином уже зарегистрирован")
            )
            throw e
        }
        exception<PasswordChangeException> { e ->
            call.respond(HttpStatusCode.BadRequest, Error("Неверный пароль"))
        }
        exception<PasswordChangeException> { e ->
            call.respond(HttpStatusCode.BadRequest, Error("Неверный пароль"))
        }


    }


}

