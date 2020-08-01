package dev.alpas.fireplace.controllers

import dev.alpas.fireplace.entities.Activities
import dev.alpas.fireplace.entities.Project
import dev.alpas.fireplace.entities.Projects
import dev.alpas.fireplace.entities.User
import dev.alpas.fireplace.guards.CreateProjectGuard
import dev.alpas.http.HttpCall
import dev.alpas.orAbort
import dev.alpas.ozone.create
import dev.alpas.ozone.findOrFail
import dev.alpas.routing.Controller
import me.liuwj.ktorm.dsl.delete
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.insert
import me.liuwj.ktorm.entity.findAll

class ProjectController : Controller() {
    fun index(call: HttpCall) {
        val user = call.caller<User>()
        val projects = user.projects
        val membershipProjects = user.projectMembership
        call.render("project_list", mapOf("projects" to projects, "membershipProjects" to membershipProjects))
    }

    fun create(call: HttpCall) {
        call.render("projects_new")
    }

    fun store(call: HttpCall) {
        call.validateUsing(CreateProjectGuard::class){
            val project = commit()
            logCreateProjectActivity(project, mapOf("action" to "create project", "title" to project.title))
            flash("success", "Successfully created a project '${project.title}'")
        }
        call.redirect().toRouteNamed("projects.list")
    }

    private fun logCreateProjectActivity(project: Project, payload: Map<String, Any?>) {
        val now = call.nowInCurrentTimezone().toInstant()
        val user = caller<User>()
        Activities.insert {
            it.payload to payload
            it.projectId to project.id
            it.userId to user.id
            it.createdAt to now
            it.updatedAt to now
        }
    }

    fun delete(call: HttpCall) {
        val id = call.longParam("id").orAbort()
        Projects.delete { it.id eq id }
        flash("success", "Successfully deleted a project")
        call.redirect().back()
    }

    fun show(call: HttpCall) {
        val id = call.longParam("id").orAbort()
        val project = Projects.findOrFail(id)
        call.render("project_show", mapOf("project" to project))
    }
}
