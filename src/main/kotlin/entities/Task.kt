package dev.alpas.fireplace.entities

import dev.alpas.JsonSerializable
import dev.alpas.JsonSerializer
import dev.alpas.ozone.*
import me.liuwj.ktorm.schema.boolean
import me.liuwj.ktorm.schema.long
import me.liuwj.ktorm.schema.text
import java.time.Instant

interface Task : OzoneEntity<Task>, JsonSerializable {
    var id: Long
    var body: String
    var completed: Boolean
    val project: Project
    var createdAt: Instant?
    var updatedAt: Instant?

    companion object : OzoneEntity.Of<Task>()

    override fun toJson(): String {
        return JsonSerializer.serialize(this)
    }
}

object Tasks : OzoneTable<Task>("tasks") {
    val id by bigIncrements()
    val body by text("body").bindTo{it.body}
    val projectId by long("project_id").belongsTo(Projects){it.project}
    val completed by boolean("completed").default(false).bindTo{it.completed}
    val createdAt by createdAt()
    val updatedAt by updatedAt()
}
