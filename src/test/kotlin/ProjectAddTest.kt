import dev.alpas.fireplace.database.factories.ProjectFactory
import dev.alpas.fireplace.database.factories.UserFactory
import dev.alpas.fireplace.entities.Activities
import dev.alpas.fireplace.entities.Project
import dev.alpas.fireplace.entities.Projects
import dev.alpas.make
import dev.alpas.ozone.faker
import dev.alpas.pulsar.RefreshDatabase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import dev.alpas.pulsar.trapRedirects
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import dev.alpas.ozone.from
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.findOne
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectAddTest : TestBase(), RefreshDatabase {
    @Test
    fun `unauthenticated user is redirected to login page`() {

        Given{
           trapRedirects()
        } When {

            get("/projects/create")
        } Then{

            assertRedirect("/login", 302)
        }

    }

    @Test
    fun `authenticated user can access the add route`() {

        val user = from(UserFactory(app.make()))
        Given{
            asUser(user)
        } When {

            get("/projects/create")
        } Then{

            assertViewIs("projects_new")
        }
    }

    @Test
    fun `unauthenticated user cannot submit form and is redirected to login page`() {

        Given{
            trapRedirects()
            noCSRFMiddleware()
        } When {
            post("/projects")
        } Then{
            assertRedirect("/login", 302)
        }
    }

    @Test
    fun `project title is required`() {
        val user = from(UserFactory(app.make()))
        Given{
            asUser(user)
            noCSRFMiddleware()
            formParam("description", faker.lorem().paragraph())
        } When {
            post("/projects")
        } Then{
            assertResponseHasErrors(listOf("title"))
            assertResponseHasNoErrors(listOf("description"))
        }
    }

    @Test
    fun `project title must be eight character long minimum`() {
        val user = from(UserFactory(app.make()))
        Given{
            asUser(user)
            noCSRFMiddleware()
            formParam("description", faker.lorem().paragraph())
            formParam("title", "short")
        } When {
            post("/projects")
        } Then{
            assertResponseHasErrors(listOf("title"))
            assertResponseHasNoErrors(listOf("description"))
        }
    }

    @Test
    fun `project description is required`() {
        val user = from(UserFactory(app.make()))
        Given{
            asUser(user)
            noCSRFMiddleware()
            formParam("title", faker.lorem().sentence(5))
        } When {
            post("/projects")
        } Then{
            assertResponseHasNoErrors(listOf("title"))
            assertResponseHasErrors(listOf("description"))
        }
    }

    @Test
    fun `user can create project`() {
        val user = from(UserFactory(app.make()))
        val title = faker.lorem().sentence(5)
        val description = faker.lorem().paragraph()
        Given{
            asUser(user)
            noCSRFMiddleware()
            formParam("title", title)
            formParam("description", description)
        } When {
            post("/projects")
        }
        val project = Projects.findOne { it.ownerId eq user.id }
        assertNotNull(project)
        assertEquals(title, project?.title)
        assertEquals(description, project?.description)

    }

    @Test
    fun `a project created activity is logged when a project is created`() {
        val user = from(UserFactory(app.make()))
        val title = faker.lorem().sentence(5)
        val description = faker.lorem().paragraph()
        Given{
            asUser(user)
            noCSRFMiddleware()
            formParam("title", title)
            formParam("description", description)
        } When {
            post("/projects")
        }
        val project = Projects.findOne { it.ownerId eq user.id }
        val activity = Activities.findOne { it.projectId eq project!!.id }
        assertNotNull(activity)
        assertEquals(user.id, activity?.user?.id)

        assertEquals(activity!!.payload, mapOf("action" to "create project", "title" to title))
    }

    @Test
    fun `user is redirected to project list page after creating a project`() {
        val user = from(UserFactory(app.make()))
        val title = faker.lorem().sentence(5)
        val description = faker.lorem().paragraph()
        Given{
            asUser(user)
            noCSRFMiddleware()
            formParam("title", title)
            formParam("description", description)
        } When {
            post("/projects")
        } Then{
            assertRedirect(routeNamed("projects.list"), 302)
        }
    }
}
