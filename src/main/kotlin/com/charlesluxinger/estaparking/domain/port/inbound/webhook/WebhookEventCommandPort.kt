package com.charlesluxinger.estaparking.domain.port.inbound.webhook

import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventCommand
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventOutcome

interface WebhookEventCommandPort {
    fun handle(command: WebhookEventCommand): WebhookEventOutcome
}
