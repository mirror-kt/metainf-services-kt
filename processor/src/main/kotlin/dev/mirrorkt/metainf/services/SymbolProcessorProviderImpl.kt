package dev.mirrorkt.metainf.services

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

internal class SymbolProcessorProviderImpl : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return SymbolProcessorImpl(
            logger = environment.logger,
            codeGenerator = environment.codeGenerator,
        )
    }
}
