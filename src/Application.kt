package com.martynov

import com.martynov.route.RoutingV1
import com.martynov.service.FileService
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.util.*
import org.kodein.di.generic.*
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein
import javax.naming.ConfigurationException


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress(" ") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(KodeinFeature){
        constant(tag = "upload-dir") with (environment.config.propertyOrNull("myv.upload.dir")?.getString()
                ?: throw ConfigurationException("Upload dir is not specified")
                )
        bind<RoutingV1>() with eagerSingleton {RoutingV1(instance(tag = "upload-dir"), instance())}
        bind<FileService>() with eagerSingleton { FileService(instance(tag = "upload-dir")) }
    }
    install(Routing){
        val routingV1 by kodein().instance<RoutingV1>()
        routingV1.setup(this)
    }



}

