package ru.astrainteractive.astrashop.events

import ru.astrainteractive.astrashop.events.events.MultipleEventsDSL
import ru.astrainteractive.astralibs.events.EventListener
import ru.astrainteractive.astralibs.events.EventManager


/**
 * Handler for all your events
 */
class EventHandler : EventManager {
    override val handlers: MutableList<EventListener> = mutableListOf()

    init {
        MultipleEventsDSL()
    }
}
