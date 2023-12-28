package dev.lythium.sharkbackend

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.main() {
    routing {
        get("/feed") {
            val offset = call.request.queryParameters["offset"]?.toLong() ?: 0
            val feedItems = transaction {
                FeedItems.selectAll().limit(20, offset = offset).map {
                    FeedItem(
                        id = it[FeedItems.id].value,
                        title = it[FeedItems.title],
                        imageUrl = it[FeedItems.imageUrl],
                        upvotes = it[FeedItems.upvotes],
                        downvotes = it[FeedItems.downvotes]
                    )
                }
            }

            call.respondText(Json.encodeToString(feedItems), ContentType.Application.Json)
        }
        get("/feed/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)

            val feedItem = transaction {
                FeedItems.select {
                    FeedItems.id eq id
                }.map {
                    FeedItem(
                        id = it[FeedItems.id].value,
                        title = it[FeedItems.title],
                        imageUrl = it[FeedItems.imageUrl],
                        upvotes = it[FeedItems.upvotes],
                        downvotes = it[FeedItems.downvotes]
                    )
                }
            }.firstOrNull() ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respondText(Json.encodeToString(feedItem), ContentType.Application.Json)
        }
        post("/feed/{id}/upvote") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)

            transaction {
                FeedItems.update({ FeedItems.id eq id }) {
                    it[upvotes] = upvotes + 1
                }
            }

            call.respond(HttpStatusCode.OK)
        }
        post("/feed/{id}/downvote") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)

            transaction {
                FeedItems.update({ FeedItems.id eq id }) {
                    it[downvotes] = downvotes + 1
                }
            }

            call.respond(HttpStatusCode.OK)
        }
        post("/feed/new") {
            val feedTitle = call.parameters["title"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val feedImageUrl = call.parameters["imageUrl"] ?: return@post call.respond(HttpStatusCode.BadRequest)

            transaction {
                FeedItems.insert {
                    it[title] = feedTitle
                    it[imageUrl] = feedImageUrl
                    it[upvotes] = 0
                    it[downvotes] = 0
                }
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}