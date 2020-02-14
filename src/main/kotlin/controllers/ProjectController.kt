package dev.alpas.fireplace.controllers

import dev.alpas.fireplace.entities.Projects
import dev.alpas.http.HttpCall
import dev.alpas.routing.Controller
import me.liuwj.ktorm.entity.findAll

class ProjectController : Controller() {
    fun index(call: HttpCall) {
        val projects = Projects.findAll()
        call.render("project_list", mapOf("projects" to projects))
    }

}
