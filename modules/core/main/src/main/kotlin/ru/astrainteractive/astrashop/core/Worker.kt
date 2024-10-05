package ru.astrainteractive.astrashop.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import java.util.Timer
import kotlin.time.Duration

abstract class Worker(val key: String) : Logger by JUtiltLogger("AstraShop-$key") {
    private var scheduler: Timer? = null

    abstract val scope: CoroutineScope

    abstract val initialDelay: Duration
    abstract val period: Duration

    abstract suspend fun doWork()

    fun start() {
        if (scheduler != null) error("Scheduler already exists! $key $scheduler")
        scheduler = kotlin.concurrent.timer(
            name = key,
            daemon = false,
            initialDelay = initialDelay.inWholeMilliseconds,
            period = period.inWholeMilliseconds,
            action = { scope.launch { doWork() } }
        )
    }

    fun stop() {
        scheduler?.cancel()
        scheduler = null
    }
}
