package dev.alpas.fireplace.entities

import dev.alpas.auth.Authenticatable
import dev.alpas.auth.UserProvider
import dev.alpas.md5
import dev.alpas.ozone.OzoneEntity
import dev.alpas.ozone.OzoneTable
import dev.alpas.ozone.bigIncrements
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.findById
import me.liuwj.ktorm.entity.findList
import me.liuwj.ktorm.entity.findOne
import me.liuwj.ktorm.schema.timestamp
import me.liuwj.ktorm.schema.varchar
import java.time.Instant

interface User : OzoneEntity<User>, Authenticatable {
    companion object : OzoneEntity.Of<User>()

    override var id: Long
    override var email: String
    override var password: String
    var name: String?
    var createdAt: Instant?
    var updatedAt: Instant?
    var emailVerifiedAt: Instant?

    val projects get() = Projects.findList { it.ownerId eq id }

    override val mustVerifyEmail: Boolean
        get() = true

    override fun isEmailVerified() = emailVerifiedAt != null

    @ExperimentalUnsignedTypes
    fun gravatarUrl(): String {
        val hash = email.trim().toLowerCase().md5()
        return "//www.gravatar.com/avatar/$hash?s=160&d=robohash"
    }
}

object Users : OzoneTable<User>("users"), UserProvider {
    val id by bigIncrements()
    val email by varchar("email").index().unique().bindTo { it.email }
    val password by varchar("password").bindTo { it.password }
    val name by varchar("name").nullable().bindTo { it.name }
    val createdAt by createdAt()
    val updatedAt by updatedAt()
    val emailVerifiedAt by timestamp("email_verified_at").nullable().bindTo { it.emailVerifiedAt }

    override fun findByUsername(username: Any): User? {
        return findOne { it.email.eq(username.toString()) }
    }

    override fun findByPrimaryKey(id: Any): User? {
        return findById(id)
    }
}
