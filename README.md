# Git Commit Message Ticket Number

A modern rewrite of the commit template plugin that auto-inserts ticket references from your active Git branch.

Maintainer: **Mohammad Farsi**

## Features

- Extracts ticket IDs from the current branch with configurable regex patterns.
- Inserts generated commit text at caret when commit editor is available.
- Supports prefix/suffix mode and regex replacement/backreference mode.
- Includes live preview in plugin settings.
- Built with Kotlin-only implementation (no Java UI form files).

## Usage

1. Open the Commit tool window (or the Commit Changes dialog).
2. Place the caret where you want the ticket text in the commit message.
3. Press `Ctrl+Shift+Alt+K`, or click the **Insert Commit Message From Branch** icon in the commit message toolbar.
4. The plugin inserts the generated message at the caret using your settings.

## Default Pattern Examples

- `ABC-123`
- `feature/ABC-123-add-login`

## Install

A ready-to-install build is in the [`dist`](dist) folder.

1. Download [`dist/GitCommitMessageTicketNumber-1.0.0.zip`](dist/GitCommitMessageTicketNumber-1.0.0.zip). Keep it zipped.
2. In Rider, open **Settings → Plugins**.
3. Click the ⚙️ icon at the top and choose **Install Plugin from Disk…**.
4. Select the zip file and click **OK**.
5. Restart Rider when prompted.

To update, install the newer zip the same way; Rider replaces the old version.

## Build from Source

Requirements:

- JDK 17 or newer to run Gradle (`java -version` to check)
- Rider 2026.1 or newer installed locally (recommended), or an internet connection to download the Rider SDK

Steps:

1. Clone the repository:

   ```shell
   git clone https://github.com/mohammadfarsi/GitCommitTicket-Rider.git
   cd GitCommitTicket-Rider
   ```

2. Optional: build against your local Rider instead of downloading the Rider SDK (a large download the first time).
   Add these lines to your user-level `~/.gradle/gradle.properties` (on Windows: `%USERPROFILE%.gradlegradle.properties`), not the project file:

   ```properties
   riderHome = C:/Path/To/JetBrains Rider
   org.gradle.java.installations.paths = C:/Path/To/JetBrains Rider/jbr
   ```

   The second line lets Gradle use the Java runtime bundled with Rider for compiling. Add it if the build reports
   `Cannot find a Java installation on your machine matching: {languageVersion=...}`.

3. Build the plugin:

   ```shell
   # Windows
   gradlew.bat buildPlugin

   # macOS / Linux
   ./gradlew buildPlugin
   ```

4. The installable zip is written to `build/distributions/`. Install it with the steps in [Install](#install).

Other useful tasks:

- `gradlew runIde` starts a sandboxed Rider with the plugin installed.
- `gradlew verifyPlugin` runs the JetBrains Plugin Verifier, the same compatibility check JetBrains Marketplace runs on upload.

## Development

- Target IDE: Rider (`RD`) 2026.1 and newer (`since-build` 261)
- Build: Gradle 9.8.0 (via the included wrapper) with the IntelliJ Platform Gradle Plugin 2.x
- Settings page: **Settings | Version Control | Git Commit Message Ticket Number**
