import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

val pluginGroup = providers.gradleProperty("pluginGroup").get()
val pluginVersion = providers.gradleProperty("pluginVersion").get()
val pluginSinceBuild = providers.gradleProperty("pluginSinceBuild").get()
val platformVersion = providers.gradleProperty("platformVersion").get()
val pluginDisplayName = providers.gradleProperty("pluginName").get()

// Optional path to a locally installed Rider. Set it in ~/.gradle/gradle.properties to skip downloading the SDK.
val riderHome = providers.gradleProperty("riderHome").orNull?.takeIf { it.isNotBlank() }

group = pluginGroup
version = pluginVersion

// IDEs from 2024.2 on run on Java 21+.
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget = JvmTarget.JVM_21
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        if (riderHome != null) {
            local(riderHome)
        } else {
            rider(platformVersion)
        }
        bundledPlugin("Git4Idea")
        pluginVerifier()
    }
}

intellijPlatform {
    buildSearchableOptions = false
    instrumentCode = false

    pluginConfiguration {
        name = pluginDisplayName
        version = pluginVersion

        description = """
            <p>Inserts the ticket number from your current Git branch into the commit message, right where your caret is.</p>
            <p>If your branch is named <code>feature/ABC-123-add-login</code>, the plugin extracts <code>ABC-123</code>
            and builds the commit message text from it using your own template.</p>
            <h3>Features</h3>
            <ul>
              <li>Extracts ticket IDs from the current branch with a built-in or custom regex pattern.</li>
              <li>Inserts the generated text at the caret position in the commit message editor.</li>
              <li>Prefix/suffix mode, or regex replacement mode with group backreferences.</li>
              <li>Live preview of the generated message in the settings page.</li>
            </ul>
            <h3>Usage</h3>
            <ol>
              <li>Open the Commit tool window and place the caret in the commit message.</li>
              <li>Press <kbd>Ctrl+Shift+Alt+K</kbd>, or click the ticket icon in the commit message toolbar.</li>
            </ol>
            <p>Configure the pattern and template in <b>Settings | Version Control | Git Commit Message Ticket Number</b>.</p>
        """.trimIndent()

        changeNotes = """
            <ul>
              <li>Initial release: a Kotlin rewrite with a new settings UI and robust branch parsing.</li>
              <li>The toolbar button inserts at the caret, the same as the keyboard shortcut.</li>
              <li>New plugin and toolbar icons.</li>
            </ul>
        """.trimIndent()

        ideaVersion {
            sinceBuild = pluginSinceBuild
            untilBuild = provider { null }
        }
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        channels = listOf(pluginVersion.substringAfter('-', "default").substringBefore('.'))
    }

    pluginVerification {
        ides {
            if (riderHome != null) {
                local(riderHome)
            }
            create(IntelliJPlatformType.Rider, platformVersion)
        }
    }
}
