package com.charlesluxinger.estaparking.domain.port.inbound.webhook.model

import com.charlesluxinger.estaparking.domain.error.ParkingDomainError

sealed interface WebhookEventOutcome {
    data object Processed : WebhookEventOutcome

    data object IgnoredDuplicate : WebhookEventOutcome

    data object NotFound : WebhookEventOutcome

    data class RejectedTransition(
        val error: ParkingDomainError,
    ) : WebhookEventOutcome
}
