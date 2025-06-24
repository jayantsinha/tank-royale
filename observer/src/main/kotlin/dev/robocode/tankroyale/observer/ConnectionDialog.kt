package dev.robocode.tankroyale.observer

import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

/**
 * Dialog for configuring connection to Tank Royale server.
 */
class ConnectionDialog(
    parent: Frame?,
    private val onConnect: (host: String, port: Int) -> Unit
) : JDialog(parent, "Connect to Tank Royale Server", true) {
    
    private val hostField = JTextField("localhost", 20)
    private val portField = JTextField("7654", 10)
    
    init {
        setupUI()
        setupEventHandlers()
        pack()
        setLocationRelativeTo(parent)
    }
    
    private fun setupUI() {
        layout = BorderLayout()
        
        // Create main panel
        val mainPanel = JPanel(GridBagLayout()).apply {
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        }
        
        val gbc = GridBagConstraints()
        
        // Host label and field
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.anchor = GridBagConstraints.WEST
        gbc.insets = Insets(5, 5, 5, 5)
        mainPanel.add(JLabel("Server Host:"), gbc)
        
        gbc.gridx = 1
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weightx = 1.0
        mainPanel.add(hostField, gbc)
        
        // Port label and field
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.fill = GridBagConstraints.NONE
        gbc.weightx = 0.0
        mainPanel.add(JLabel("Server Port:"), gbc)
        
        gbc.gridx = 1
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weightx = 1.0
        mainPanel.add(portField, gbc)
        
        // Info label
        gbc.gridx = 0
        gbc.gridy = 2
        gbc.gridwidth = 2
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.insets = Insets(15, 5, 5, 5)
        val infoLabel = JLabel(
            "<html><small>Enter the hostname/IP and port of the Tank Royale server.<br>" +
            "Default port is usually 7654.</small></html>"
        )
        infoLabel.foreground = Color.GRAY
        mainPanel.add(infoLabel, gbc)
        
        add(mainPanel, BorderLayout.CENTER)
        
        // Create button panel
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
            border = BorderFactory.createEmptyBorder(0, 20, 20, 20)
        }
        
        val connectButton = JButton("Connect").apply {
            preferredSize = Dimension(100, 30)
        }
        
        val cancelButton = JButton("Cancel").apply {
            preferredSize = Dimension(100, 30)
        }
        
        buttonPanel.add(cancelButton)
        buttonPanel.add(connectButton)
        
        add(buttonPanel, BorderLayout.SOUTH)
        
        // Set default button
        rootPane.defaultButton = connectButton
        
        // Store button references for event handlers
        connectButton.addActionListener { handleConnect() }
        cancelButton.addActionListener { handleCancel() }
    }
    
    private fun setupEventHandlers() {
        // Handle Enter key in text fields
        val enterAction = object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                handleConnect()
            }
        }
        
        hostField.actionMap.put("connect", enterAction)
        portField.actionMap.put("connect", enterAction)
        hostField.inputMap.put(KeyStroke.getKeyStroke("ENTER"), "connect")
        portField.inputMap.put(KeyStroke.getKeyStroke("ENTER"), "connect")
        
        // Handle Escape key
        val escapeAction = object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                handleCancel()
            }
        }
        
        rootPane.actionMap.put("cancel", escapeAction)
        rootPane.inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel")
    }
    
    private fun handleConnect() {
        val host = hostField.text.trim()
        val portText = portField.text.trim()
        
        if (host.isEmpty()) {
            showError("Please enter a server hostname or IP address.")
            hostField.requestFocus()
            return
        }
        
        val port = try {
            portText.toInt()
        } catch (e: NumberFormatException) {
            showError("Please enter a valid port number.")
            portField.requestFocus()
            return
        }
        
        if (port < 1 || port > 65535) {
            showError("Port number must be between 1 and 65535.")
            portField.requestFocus()
            return
        }
        
        // Save connection settings for next time
        saveConnectionSettings(host, port)
        
        // Close dialog and connect
        isVisible = false
        dispose()
        onConnect(host, port)
    }
    
    private fun handleCancel() {
        isVisible = false
        dispose()
    }
    
    private fun showError(message: String) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Connection Error",
            JOptionPane.ERROR_MESSAGE
        )
    }
    
    private fun saveConnectionSettings(host: String, port: Int) {
        // In a real application, you might want to save these settings
        // to a preferences file or configuration
        // For now, we'll just update the default values in the fields
    }
}
