import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("org.jetbrains.intellij") version "1.17.4"
}

val pluginGroup: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project
val platformType: String by project
val platformVersion: String by project
val platformDownloadSources: String by project
val riderHome: String? by project
val pluginDisplayName = providers.gradleProperty("pluginName").get()

group = pluginGroup
version = pluginVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

intellij {
    pluginName.set(pluginDisplayName)
    if (!riderHome.isNullOrBlank()) {
        localPath.set(riderHome)
    } else {
        version.set(platformVersion)
        type.set(platformType)
        downloadSources.set(platformDownloadSources.toBoolean())
    }
    updateSinceUntilBuild.set(true)
    plugins.set(listOf("Git4Idea"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    withType<KotlinCompile> {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }

    patchPluginXml {
        version.set(pluginVersion)
        sinceBuild.set(pluginSinceBuild)
        untilBuild.set(pluginUntilBuild)

        pluginDescription.set(
            """
            <p>Fast commit message templates from branch ticket numbers, built and maintained by Mohammad Farsi.</p>
            """.trimIndent()
        )

        changeNotes.set(
            """
            <p>Initial optimized rewrite with modern Kotlin-only settings UI and robust branch parsing.</p>
            """.trimIndent()
        )
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
        channels.set(listOf(pluginVersion.substringAfter('-', "default").substringBefore('.')))
    }

    named("buildSearchableOptions") {
        enabled = false
    }

    named("instrumentCode") {
        enabled = false
    }

    named("buildPlugin") {
        setDependsOn(
            dependsOn.filterNot {
                it.toString().contains("buildSearchableOptions") || it.toString().contains("instrumentCode")
            }
        )
    }
}
