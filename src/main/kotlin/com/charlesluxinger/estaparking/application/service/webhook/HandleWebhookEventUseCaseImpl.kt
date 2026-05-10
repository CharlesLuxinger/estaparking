package com.charlesluxinger.estaparking.application.service.webhook

import com.charlesluxinger.estaparking.domain.error.DomainResult
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.event.StoredParkingEvent
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.WebhookEventCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventCommand
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventOutcome
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingEventRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingSessionRepositoryPort
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle

class HandleWebhookEventUseCaseImpl(
    private val parkingSessionRepositoryPort: ParkingSessionRepositoryPort,
    private val parkingEventRepositoryPort: ParkingEventRepositoryPort,
) : WebhookEventCommandPort {
    override fun handle(command: WebhookEventCommand): WebhookEventOutcome {
        val currentParking =
            parkingSessionRepositoryPort.findById(command.parkingId)
                ?: return WebhookEventOutcome.NotFound

        val outcome =
            if (isDuplicateForActiveSession(command)) {
                WebhookEventOutcome.IgnoredDuplicate
            } else {
                val vehicle = Vehicle(command.licensePlate)
                when (
                    val transitionResult =
                        currentParking.apply(
                            eventType = command.eventType,
                            vehicle = vehicle,
                        )
                ) {
                    is DomainResult.Success -> {
                        parkingSessionRepositoryPort.save(transitionResult.value)
                        parkingEventRepositoryPort.save(
                            StoredParkingEvent(
                                parkingId = command.parkingId,
                                licensePlate = command.licensePlate,
                                eventType = command.eventType,
                            ),
                        )
                        WebhookEventOutcome.Processed
                    }

                    is DomainResult.Error -> WebhookEventOutcome.RejectedTransition(transitionResult.error)
                }
            }

        return outcome
    }

    private fun isDuplicateForActiveSession(command: WebhookEventCommand): Boolean {
        val eventsByVehicle =
            parkingEventRepositoryPort
                .findByParkingId(command.parkingId)
                .filter { it.licensePlate == command.licensePlate }

        if (eventsByVehicle.isEmpty()) {
            return false
        }

        val lastExitIndex = eventsByVehicle.indexOfLast { it.eventType == EventType.EXIT }
        val eventsInActiveSession =
            if (lastExitIndex < 0) {
                eventsByVehicle
            } else {
                eventsByVehicle.drop(lastExitIndex + 1)
            }

        return eventsInActiveSession.any { it.eventType == command.eventType }
    }
}
