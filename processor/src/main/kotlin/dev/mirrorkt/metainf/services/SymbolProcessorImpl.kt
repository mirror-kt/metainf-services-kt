package dev.mirrorkt.metainf.services

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import dev.mirrorkt.metainf.services.util.getKSAnnotationsByType
import java.io.IOException

class SymbolProcessorImpl(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val allSymbols = resolver.getSymbolsWithAnnotation(MetaInfServices::class.qualifiedName!!)
        val ret = allSymbols.filter { !it.validate() }.toList()

        val symbols = allSymbols.mapNotNull {
            if (it is KSClassDeclaration && it.classKind == ClassKind.CLASS && it.validate()) {
                it
            } else {
                logger.warn("@MetaInfServices must be annotated to class", it)
                null
            }
        }

        val services = mutableMapOf<String, MutableSet<String>>()

        for (symbol in symbols.toList()) {
            val serviceInterfaces = symbol.getKSAnnotationsByType(MetaInfServices::class)
                .flatMap { annotation ->
                    @Suppress("UNCHECKED_CAST")
                    (annotation.arguments.find { it.name?.asString() == "value" }?.value as? List<KSType>)
                        ?.map { it.declaration.qualifiedName!!.asString() }
                        ?.takeIf { it.isNotEmpty() }
                        ?.asSequence()
                        ?: symbol.superTypes.map { it.resolve().declaration }
                            .filterIsInstance<KSClassDeclaration>()
                            .filter { it.classKind == ClassKind.INTERFACE }
                            .map { it.qualifiedName!!.asString() }
                }
            serviceInterfaces.forEach { service ->
                services.getOrPut(service) { mutableSetOf() }
                    .add(symbol.qualifiedName!!.asString())
            }
        }

        for ((service, impls) in services) {
            try {
                codeGenerator.createNewFileByPath(
                    dependencies = Dependencies.ALL_FILES,
                    path = "META-INF/services/${service}",
                    extensionName = ""
                ).bufferedWriter().use { writer ->
                    impls.forEach { impl ->
                        writer.appendLine(impl)
                    }
                    writer.flush()
                }
            } catch (ex: IOException) {
                logger.error("Failed to write service definition files")
                logger.exception(ex)
            }
        }

        return ret
    }
}
