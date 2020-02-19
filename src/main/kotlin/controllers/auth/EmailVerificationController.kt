package dev.alpas.fireplace.controllers.auth

import dev.alpas.Container
import dev.alpas.auth.AuthConfig
import dev.alpas.auth.Authenticatable
import dev.alpas.auth.HandlesEmailVerification
import dev.alpas.auth.notifications.VerifyEmail
import dev.alpas.http.HttpCall
import dev.alpas.mailing.MailMessage
import dev.alpas.make
import dev.alpas.notifications.MailableNotification
import dev.alpas.notifications.NotificationDispatcher
import dev.alpas.notifications.channels.MailChannel
import dev.alpas.notifications.channels.NotificationChannel
import dev.alpas.orAbort
import dev.alpas.routing.Controller
import dev.alpas.routing.UrlGenerator
import dev.alpas.view.ViewRenderer
import kotlin.reflect.KClass

class EmailVerificationController : Controller(), HandlesEmailVerification
{
    override fun  sendVerificationNotice(call: HttpCall, user: Authenticatable) {
        val mail =VerifyEmail(call)
        call.make<NotificationDispatcher>().dispatch(mail, user)
    }
}

class VerifyEmailOverride(private val container: Container) : MailableNotification<Authenticatable> {
    override fun channels(notifiable: Authenticatable): List<KClass<out NotificationChannel>> {
        return listOf(MailChannel::class)
    }

    override fun toMail(notifiable: Authenticatable): MailMessage {
        val expiration = container.make<AuthConfig>().emailVerificationExpiration
        val verificationUrl =
            container.make<UrlGenerator>().signedRoute("verification.verify", mapOf("id" to notifiable.id), expiration)

        return MailMessage().apply {
            to = notifiable.email.orAbort()
            subject = "Verify Email"
            view(
                "auth.emails.verify",
                mapOf(
                    "verificationUrl" to verificationUrl?.toExternalForm(),
                    "user" to notifiable
                )
            )
        }
    }
}

