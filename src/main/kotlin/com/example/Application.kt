package com.example

import com.example.plugins.*
import freemarker.cache.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import java.time.Duration
import java.util.*

fun main() {
    // stores all rooms
    val rooms = Collections.synchronizedMap(HashMap<Int, Room>())
    //configuration of server's parameters
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", ) {
        installModules()
        configureRouting(rooms)
        configureSockets(rooms)
    }.start(wait = true)
}

/**
 * install modules
 */
fun Application.installModules() {
    // installing FreeMarker
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "client")
    }
    // installing WebSocket
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}