package dev.alpas.fireplace.controllers

import dev.alpas.fireplace.entities.*
import dev.alpas.fireplace.guards.CreateTasksGuard
import dev.alpas.fireplace.guards.UpdateTasksGuard
import dev.alpas.http.HttpCall
import dev.alpas.orAbort
import dev.alpas.routing.Controller
import me.liuwj.ktorm.dsl.delete
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.insert

class TaskController : Controller() {
    fun store(call: HttpCall) {
       call.validateUsing(CreateTasksGuard::class) {
           val task = commit()
           logTaskActivity(task, mapOf("action" to "created a task", "title" to task.body))
           call.replyAsJson(task)
       }
    }

    fun delete(call: HttpCall) {
        val taskId = call.longParam("id").orAbort()
        Tasks.delete{ it.id eq taskId}
        call.acknowledge()
    }

    fun update(call: HttpCall) {
        call.validateUsing(UpdateTasksGuard::class) {
            val task = commit()
            logTaskActivity(task, mapOf("action" to "updated a task", "title" to task.body))
            call.acknowledge()
        }
    }

    private fun logTaskActivity(task: Task, payload: Map<String, Any?>) {
        val now = call.nowInCurrentTimezone().toInstant()
        val user = caller<User>()
        Activities.insert {
            it.payload to payload
            it.projectId to task.project.id
            it.tasId to task.id
            it.userId to user.id
            it.createdAt to now
            it.updatedAt to now
        }
    }
}
