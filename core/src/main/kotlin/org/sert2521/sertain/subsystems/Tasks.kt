package org.sert2521.sertain.subsystems

import kotlinx.coroutines.CoroutineScope
import org.sert2521.sertain.coroutines.RobotDispatcher
import kotlin.coroutines.CoroutineContext

@DslMarker
annotation class TaskDsl

@TaskDsl
class TaskConfigure {
    internal val subsystems = mutableListOf<Subsystem>()

    operator fun plusAssign(subsystem: Subsystem) {
        subsystems += subsystem
    }

    internal var action: (suspend CoroutineScope.() -> Unit)? = null

    fun action(action: suspend CoroutineScope.() -> Unit) {
        this.action = action
    }

    fun <S : Subsystem> S.use(): S {
        subsystems += this
        return this
    }
}

suspend fun doTask(name: String = "ANONYMOUS_TASK", configure: TaskConfigure.() -> Unit) {
    with(TaskConfigure().apply(configure)) {
        action?.let {
            @Suppress("unchecked_cast") // Will work, ActionConfigure extends CoroutineScope
            use(*subsystems.toTypedArray(), name = name, action = it)
        }
    }
}

fun <S : Subsystem> TaskConfigure.use(subsystem: S): S {
    this += subsystem
    return subsystem
}
