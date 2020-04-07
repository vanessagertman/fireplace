package dev.alpas.fireplace.controllers

import dev.alpas.fireplace.entities.Tasks
import dev.alpas.fireplace.guards.CreateTasksGuard
import dev.alpas.http.HttpCall
import dev.alpas.orAbort
import dev.alpas.routing.Controller
import me.liuwj.ktorm.dsl.delete
import me.liuwj.ktorm.dsl.eq

class TaskController : Controller() {
    fun store(call: HttpCall) {
       call.validateUsing(CreateTasksGuard::class) {
           val task = commit()
           call.replyAsJson(task)
       }
    }

    fun delete(call: HttpCall) {
        val taskId = call.longParam("id").orAbort()
        Tasks.delete{ it.id eq taskId}
        call.acknowledge()
    }
}
