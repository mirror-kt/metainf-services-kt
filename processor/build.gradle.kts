plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotest)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.dokka)
}

version = System.getenv("RELEASE_VERSION") ?: "unspecified-SNAPSHOT"

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

val dokkaHtmlJar = tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-docs")
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates("dev.mirror-kt", "metainf-services-kt", project.version.toString())

    pom {
        name = "metainf-services-kt"
        description = "Generates META-INF/services files automatically with Kotlin KSP"
        url = "https://github.com/mirror-kt/metainf-services-kt"
        licenses {
            license {
                name = "MIT"
                url = "https://spdx.org/licenses/MIT.html"
            }
        }
        developers {
            developer {
                id = "mirror-kt"
                name = "Misato Kano"
                email = "me@mirror-kt.dev"
            }
        }
        scm {
            connection = "scm:git:https://github.com/mirror-kt/metainf-services-kt.git"
            developerConnection = "scm:git:https://github.com/mirror-kt/metainf-services-kt.git"
            url = "https://github.com/mirror-kt/metainf-services-kt.git"
        }
    }
}
