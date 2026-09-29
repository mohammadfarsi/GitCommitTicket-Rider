package com.mohammadfarsi.gitcommitmessage.settings

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.components.JBScrollPane
import com.mohammadfarsi.gitcommitmessage.Bundle
import com.mohammadfarsi.gitcommitmessage.util.BuilderInput
import com.mohammadfarsi.gitcommitmessage.util.CommitMessageBuilder
import com.mohammadfarsi.gitcommitmessage.util.OutputMode
import com.mohammadfarsi.gitcommitmessage.util.PatternMode
import java.awt.BorderLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JScrollPane
import javax.swing.JTextField

class CommitTemplateConfigurable : SearchableConfigurable {
    private val settings = CommitTemplateSettings.getInstance()

    private val defaultPatternButton = JRadioButton(Bundle.message("settings.pattern.default"))
    private val pathPatternButton = JRadioButton(Bundle.message("settings.pattern.path"))
    private val customPatternButton = JRadioButton(Bundle.message("settings.pattern.custom"))

    private val prefixSuffixButton = JRadioButton(Bundle.message("settings.output.prefixSuffix"))
    private val replacementButton = JRadioButton(Bundle.message("settings.output.replacement"))

    private val customPatternField = JTextField(24)
    private val prefixField = JTextField(24)
    private val suffixField = JTextField(24)
    private val replacementField = JTextField(24)
    private val previewBranchField = JTextField(24)

    private val previewField = JTextField(24)
    private val errorLabel = JLabel("")
    private val previewButton = JButton(Bundle.message("settings.button.preview"))

    private var initialized = false
    private val contentPanel: JPanel = JPanel()
    private val rootPanel: JPanel = JPanel(BorderLayout())

    override fun getId(): String = "git-commit-message-ticket-number"

    override fun getDisplayName(): String = Bundle.message("settings.title")

    override fun createComponent(): JComponent {
        if (!initialized) {
            buildUi()
            wireListeners()
            reset()
            initialized = true
        }

        val scrollPane = JBScrollPane(contentPanel)
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        rootPanel.removeAll()
        rootPanel.add(scrollPane, BorderLayout.CENTER)
        return rootPanel
    }

    override fun isModified(): Boolean {
        return settings.patternMode != selectedPatternMode() ||
            settings.outputMode != selectedOutputMode() ||
            settings.customPattern != customPatternField.text ||
            settings.prefix != prefixField.text ||
            settings.suffix != suffixField.text ||
            settings.replacementTemplate != replacementField.text ||
            settings.previewBranch != previewBranchField.text
    }

    override fun apply() {
        settings.patternMode = selectedPatternMode()
        settings.outputMode = selectedOutputMode()
        settings.customPattern = customPatternField.text
        settings.prefix = prefixField.text
        settings.suffix = suffixField.text
        settings.replacementTemplate = replacementField.text
        settings.previewBranch = previewBranchField.text
    }

    override fun reset() {
        when (settings.patternMode) {
            PatternMode.DEFAULT -> defaultPatternButton.isSelected = true
            PatternMode.PATH -> pathPatternButton.isSelected = true
            PatternMode.CUSTOM -> customPatternButton.isSelected = true
        }

        when (settings.outputMode) {
            OutputMode.PREFIX_SUFFIX -> prefixSuffixButton.isSelected = true
            OutputMode.REPLACEMENT -> replacementButton.isSelected = true
        }

        customPatternField.text = settings.customPattern
        prefixField.text = settings.prefix
        suffixField.text = settings.suffix
        replacementField.text = settings.replacementTemplate
        previewBranchField.text = settings.previewBranch
        previewField.text = ""
        errorLabel.text = ""
        syncEnabledFields()
    }

    private fun buildUi() {
        val patternGroup = ButtonGroup().apply {
            add(defaultPatternButton)
            add(pathPatternButton)
            add(customPatternButton)
        }

        val outputGroup = ButtonGroup().apply {
            add(prefixSuffixButton)
            add(replacementButton)
        }

        previewField.isEditable = false

        contentPanel.layout = BoxLayout(contentPanel, BoxLayout.Y_AXIS)
        contentPanel.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)

