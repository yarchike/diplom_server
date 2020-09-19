package com.martynov

import com.martynov.route.RoutingV1
import io.ktor.application.*
import io.ktor.routing.*
import org.kodein.di.generic.*
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(KodeinFeature){
        bind<RoutingV1>() with eagerSingleton {RoutingV1()}
    }
    install(Routing){
        val routingV1 by kodein().instance<RoutingV1>()
        routingV1.setup(this)
    }



}

