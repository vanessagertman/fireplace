package dev.alpas.fireplace.rules

import dev.alpas.fireplace.entities.ProjectMemberships
import dev.alpas.fireplace.entities.User
import dev.alpas.fireplace.entities.Users
import dev.alpas.http.HttpCall
import dev.alpas.orAbort
import dev.alpas.validation.ErrorMessage
import dev.alpas.validation.Rule
import dev.alpas.validation.ValidationGuard
import me.liuwj.ktorm.dsl.and
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.findOne

class ValidUserRule(private val message: ErrorMessage = null) : Rule() {
    var user: User? = null
        private set

    override fun check(attribute: String, call: HttpCall): Boolean {

        val email = call.stringParam("email").orAbort()
        if(email == call.caller<User>().email){
            error = "You are the owner of the project"
            return false
        }
        user = Users.findOne{ it.email eq email}
        if(user == null) {
            error = "The user must already be registered with ${call.env("APP_NAME")}"
            return false
        }
        val projectId = call.longParam("project").orAbort()
        val membership = ProjectMemberships.findOne { (it.userId eq user!!.id) and (it.projectId eq projectId) }
        if(membership != null) {
            error = "The user is already a member of this project"
            return false
        }
        return true
    }
}

fun ValidationGuard.validUserRule(message: ErrorMessage = null): Rule {
    return rule(ValidUserRule(message))
}
