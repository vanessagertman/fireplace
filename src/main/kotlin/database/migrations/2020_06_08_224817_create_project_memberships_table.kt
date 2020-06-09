package dev.alpas.fireplace.database.migrations

import dev.alpas.fireplace.entities.ProjectMemberships
import dev.alpas.ozone.migration.Migration

class CreateProjectMembershipsTable : Migration() {
    override var name = "2020_06_08_224817_create_project_memberships_table"
    
    override fun up() {
        createTable(ProjectMemberships)
    }
    
    override fun down() {
        dropTable(ProjectMemberships)
    }
}