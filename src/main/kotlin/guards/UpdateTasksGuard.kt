package dev.alpas.fireplace.guards

import dev.alpas.fireplace.entities.Task
import dev.alpas.fireplace.entities.Tasks
import dev.alpas.orAbort
import dev.alpas.ozone.findOrFail
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.update

class UpdateTasksGuard : CreateTasksGuard() {

    override fun commit(): Task {
        val id = call.longParam("id").orAbort()
        Tasks.update {
            it.body to call.jsonBody?.get("body")
            val completed = call.jsonBody?.get("completed")
            if(completed != null) {
                it.completed to completed
            }
            where {it.id eq id}
        }
        return Tasks.findOrFail(id)
    }
}
