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

- JDK 17 or newer (`java -version` to check)
- Rider 2026.1 installed locally (recommended), or an internet connection to download the Rider SDK

Steps:

1. Clone the repository:

   ```shell
   git clone https://github.com/mohammadfarsi/GitCommitTicket-Rider.git
   cd GitCommitTicket-Rider
   ```

2. Point the build at your Rider installation by editing `riderHome` in `gradle.properties`:

   ```properties
   riderHome = C:/Path/To/JetBrains Rider 2026.1
   ```

   To let Gradle download the Rider SDK instead, leave the value empty (`riderHome =`). This is a large download the first time.

3. Build the plugin:

   ```shell
   # Windows
   gradlew.bat buildPlugin

   # macOS / Linux
   ./gradlew buildPlugin
   ```

4. The installable zip is written to `build/distributions/`. Install it with the steps in [Install](#install).

To try the plugin in a sandboxed Rider instance without installing it, run `gradlew runIde`.

## Development

- Target IDE: Rider (`RD`) 2026.1
- Gradle 8.10.2 (via the included wrapper)
