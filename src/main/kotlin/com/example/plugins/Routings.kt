package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutings(rooms: MutableMap<Int, Room>) {
    //Installs a routing plugin
    routing {
        /**
         * Action on GET:.../create_room
         * Creates room, adds it to a map and redirects to this room
         */
        get("/create_room") {
            val newRoom = Room()
            rooms += Pair(newRoom.number, newRoom)
            call.respondRedirect("/rooms/${newRoom.number}")
        }

        /**
         * Respond with html page witch represent room/{id} or HttpStatusCode.NotFound
         */
        get("/rooms/{id}") {
            println(call.request.local.host)
            val num = call.parameters["id"]?.toInt()
            if (num in rooms.keys) {
                println("web_socket/$num")
                call.respondTemplate(
                    "index1.html",
                    mapOf("host_ip" to call.request.local.host,
                        "path" to "web_socket/$num", "messages_on_connection" to rooms[num]?.getAllMessages()
                    )
                )
            } else {
                call.respond(status = HttpStatusCode.NotFound, message = "There is no such room")
            }
        }
    }
}