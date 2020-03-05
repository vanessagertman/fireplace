package dev.alpas.fireplace.guards

import dev.alpas.fireplace.entities.Project
import dev.alpas.fireplace.entities.Projects
import dev.alpas.ozone.create
import dev.alpas.validation.Min
import dev.alpas.validation.Required
import dev.alpas.validation.ValidationGuard
import dev.alpas.validation.Rule

class CreateProjectGuard : ValidationGuard() {
    override fun rules(): Map<String, Iterable<Rule>> {
        return mapOf(
            "title" to listOf(Required(), Min(8)),
            "description" to listOf(Required())
        )
    }

    fun commit(): Project {
        return  Projects.create{
            it.title to call.param("title")
            it.description to call.param("description")
            it.ownerId to call.user.id
        }
    }
}
