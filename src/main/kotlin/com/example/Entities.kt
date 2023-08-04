package com.example

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "project")
class ProjectEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    var entityId: UUID? = null

    @Column(nullable = false)
    var displayName: String? = null

    @Column(columnDefinition = "JSONB")
    @Convert(converter = BarConverter::class)
    var bar: Bar? = null
}

@Entity
@Table(name = "task")
class TaskEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    var entityId: UUID? = null

    @Column(nullable = false)
    var displayName: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    var project: ProjectEntity? = null
}

data class Bar(val foo: Foo)

@JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
sealed interface Foo {
}

data class Foo1(val name: String) : Foo
class Foo2 : Foo

class Foo3 : Foo {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

inline fun <reified T> getTypeRef(): TypeReference<T> =
    object : TypeReference<T>() { }
object BarConverter : JsonConverter<Bar?>(getTypeRef())

open class JsonConverter<T>(private val typeReference: TypeReference<T>) : AttributeConverter<T, String> {
    private val objectMapper = jacksonObjectMapper()//.registerModule(JavaTimeModule())

    override fun convertToDatabaseColumn(attribute: T): String? =
        //object mapper writes nulls as the string "null" :(
        if (attribute == null) null else objectMapper.writeValueAsString(attribute)

    override fun convertToEntityAttribute(jsonValue: String?): T? =
        jsonValue?.let {objectMapper.readValue(it, typeReference) }
}





