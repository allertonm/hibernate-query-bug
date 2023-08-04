package com.example

import io.quarkus.hibernate.reactive.panache.common.runtime.SessionOperations
import io.smallrye.mutiny.Uni
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import java.util.*

@Path("/")
class ExampleResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hello")
    fun hello() = "Hello from RESTEasy Reactive"

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/create1")
    fun create1(): Uni<String> {
        return SessionOperations.withTransaction { tx ->
            SessionOperations.withSession { session ->
                val bar = BarEntity()
                bar.displayName = "Bar1"
                bar.foo = Foo1("hello")
                session.persistAll(bar).map { "Created ${bar.entityId}" }
            }
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/create2")
    fun create2(): Uni<String> {
        return SessionOperations.withTransaction { tx ->
            SessionOperations.withSession { session ->
                val bar = BarEntity()
                bar.displayName = "Bar2"
                bar.foo = Foo2()
                session.persistAll(bar).map { "Created ${bar.entityId}" }
            }
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/create3")
    fun create3(): Uni<String> {
        return SessionOperations.withTransaction { tx ->
            SessionOperations.withSession { session ->
                val bar = BarEntity()
                bar.displayName = "Bar2"
                bar.foo = Foo3()
                session.persistAll(bar).map { "Created ${bar.entityId}" }
            }
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/read/{id}")
    fun read(id: UUID): Uni<String> {
        return SessionOperations.withSession { session ->
            session.createQuery(
                """
                from BarEntity p where p.entityId = :id
                """.trimIndent(),
                BarEntity::class.java)
                .setParameter("id", id)
                .resultList
                .flatMap { projects ->
                    projects.first().let { project ->
                        session.createQuery(
                            """
                            from BarEntity p where p.entityId = :id
                            """.trimIndent(),
                            BarEntity::class.java)
                            .setParameter("id", project.entityId)
                            .resultList
                            .map { projects ->
                                projects.first().displayName
                            }
                    }
                }
        }
    }
}
