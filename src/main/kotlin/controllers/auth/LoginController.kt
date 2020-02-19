package dev.alpas.fireplace.controllers.auth

import dev.alpas.auth.HandlesUserLogin
import dev.alpas.http.HttpCall
import dev.alpas.routing.Controller

class LoginController : Controller(), HandlesUserLogin {
    override fun afterLoginRedirectTo(call: HttpCall): String {
      return "/projects";
    }
}
