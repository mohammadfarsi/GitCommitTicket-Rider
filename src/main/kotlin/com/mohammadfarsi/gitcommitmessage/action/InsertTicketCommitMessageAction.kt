package com.mohammadfarsi.gitcommitmessage.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.vcs.CommitMessageI
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.ui.CommitMessage
import com.intellij.openapi.vcs.ui.Refreshable
import com.intellij.openapi.wm.IdeFocusManager
import com.mohammadfarsi.gitcommitmessage.settings.CommitTemplateSettings
import com.mohammadfarsi.gitcommitmessage.util.BuilderInput
import com.mohammadfarsi.gitcommitmessage.util.CommitMessageBuilder
import git4idea.branch.GitBranchUtil

class InsertTicketCommitMessageAction : DumbAwareAction() {
    init {
        isEnabledInModalContext = true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val data = Refreshable.PANEL_KEY.getData(e.dataContext)
        if (data is CommitMessageI) {
            val branchName = GitBranchUtil.getCurrentRepository(project)?.currentBranch?.name?.trim().orEmpty()
            val settings = CommitTemplateSettings.getInstance()
            try {
                val message = CommitMessageBuilder.build(
                    input = BuilderInput(
                        pattern = settings.effectivePattern(),
                        outputMode = settings.outputMode,
                        prefix = settings.prefix.withPlatformNewLine(),
                        suffix = settings.suffix.withPlatformNewLine(),
                        replacementTemplate = settings.replacementTemplate.withPlatformNewLine()
                    ),
                    branchName = branchName
                )
                val editor = findCommitMessageEditor(e, data)

                if (editor != null) {
                    val document = editor.document
                    val caret = editor.caretModel.currentCaret
                    val offset = caret.offset.coerceIn(0, document.textLength)

                    WriteCommandAction.runWriteCommandAction(project) {
                        document.insertString(offset, message)
                        caret.removeSelection()
                        caret.moveToOffset(offset + message.length)
                    }
                    // Toolbar clicks move focus to the button; return it to the message editor.
                    IdeFocusManager.getInstance(project).requestFocus(editor.contentComponent, true)
                } else {
                    // Fallback when no editor is exposed in the action context.
                    val currentMessage = readCurrentCommitMessage(data)
                    data.setCommitMessage(currentMessage + message)
                }
            } catch (ex: Exception) {
                throw ex
            }
        }
    }

    /**
     * The shortcut runs with focus inside the message editor, so [CommonDataKeys.EDITOR] is available.
     * A toolbar click runs with the button's data context, so resolve the editor from the commit message control.
     */
    private fun findCommitMessageEditor(e: AnActionEvent, data: CommitMessageI): Editor? {
        e.getData(CommonDataKeys.EDITOR)?.let { return it }
        val control = e.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL) as? CommitMessage
            ?: data as? CommitMessage
        return control?.editorField?.editor
    }

    private fun readCurrentCommitMessage(data: CommitMessageI): String {
        val methodNames = listOf("getComment", "getCommitMessage")
        for (methodName in methodNames) {
            try {
                val method = data.javaClass.methods.firstOrNull { m ->
                    m.name == methodName && m.parameterCount == 0
                } ?: continue
                val value = method.invoke(data) as? String
                return value ?: ""
            } catch (_: Exception) {
            }
        }
        return ""
    }
}

private fun String.withPlatformNewLine(): String {
    return replace("\\n", System.lineSeparator())
}
