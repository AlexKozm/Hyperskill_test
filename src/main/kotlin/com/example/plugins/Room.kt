package com.example.plugins

import com.example.Connection
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet

/**
 * Stores all messages, connections
 */
class Room {
    val connections: MutableSet<Connection> = Collections.synchronizedSet(LinkedHashSet())
    val messages = Collections.synchronizedList(mutableListOf<Message>())
    companion object {
        private val lastId = AtomicInteger(0)
        fun getIdAndRoom(): Pair<Int, Room> =
            Pair(lastId.getAndIncrement(), Room())
    }

    /**
     * Constructor is private. Use getIdAndRoom()
     */
    private constructor()

    fun addConnection(connection: Connection) {
        connections += connection
    }
    fun removeConnection(connection: Connection) {
        connections -= connection
    }

    fun addMessage(message: Message) {
        messages += message
    }

    /**
     * Returns last message in html format ar greeting
     */
    fun getLastMessage(): String =
         if (messages.size != 0) {
            messages.last().toString()
        } else Message(text = "Hello! Write something").toString()

    /**
     *  Returns all messages in html format divided by '\n'
     */
    fun getAllMessages(): String {
        var msg = ""
        for (i in messages) {
            msg += i.toString() + "\n"
        }
        return if (msg != "") msg else Message(text = "Hello! Write something").toString()
    }
}

/**
 * Stores time, author and text of a message
 */
data class Message(
    val time: LocalDateTime? = null,
    val author: String? = null,
    val text: String
) {
    override fun toString() = "<li class=\"received\">" +
            "<span>${time?.run { format(DateTimeFormatter.ofPattern("HH:mm")).toString() + ", " } ?: ""}" +
            " ${author?.let { "$it: " } ?: ""}</span>${text}</li>"

}