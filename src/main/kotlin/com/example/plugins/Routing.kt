package com.example.plugins

import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import java.io.File

fun Application.configureRouting() {

    routing {
        get("/") {
            println( call.request.local.host)
            call.respondTemplate("index.html", mapOf("host_ip" to call.request.local.host,
                "path" to "web_socket/0"))
        }


    }
}
