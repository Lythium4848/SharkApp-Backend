package dev.lythium.sharkbackend

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val connectionString = System.getenv("DB_CONNECTION_STRING")
    val username = System.getenv("DB_USERNAME")
    val password = System.getenv("DB_PASSWORD")
    println("Classpath: ${System.getProperty("java.class.path")}")
    if (connectionString == null) {
        println("DB_CONNECTION_STRING environment variable not set")
        exitProcess(1)
    }
    if (username == null) {
        println("DB_USERNAME environment variable not set")
        exitProcess(1)
    }
    if (password == null) {
        println("DB_PASSWORD environment variable not set")

        exitProcess(1)
    }

    Database.connect(connectionString, driver = "org.postgresql.Driver",
        user = username, password = password)

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(FeedItems)
    }

    return io.ktor.server.netty.EngineMain.main(args)
}

@Serializable
class FeedItem(val id: Int = 0, val title: String, val imageUrl: String, var upvotes: Int = 0, var downvotes: Int = 0) {
}

object FeedItems: IntIdTable() {
    val title = varchar("title", 24)
    val imageUrl = varchar("imageUrl", 256)
    val upvotes = integer("upvotes")
    val downvotes = integer("downvotes")
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText("")
        }
    }
}