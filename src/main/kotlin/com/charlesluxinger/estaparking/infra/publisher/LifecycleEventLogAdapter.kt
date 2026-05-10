package com.charlesluxinger.estaparking.infra.publisher

import com.charlesluxinger.estaparking.domain.event.PublishedLifecycleEvent
import com.charlesluxinger.estaparking.domain.port.outbound.LifecycleEventPublisherPort
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class LifecycleEventLogAdapter : LifecycleEventPublisherPort {
    override fun publish(event: PublishedLifecycleEvent) {
        logger.info {
            "Lifecycle event published: parkingId=${event.parkingId}, plate=${event.licensePlate}, " +
                "type=${event.eventType}, occurredAt=${event.occurredAt}"
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
