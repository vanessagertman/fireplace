
package dev.alpas.fireplace.entities

import dev.alpas.ozone.*
import me.liuwj.ktorm.schema.long
import me.liuwj.ktorm.schema.text
import java.time.Instant

interface Project : OzoneEntity<Project> {
    var id: Long
    val title: String
    val description: String
    val notes: String?
    val owner: User
    val tasks get() = hasMany(Tasks)
    val activities get() = hasMany(Activities)
    val members get() = hasMany(ProjectMemberships).map{it.member}
    var createdAt: Instant?
    var updatedAt: Instant?

    companion object : OzoneEntity.Of<Project>()
}


object Projects : OzoneTable<Project>("projects") {
    val id by bigIncrements()
    val title by mediumText("title").bindTo { it.title }
    val description by text("description").bindTo { it.description }
    val notes by text("notes").nullable().bindTo { it.notes }
    val ownerId by long("owner_id").belongsTo(Users) { it.owner }
    val createdAt by createdAt()
    val updatedAt by updatedAt()
}
