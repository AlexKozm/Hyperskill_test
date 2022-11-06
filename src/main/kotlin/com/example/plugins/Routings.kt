package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(rooms: MutableMap<Int, Room>) {
    //Installs a routing plugin
    routing {
        /**
         * Action on GET:.../create_room
         * Creates room, adds it to a map and redirects to this room
         */
        get("/create_room") {
            Room.getIdAndRoom().also { room -> rooms += room }.first.also { id ->
                call.respondRedirect("/rooms/${id}")
            }
        }

        /**
         * Respond with html page witch represent room or respond with HttpStatusCode.NotFound
         */
        get("/rooms/{id}") {
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                rooms[id]?.let { room ->
                    println("web_socket/$id")
                    call.respondTemplate(
                        "index1.html",
                        mapOf("host_ip" to call.request.local.host,
                            "path" to "web_socket/$id", "messages_on_connection" to room.getAllMessages()
                        )
                    )
                }
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "There is no such room")
        }
    }
}