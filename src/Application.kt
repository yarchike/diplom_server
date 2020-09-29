package com.martynov

import com.martynov.exception.UserAddException
import com.martynov.repository.UserRepository
import com.martynov.repository.UserRepositoryInMemoryWithMutexImpl
import com.martynov.route.RoutingV1
import com.martynov.service.FileService
import com.martynov.service.JWTTokenService
import com.martynov.service.UserService
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
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.with
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

        constant(tag = "upload-dir") with (environment.config.propertyOrNull("myv.upload.dir")?.getString()
                ?: throw ConfigurationException("Upload dir is not specified")
                )
        constant(tag = "upload-user") with (environment.config.propertyOrNull("myv.uploadUser.dir")?.getString()
            ?: throw ConfigurationException("Upload dir is not specified")
                )
        bind<RoutingV1>() with eagerSingleton { RoutingV1(instance(tag = "upload-dir"),instance(tag = "upload-user"), instance(), instance()) }
        bind<FileService>() with eagerSingleton { FileService(instance(tag = "upload-dir"), instance(tag = "upload-user")) }
        bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }
        bind<JWTTokenService>() with eagerSingleton { JWTTokenService() }
        bind<UserService>() with eagerSingleton { UserService(instance(), instance(), instance()) }
        bind<UserRepository>() with eagerSingleton { UserRepositoryInMemoryWithMutexImpl() }
    }
    install(Routing) {
        val routingV1 by kodein().instance<RoutingV1>()
        routingV1.setup(this)
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
    install(StatusPages) {
        exception<UserAddException> { e ->
            call.respond(HttpStatusCode.BadRequest, Error("\"error\": Пользователь с таким логином уже зарегистрирован"))
            throw e
        }
    }


}

