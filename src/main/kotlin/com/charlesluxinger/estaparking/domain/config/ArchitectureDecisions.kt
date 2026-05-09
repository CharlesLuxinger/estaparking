package com.charlesluxinger.estaparking.domain.config

import java.math.RoundingMode
import java.time.ZoneOffset

/**
 * Architecture Decisions - Tech Challenge
 *
 * These decisions are locked and must not be changed without review.
 */
object ArchitectureDecisions {
    /** Idempotency: Reject out-of-order transitions with 409 Conflict */
    enum class IdempotencyStrategy {
        STRICT_REJECT,
    }

    /** Money rounding: HALF_UP, 2 decimal places */
    val MONEY_ROUNDING_MODE: RoundingMode = RoundingMode.HALF_UP
    const val MONEY_DECIMAL_PLACES = 2

    /** Timezone: UTC for all billing/revenue date partitioning */
    val TIMEZONE: ZoneOffset = ZoneOffset.UTC

    /** Startup resilience: Degraded mode when simulator unavailable */
    enum class StartupResilience {
        DEGRADED,
    }
}
