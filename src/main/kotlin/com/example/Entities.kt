package com.example

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "bar")
open class BarEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    open var entityId: UUID? = null

    @Column(nullable = false)
    open var displayName: String? = null

    @Column(columnDefinition = "JSONB")
    @Convert(converter = FooConverter::class)
    open var foo: Foo? = null
}

@JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
sealed interface Foo

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

object FooConverter : JsonConverter<Foo>(object : TypeReference<Foo>() {})

open class JsonConverter<T>(private val typeReference: TypeReference<T>) : AttributeConverter<T, String> {
    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: T): String? =
        //object mapper writes nulls as the string "null" :(
        if (attribute == null) null else objectMapper.writeValueAsString(attribute)

    override fun convertToEntityAttribute(jsonValue: String?): T? =
        jsonValue?.let {objectMapper.readValue(it, typeReference) }
}





