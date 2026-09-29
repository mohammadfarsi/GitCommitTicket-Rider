package com.mohammadfarsi.gitcommitmessage.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.util.xmlb.XmlSerializerUtil
import com.mohammadfarsi.gitcommitmessage.Bundle
import com.mohammadfarsi.gitcommitmessage.util.OutputMode
import com.mohammadfarsi.gitcommitmessage.util.PatternMode

@State(name = "git-commit-message-ticket-number", storages = [Storage("git-commit-message-ticket-number.xml")])
@Service(Service.Level.APP)
class CommitTemplateSettings : PersistentStateComponent<CommitTemplateSettings> {
    var patternMode: PatternMode = PatternMode.DEFAULT
    var outputMode: OutputMode = OutputMode.PREFIX_SUFFIX

    var customPattern: String = Bundle.message("settings.default.customPattern")
    var prefix: String = Bundle.message("settings.default.prefix")
    var suffix: String = Bundle.message("settings.default.suffix")
    var replacementTemplate: String = Bundle.message("settings.default.replacement")
    var previewBranch: String = Bundle.message("settings.default.previewBranch")

    override fun getState(): CommitTemplateSettings = this

    override fun loadState(state: CommitTemplateSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun effectivePattern(): String {
        return when (patternMode) {
            PatternMode.DEFAULT -> Bundle.message("settings.default.pattern")
            PatternMode.PATH -> Bundle.message("settings.path.pattern")
            PatternMode.CUSTOM -> customPattern
        }
    }

    companion object {
        fun getInstance(): CommitTemplateSettings = service()
    }
}
