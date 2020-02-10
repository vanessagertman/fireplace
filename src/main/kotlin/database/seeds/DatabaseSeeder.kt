package dev.alpas.fireplace.database.seeds

import dev.alpas.fireplace.database.factories.UserFactory
import dev.alpas.Application
import dev.alpas.make
import dev.alpas.ozone.Seeder
import dev.alpas.ozone.from
import dev.alpas.printAsSuccess

internal class DatabaseSeeder : Seeder() {
    override fun run(app: Application) {
        val user = from(UserFactory(app.make()), "name" to "Jane Doe")
        "Seeded ${user.email}".printAsSuccess()
    }
}
