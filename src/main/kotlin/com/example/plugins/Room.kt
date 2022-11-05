package com.example.plugins

import com.example.Connection
import io.ktor.server.routing.*
import io.ktor.websocket.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet

class Room {
    companion object {
        val lastId = AtomicInteger(0)
    }
    constructor(connection: Connection?) {
        addConnection(connection)
    }

    constructor()

    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    val number = lastId.getAndIncrement()

    fun addConnection(connection: Connection?) {
        connections += connection
    }
    fun removeConnection(connection: Connection?) {
        connections -= connection
    }
}