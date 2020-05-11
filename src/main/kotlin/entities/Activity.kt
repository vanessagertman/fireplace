package dev.alpas.fireplace.entities

import dev.alpas.ozone.*
import me.liuwj.ktorm.jackson.json
import me.liuwj.ktorm.schema.long
import me.liuwj.ktorm.schema.typeRef
import java.time.Instant

interface Activity : OzoneEntity<Activity> {
    var id: Long
    val user: User
    val project: Project
    val task: Task?
    val payload: Map<String, Any?>
    var createdAt: Instant?
    var updatedAt: Instant?

    companion object : OzoneEntity.Of<Activity>()
}

object Activities : OzoneTable<Activity>("activities") {
    val id by bigIncrements()
    val userId by long("user_id").belongsTo(Users){it.user}
    val projectId by long("project_id").belongsTo(Projects){it.project}
    val tasId by long("task_id").nullable().belongsTo(Tasks){it.task}
    val payload by json("action", typeRef<Map<String, Any?>>()).bindTo{it.payload}
    val createdAt by createdAt()
    val updatedAt by updatedAt()
}
