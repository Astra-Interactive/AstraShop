package ru.astrainteractive.astrashop

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface State

inline fun <reified T> State.asState() = this as? T
inline fun <reified T> State.edit(block: T.() -> T): T? = (this as? T)?.let(block)

inline fun <reified T : State> MutableStateFlow<T>.edit(block: T.() -> T) {
    value.edit<T>(block)?.let {
        value = it
    }
}
