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
         * Setting WS connection to room with certain id
         */
        webSocket("/web_socket/{id}") {
            call.parameters["id"]?.toIntOrNull()?.let { num ->
                rooms[num]?.let { room ->
                    Connection(this).apply {
                        room.addConnection(this)
                        println("User: $name added to room $num")
                        onIncoming(room, this)
                        println("removing $name!")
                        room.removeConnection(this)
                    }
                }
            } ?: send("There is no WS with such id. Closing")
        }
    }
}

/**
 * waits for messages, then saves it in the room and retransmits to all room's connectors
 */
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
