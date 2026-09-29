package com.mohammadfarsi.gitcommitmessage.util

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

enum class OutputMode {
    PREFIX_SUFFIX,
    REPLACEMENT
}

enum class PatternMode {
    DEFAULT,
    PATH,
    CUSTOM
}

data class BuilderInput(
    val pattern: String,
    val outputMode: OutputMode,
    val prefix: String,
    val suffix: String,
    val replacementTemplate: String
)

object CommitMessageBuilder {
    fun build(input: BuilderInput, branchName: String, sampleCommitMessage: String = ""): String {
        val regex = try {
            Pattern.compile(input.pattern)
        } catch (ex: PatternSyntaxException) {
            throw IllegalArgumentException("Invalid regex pattern: ${ex.description}")
        }

        return when (input.outputMode) {
            OutputMode.PREFIX_SUFFIX -> {
                val matcher = regex.matcher(branchName)
                val match = if (matcher.find()) matcher.group() else ""
                "${input.prefix}$match${input.suffix}$sampleCommitMessage"
            }
            OutputMode.REPLACEMENT -> {
                val matcher = regex.matcher(branchName)
                if (!matcher.find()) {
                    sampleCommitMessage
                } else {
                    matcher.replaceAll(input.replacementTemplate) + sampleCommitMessage
                }
            }
        }
    }
}
