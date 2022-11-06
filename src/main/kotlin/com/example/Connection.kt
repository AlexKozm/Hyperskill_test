package com.example

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Saves session with unique id
 */
class Connection (val session: DefaultWebSocketSession) {
    val name = "user${lastId.getAndIncrement()}"

    companion object {
        val lastId = AtomicInteger(0)
    }
}