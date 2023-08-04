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
                val project = ProjectEntity()
                project.displayName = "MyProject1"
                project.bar = Bar(Foo1("hello"))
                val task = TaskEntity()
                task.displayName = "MyTask1"
                task.project = project
                session.persistAll(project, task).map { "Created task with ID ${task.entityId} in project ${project.entityId}" }
            }
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/create2")
    fun create2(): Uni<String> {
        return SessionOperations.withTransaction { tx ->
            SessionOperations.withSession { session ->
                val project = ProjectEntity()
                project.displayName = "MyProject2"
                project.bar = Bar(Foo2())
                val task = TaskEntity()
                task.displayName = "MyTask2"
                task.project = project
                session.persistAll(project, task).map { "Created task with ID ${task.entityId} in project ${project.entityId}" }
            }
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/create3")
    fun create3(): Uni<String> {
        return SessionOperations.withTransaction { tx ->
            SessionOperations.withSession { session ->
                val project = ProjectEntity()
                project.displayName = "MyProject3"
                project.bar = Bar(Foo3())
                val task = TaskEntity()
                task.displayName = "MyTask3"
                task.project = project
                session.persistAll(project, task).map { "Created task with ID ${task.entityId} in project ${project.entityId}" }
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
                from TaskEntity t 
                join fetch t.project
                where t.entityId = :id
                """.trimIndent(),
                TaskEntity::class.java)
                .setParameter("id", id)
                .resultList
                .flatMap { tasks ->
                    tasks.first().let { task ->
                        session.createQuery(
                            """
                            from ProjectEntity p where p.entityId = :id
                            """.trimIndent(),
                            ProjectEntity::class.java)
                            .setParameter("id", task.project!!.entityId)
                            .resultList
                            .map { projects ->
                                projects.first().displayName
                            }
                    }
                }
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/read2/{id}")
    fun read2(id: UUID): Uni<String> {
        return SessionOperations.withSession { session ->
            session.createQuery(
                """
                from ProjectEntity p where p.entityId = :id
                """.trimIndent(),
                ProjectEntity::class.java)
                .setParameter("id", id)
                .resultList
                .flatMap { projects ->
                    projects.first().let { project ->
                        session.createQuery(
                            """
                            from ProjectEntity p where p.entityId = :id
                            """.trimIndent(),
                            ProjectEntity::class.java)
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
