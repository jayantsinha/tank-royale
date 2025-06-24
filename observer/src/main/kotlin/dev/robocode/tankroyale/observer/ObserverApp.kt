package dev.robocode.tankroyale.observer

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

/**
 * Main entry point for the Tank Royale Observer application.
 */
fun main(args: Array<String>) {
    // Set system properties for better UI appearance
    System.setProperty("apple.laf.useScreenMenuBar", "true")
    System.setProperty("apple.awt.application.name", "Tank Royale Observer")
    
    SwingUtilities.invokeLater {
        // Use default look and feel for now
        
        val observer = ObserverApp()
        observer.start()
    }
}

/**
 * Main Observer application class that provides a window to monitor Tank Royale games.
 */
class ObserverApp {
    private var mainFrame: JFrame? = null
    private var gameConnection: GameConnection? = null
    private var gamePanel: GamePanel? = null
    
    fun start() {
        createUI()
    }
    
    private fun createUI() {
        mainFrame = JFrame("Tank Royale Observer").apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            size = Dimension(1200, 800)
            setLocationRelativeTo(null)
            
            // Create menu bar
            jMenuBar = createMenuBar()
            
            // Create main panel
            contentPane = createMainPanel()
            
            // Handle window closing
            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
                    shutdown()
                }
            })
            
            isVisible = true
        }
    }
    
    private fun createMenuBar(): JMenuBar {
        return JMenuBar().apply {
            add(JMenu("Connection").apply {
                add(JMenuItem("Connect to Server...").apply {
                    addActionListener { showConnectionDialog() }
                })
                add(JMenuItem("Disconnect").apply {
                    addActionListener { disconnect() }
                })
                addSeparator()
                add(JMenuItem("Exit").apply {
                    addActionListener { shutdown() }
                })
            })
            
            add(JMenu("View").apply {
                add(JMenuItem("Reset View").apply {
                    addActionListener { gamePanel?.resetView() }
                })
                add(JMenuItem("Fit to Window").apply {
                    addActionListener { gamePanel?.fitToWindow() }
                })
            })
            
            add(JMenu("Help").apply {
                add(JMenuItem("About").apply {
                    addActionListener { showAboutDialog() }
                })
            })
        }
    }
    
    private fun createMainPanel(): JPanel {
        return JPanel(BorderLayout()).apply {
            // Create game visualization panel
            gamePanel = GamePanel().also { panel ->
                add(panel, BorderLayout.CENTER)
            }
            
            // Create status panel
            add(createStatusPanel(), BorderLayout.SOUTH)
            
            // Create connection info panel
            add(createConnectionPanel(), BorderLayout.NORTH)
        }
    }
    
    private fun createStatusPanel(): JPanel {
        return JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            border = BorderFactory.createBevelBorder(1)
            add(JLabel("Status: Not connected"))
        }
    }
    
    private fun createConnectionPanel(): JPanel {
        return JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            border = BorderFactory.createTitledBorder("Connection")
            add(JLabel("Server: Not connected"))
            add(JButton("Connect").apply {
                addActionListener { showConnectionDialog() }
            })
        }
    }
    
    private fun showConnectionDialog() {
        val dialog = ConnectionDialog(mainFrame) { host, port ->
            connect(host, port)
        }
        dialog.isVisible = true
    }
    
    private fun connect(host: String, port: Int) {
        Thread {
            try {
                disconnect() // Disconnect existing connection if any
                
                gameConnection = GameConnection(host, port) { gameState ->
                    SwingUtilities.invokeLater {
                        gamePanel?.updateGameState(gameState)
                    }
                }
                
                gameConnection?.connect()
                SwingUtilities.invokeLater {
                    updateConnectionStatus("Connected to $host:$port")
                }
            } catch (e: Exception) {
                SwingUtilities.invokeLater {
                    JOptionPane.showMessageDialog(
                        mainFrame,
                        "Failed to connect to server: ${e.message}",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        }.start()
    }
    
    private fun disconnect() {
        gameConnection?.disconnect()
        gameConnection = null
        updateConnectionStatus("Not connected")
        gamePanel?.clearGameState()
    }
    
    private fun updateConnectionStatus(status: String) {
        // Update UI status - you can implement this to update status labels
        mainFrame?.title = "Tank Royale Observer - $status"
    }
    
    private fun showAboutDialog() {
        JOptionPane.showMessageDialog(
            mainFrame,
            """
            Tank Royale Observer
            
            A monitoring tool for observing Tank Royale games in real-time.
            
            Version: 1.0.0
            Copyright © 2025 Robocode
            """.trimIndent(),
            "About Tank Royale Observer",
            JOptionPane.INFORMATION_MESSAGE
        )
    }
    
    private fun shutdown() {
        disconnect()
        System.exit(0)
    }
}
