package dev.alpas.fireplace

import dev.alpas.auth.authRoutes
import dev.alpas.fireplace.controllers.WelcomeController
import dev.alpas.routing.RouteGroup
import dev.alpas.routing.Router

fun Router.addRoutes() = apply {
    group {
        webRoutesGroup()
        authRoutes()
    }.middlewareGroup("web")

    apiRoutes()
}

private fun RouteGroup.webRoutesGroup() {
    get("/", WelcomeController::class).name("welcome")
    // register more web routes here
}

private fun Router.apiRoutes() {
    // register API routes here
}
