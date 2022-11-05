package com.example.plugins

import com.example.Connection
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.websocket.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet

class Room {
    val connections: MutableSet<Connection> = Collections.synchronizedSet<Connection>(LinkedHashSet())
    val number = lastId.getAndIncrement()
    val messages = Collections.synchronizedList(mutableListOf<Message>())
    companion object {
        val lastId = AtomicInteger(0)
    }

    fun addConnection(connection: Connection) {
        connections += connection
    }
    fun removeConnection(connection: Connection) {
        connections -= connection
    }

    fun addMessage(message: Message) {
        messages += message
    }

    fun getLastMessage(): String =
         if (messages.size != 0) {
            messages.last().toString()
        } else "Write something"

    fun getAllMessages(): String {
        var msg = ""
        for (i in messages) {
            msg += i.toString() + "\n"
        }
        println("getAllMessages: $msg")
        return msg
    }
}

data class Message(
    val time: LocalDateTime,
    val author: String,
    val text: String
) {
    override fun toString() = "<li class=\"received\">" +
            "<span>${time.format(DateTimeFormatter.ofPattern("HH:mm"))}," +
            " ${author}: </span>${text}</li>"
}