package dev.mirrorkt.metainf.services

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import com.tschuchort.compiletesting.useKsp2
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.compilation.CompileConfig
import io.kotest.matchers.compilation.codeSnippet
import io.kotest.matchers.compilation.compile
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class SomeTest : FunSpec({
    val compilerConfig = CompileConfig {
        useKsp2()
        symbolProcessorProviders = mutableListOf(SymbolProcessorProviderImpl())
    }

    test("should compile") {
        val codeSnippet = compilerConfig.codeSnippet(
            """
                package dev.mirrorkt.metainf.services.test
                
                import dev.mirrorkt.metainf.services.MetaInfServices
    
                interface SomeInterface
            
                @MetaInfServices
                class SomeClass : SomeInterface
            """.trimIndent()
        )
        codeSnippet.compile {
            exitCode shouldBe KotlinCompilation.ExitCode.OK
            generatedFiles.forEach { println(it) }
        }
    }
})