        contentPanel.add(createPatternSection())
        contentPanel.add(createOutputSection())
        contentPanel.add(createPreviewSection())
    }

    private fun createPatternSection(): JPanel {
        val section = createSectionPanel(Bundle.message("settings.pattern.group"))
        var row = 0
        row(section, row++, sectionHeader(Bundle.message("settings.pattern.group")))
        row(section, row++, defaultPatternButton)
        row(section, row++, pathPatternButton)
        row(section, row++, customPatternButton)
        row(section, row++, JLabel(Bundle.message("settings.field.customPattern")), customPatternField)
        return section
    }

    private fun createOutputSection(): JPanel {
        val section = createSectionPanel(Bundle.message("settings.output.group"))
        var row = 0
        row(section, row++, sectionHeader(Bundle.message("settings.output.group")))
        row(section, row++, prefixSuffixButton)
        row(section, row++, replacementButton)
        row(section, row++, JLabel(Bundle.message("settings.field.prefix")), prefixField)
        row(section, row++, JLabel(Bundle.message("settings.field.suffix")), suffixField)
        row(section, row++, JLabel(Bundle.message("settings.field.replacement")), replacementField)
        return section
    }

    private fun createPreviewSection(): JPanel {
        val section = createSectionPanel(Bundle.message("settings.preview.group"))
        var row = 0
        row(section, row++, sectionHeader(Bundle.message("settings.preview.group")))
        row(section, row++, JLabel(Bundle.message("settings.field.previewBranch")), previewBranchField)
        row(section, row++, previewButton)
        row(section, row++, JLabel(Bundle.message("settings.label.preview")), previewField)
        row(section, row++, JLabel(Bundle.message("settings.label.error")), errorLabel)
        return section
    }

    private fun createSectionPanel(title: String): JPanel {
        return JPanel(GridBagLayout()).apply {
            border = BorderFactory.createEmptyBorder(8, 0, 8, 0)
            alignmentX = 0f
        }
    }

    private fun sectionHeader(text: String): JLabel {
        return JLabel(text).apply {
            font = font.deriveFont(Font.BOLD)
        }
    }

    private fun wireListeners() {
        val sync: () -> Unit = { syncEnabledFields() }
        defaultPatternButton.addActionListener { sync() }
        pathPatternButton.addActionListener { sync() }
        customPatternButton.addActionListener { sync() }
        prefixSuffixButton.addActionListener { sync() }
        replacementButton.addActionListener { sync() }

        previewButton.addActionListener {
            errorLabel.text = ""
            try {
                val output = CommitMessageBuilder.build(
                    input = BuilderInput(
                        pattern = selectedPattern(),
                        outputMode = selectedOutputMode(),
                        prefix = prefixField.text,
                        suffix = suffixField.text,
                        replacementTemplate = replacementField.text
                    ),
                    branchName = previewBranchField.text,
                    sampleCommitMessage = "sample commit message"
                )
                previewField.text = output
            } catch (ex: Exception) {
                previewField.text = ""
                errorLabel.text = ex.message ?: "Unknown error"
            }
        }
    }

    private fun syncEnabledFields() {
        customPatternField.isEnabled = customPatternButton.isSelected

        val prefixSuffixEnabled = prefixSuffixButton.isSelected
        prefixField.isEnabled = prefixSuffixEnabled
        suffixField.isEnabled = prefixSuffixEnabled
        replacementField.isEnabled = !prefixSuffixEnabled
    }

    private fun selectedPatternMode(): PatternMode {
        return when {
            defaultPatternButton.isSelected -> PatternMode.DEFAULT
            pathPatternButton.isSelected -> PatternMode.PATH
            else -> PatternMode.CUSTOM
        }
    }

    private fun selectedOutputMode(): OutputMode {
        return if (prefixSuffixButton.isSelected) OutputMode.PREFIX_SUFFIX else OutputMode.REPLACEMENT
    }

    private fun selectedPattern(): String {
        return when (selectedPatternMode()) {
            PatternMode.DEFAULT -> Bundle.message("settings.default.pattern")
            PatternMode.PATH -> Bundle.message("settings.path.pattern")
            PatternMode.CUSTOM -> customPatternField.text
        }
    }

    private fun row(panel: JPanel, row: Int, left: JComponent, right: JComponent) {
        panel.add(
            left,
            GridBagConstraints().apply {
                gridx = 0
                gridy = row
                anchor = GridBagConstraints.WEST
                insets = Insets(4, 8, 4, 8)
            }
        )

        panel.add(
            right,
            GridBagConstraints().apply {
                gridx = 1
                gridy = row
                weightx = 1.0
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(4, 8, 4, 8)
            }
        )
    }

    private fun row(panel: JPanel, row: Int, component: JComponent) {
        panel.add(
            component,
            GridBagConstraints().apply {
                gridx = 0
                gridy = row
                gridwidth = 2
                weightx = 1.0
                fill = GridBagConstraints.HORIZONTAL
                anchor = GridBagConstraints.WEST
                insets = Insets(4, 8, 4, 8)
            }
        )
    }
}
