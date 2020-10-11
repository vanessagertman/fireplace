import dev.alpas.fireplace.database.factories.ProjectFactory
import dev.alpas.fireplace.database.factories.UserFactory
import dev.alpas.fireplace.entities.Project
import dev.alpas.make
import dev.alpas.pulsar.RefreshDatabase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import dev.alpas.pulsar.trapRedirects
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import dev.alpas.ozone.from
import org.junit.jupiter.api.Assertions

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectListTest : TestBase(), RefreshDatabase {
    @Test
    fun `home page redirects to project list page`() {

        Given{
           trapRedirects()
        } When {

            get("/home")
        } Then{

            assertRedirect(routeNamed("projects.list"), 302)
        }
    }

    @Test
    fun `unauthenticated user is redirected to login page`() {

        Given{
            trapRedirects()
        } When {

            get("/projects")
        } Then{

            assertRedirect("/login", 302)
        }
    }

    @Test
    fun `an authorized user can access project list page`() {

        Given{
            trapRedirects()
        } When {
            asRandomUser { get("/projects")}
        } Then{
            assertViewIs("project_list")
            assertViewHas(mapOf("projects" to emptyList<Project>()))
        }
    }

    @Test
    fun `user's projects are listed`() {

        val user = from(UserFactory(app.make()))
        val projects = from(ProjectFactory(), 3, mapOf("owner_id" to user.id))
        Given{
            asUser(user)
        } When {
            get("/projects")
        } Then{
            assertViewIs("project_list")
            val returnedProjects = viewArgs()?.get("projects") as? List<Project>
            Assertions.assertEquals(projects.size, returnedProjects!!.size)
            Assertions.assertEquals(projects.map{it.id}, returnedProjects.map { it.id })
        }
    }

    @Test
    fun `other users' projects are not listed`() {

        val user = from(UserFactory(app.make()))
        from(ProjectFactory(), 3, mapOf("owner_id" to from(UserFactory(app.make())).id))
        val projects = from(ProjectFactory(), 3, mapOf("owner_id" to user.id))
        from(ProjectFactory(), 1, mapOf("owner_id" to from(UserFactory(app.make())).id))
        Given{
            asUser(user)
        } When {
            get("/projects")
        } Then{
            assertViewIs("project_list")
            val returnedProjects = viewArgs()?.get("projects") as? List<Project>
            Assertions.assertEquals(projects.size, returnedProjects!!.size)
            Assertions.assertEquals(projects.map{it.id}, returnedProjects.map { it.id })
        }
    }
}
