package com.jmvsta.scenarios

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class Scenario {
    protected fun runWithLifecycle(testInstance: Any, testMethod: () -> Unit) {
        testInstance::class.members.find { it.annotations.any { a -> a is BeforeEach } }?.call(testInstance)
        try {
            testMethod()
        } finally {
            testInstance::class.members.find { it.annotations.any { a -> a is AfterEach } }?.call(testInstance)
        }
    }

}