package com.example.plugins

import com.example.Connection
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import java.util.*

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        val rooms = Collections.synchronizedMap(HashMap<Int, Room?>())

        get("/create_room") {
            val newRoom = Room()
            rooms += Pair(newRoom.number, newRoom)
            call.respondRedirect("/rooms/${newRoom.number}")
        }

        get("/rooms/{id}") {
            println( call.request.local.host)
            val num = call.parameters["id"]?.toInt()
            if (num in rooms.keys) {
                println( "web_socket/$num")
                call.respondTemplate("index.html", mapOf("host_ip" to call.request.local.host,
                    "path" to "web_socket/$num"))
            } else {
                call.respond(status = HttpStatusCode.NotFound, message = "There is no such room")
            }
        }


        webSocket("/web_socket/{id}") {
            val num = call.parameters["id"]?.toIntOrNull()
            val thisConnection = Connection(this)
            val thisRoom = rooms.get(num)
            println("Adding User: ${thisConnection.name} to room ${thisRoom?.number}")
            thisRoom?.addConnection(thisConnection)
            try {
                send("You are connected! There are ${thisRoom?.connections?.count()} users here")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    val textWithUsername = "[${thisConnection.name}]: $receivedText"
                    thisRoom?.connections?.forEach() {
                        it.session.send(textWithUsername)
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                println("removing ${thisConnection.name}!")
                thisRoom?.removeConnection(thisConnection)
            }
        }

//        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
//        webSocket("/ws") {
//            val thisConnection = Connection(this)
//            println("Adding User: ${thisConnection.name}")
//            connections += thisConnection
//            try {
//                send("You are connected! There are ${connections.count()} users here")
//                for (frame in incoming) {
//                    frame as? Frame.Text ?: continue
//                    val receivedText = frame.readText()
//                    val textWithUsername = "[${thisConnection.name}]: $receivedText"
//                    println(textWithUsername)
//                    connections.forEach() {
//                        it.session.send(textWithUsername)
//                    }
//                }
//            } catch (e: Exception) {
//                println(e.localizedMessage)
//            } finally {
//                println("removing ${thisConnection.name}!")
//                connections -= thisConnection
//            }
//        }

    }
}
