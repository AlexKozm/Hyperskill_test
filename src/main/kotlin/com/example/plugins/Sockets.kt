package com.example.plugins

import com.example.Connection
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import java.time.LocalDateTime

fun Application.configureSockets(rooms: MutableMap<Int, Room>) {
    routing {
        /**
         * Setting WS connection with id room
         */
        webSocket("/web_socket/{id}") {
            val thisRoom = call.parameters["id"]?.toIntOrNull()?.let {num -> rooms[num] } ?:
                send("There is no WS with such id. Closing").run { return@webSocket }
            val thisConnection = Connection(this).also { thisRoom.addConnection(it) }
            println("User: ${thisConnection.name} added to room ${thisRoom.number}")
            send("You are connected! There are ${thisRoom.connections.count()} users here now")
            onIncoming(thisRoom, thisConnection)
            println("removing ${thisConnection.name}!")
            thisRoom.removeConnection(thisConnection)
        }
    }
}

suspend fun DefaultWebSocketServerSession.onIncoming(thisRoom: Room, thisConnection: Connection) {
    for (frame in incoming) {
        frame as? Frame.Text ?: continue
        val receivedText = frame.readText()
        thisRoom.addMessage(Message(LocalDateTime.now(), thisConnection.name, receivedText))
        thisRoom.connections.forEach {
            it.session.send(thisRoom.getLastMessage())
        }
    }
}
