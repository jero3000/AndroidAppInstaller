@file:OptIn(KoinExperimentalAPI::class)

package com.jero3000.appinstaller
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify
import kotlin.test.Test

class Simple {
    class ComponentA

    class ComponentB
}

class ModuleCheckTest {
    // given a definition with an injected definition
    private val module = module {
        single { (_: Simple.ComponentA) -> Simple.ComponentB() }
    }

    @Test
    fun checkKoinModule() {

        // Verify and declare Injected Parameters
        module.verify(
            injections = injectedParameters(
                definition<Simple.ComponentB>(Simple.ComponentA::class)
            )
        )
    }
}