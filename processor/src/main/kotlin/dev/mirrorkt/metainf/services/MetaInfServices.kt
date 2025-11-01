package dev.mirrorkt.metainf.services

import kotlin.reflect.KClass

/**
 * Indicates that this class name should be listed into the {@code META-INF/services/CONTRACTNAME}.
 *
 * If the class for which this annotation is placed only have one base class or one interface,
 * then the CONTRACTNAME is the fully qualified name of that type.
 *
 * Otherwise, the [value] element is required to specify the contract type name.
 */
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
annotation class MetaInfServices(
    vararg val value: KClass<*> = [],
)
