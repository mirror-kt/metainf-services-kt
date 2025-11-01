import org.jreleaser.model.Active
import org.jreleaser.model.Signing

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotest)
    `maven-publish`
    alias(libs.plugins.jreleaser)
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

publishing {
    publications {
        register<MavenPublication>("maven") {
            groupId = "dev.mirror-kt"
            artifactId = "metainf-services-kt"
            version = project.version as String

            from(components["kotlin"])
            artifact(tasks.kotlinSourcesJar) {
                classifier = "sources"
            }
            artifact(dokkaHtmlJar) {
                classifier = "javadoc"
            }

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
    }
    repositories {
        maven {
            setUrl(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

jreleaser {
    signing {
        active = Active.ALWAYS
        armored = true
        mode = Signing.Mode.COMMAND
        secretKey = "B1C939EB0BA877D63C5D3CE19F0869DD92DA49B7"
        publicKey = "B1C939EB0BA877D63C5D3CE19F0869DD92DA49B7"
    }
    gitRootSearch = true
    deploy {
        maven {
            mavenCentral {
                register("release-deploy") {
                    active = Active.RELEASE
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                }
            }
            nexus2 {
                register("snapshot-deploy") {
                    active = Active.SNAPSHOT
                    snapshotUrl = "https://central.sonatype.com/repository/maven-snapshots/"
                    applyMavenCentralRules = true
                    snapshotSupported = true
                    closeRepository = true
                    releaseRepository = true
                    stagingRepository("build/staging-deploy")
                }
            }
        }

    }
}
