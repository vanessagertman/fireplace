package dev.alpas.fireplace.controllers

import dev.alpas.fireplace.entities.Projects
import dev.alpas.fireplace.entities.User
import dev.alpas.http.HttpCall
import dev.alpas.orAbort
import dev.alpas.ozone.create
import dev.alpas.routing.Controller
import me.liuwj.ktorm.dsl.delete
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.findAll

class ProjectController : Controller() {
    fun index(call: HttpCall) {
        val user = call.caller<User>()
        val projects = user.projects
        call.render("project_list", mapOf("projects" to projects))
    }

    fun create(call: HttpCall) {
        call.render("projects_new")
    }

    fun store(call: HttpCall) {

        //todo: validate inputs
        val project = Projects.create{
            it.title to call.param("title")
            it.description to call.param("description")
            it.ownerId to call.user.id
        }
        flash("success", "Successfully created a project '${project.title}'")
        call.redirect().toRouteNamed("projects.list")
    }

    fun delete(call: HttpCall) {
        val id = call.longParam("id").orAbort()
        Projects.delete { it.id eq id }
        flash("success", "Successfully deleted a project")
        call.redirect().back()
    }
}
