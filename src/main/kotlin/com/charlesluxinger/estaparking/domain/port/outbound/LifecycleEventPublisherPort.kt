package com.charlesluxinger.estaparking.domain.port.outbound

import com.charlesluxinger.estaparking.domain.event.PublishedLifecycleEvent

interface LifecycleEventPublisherPort {
    fun publish(event: PublishedLifecycleEvent)
}
