package com.lonwulf.labs.easyshopmanager.scanner

import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean

class ScopedExecutor(private val executor: Executor) : Executor {
    private var shutdown: AtomicBoolean = AtomicBoolean()

    override fun execute(command: Runnable?) {
       if (shutdown.get()) return
        executor.execute{
            if (shutdown.get()) return@execute
            command?.run()
        }
    }

    fun shutdown() {
        shutdown.set(true)
    }
}