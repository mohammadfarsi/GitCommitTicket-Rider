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

1. Open the Commit Changes dialog.
2. Press `Ctrl+K` in the commit message area.
3. The plugin inserts the generated message using your settings.

## Default Pattern Examples

- `ABC-123`
- `feature/ABC-123-add-login`

## Development

- Target IDE: Rider (`RD`) 2026.1
- Build: `./gradlew buildPlugin`
- Run sandbox: `./gradlew runIde`
