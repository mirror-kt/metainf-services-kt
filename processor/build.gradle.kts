plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotest)
}

dependencies {
    implementation(libs.ksp.symbol.processing.api)
    testImplementation(platform(libs.kotest.bom))
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.bundles.kotlin.compile.testing)
}

tasks.test {
    enabled = false
}

tasks.check {
    dependsOn(tasks.kotest)
}
