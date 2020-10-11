package dev.alpas.fireplace.database.factories

import dev.alpas.fireplace.entities.Project
import dev.alpas.fireplace.entities.Projects
import dev.alpas.ozone.EntityFactory
import dev.alpas.ozone.faker
import java.time.Instant

internal class ProjectFactory() : EntityFactory<Project, Projects>() {
    override val table = Projects

    override fun entity(): Project {
        return Project {
            title = faker.lorem().sentence()
            description = faker.lorem().paragraph()
            updatedAt = Instant.now()
            createdAt = Instant.now()
        }
    }
}
