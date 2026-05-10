package com.charlesluxinger.estaparking.infra.client.webhook

import com.charlesluxinger.estaparking.domain.port.inbound.webhook.WebhookEventCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventOutcome
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class WebhookControllerV1(
    private val webhookEventCommandPort: WebhookEventCommandPort,
) {
    @PostMapping("/webhook")
    fun webhook(
        @RequestBody request: WebhookEventRequest,
    ): ResponseEntity<Unit> {
        when (webhookEventCommandPort.handle(request.toCommand())) {
            WebhookEventOutcome.Processed,
            WebhookEventOutcome.IgnoredDuplicate,
            WebhookEventOutcome.NotFound,
            is WebhookEventOutcome.RejectedTransition,
            -> Unit
        }

        return ResponseEntity.ok().build()
    }
}
