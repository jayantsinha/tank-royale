package dev.robocode.tankroyale.observer

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import javax.swing.JPanel
import kotlin.math.*

/**
 * Panel that renders the Tank Royale game visualization.
 */
class GamePanel : JPanel() {
    private var gameState: GameState? = null
    private var scale = 1.0
    private var offsetX = 0.0
    private var offsetY = 0.0
    private var lastMouseX = 0
    private var lastMouseY = 0
    private var isDragging = false
    
    init {
        background = Color.BLACK
        preferredSize = Dimension(800, 600)
        
        setupMouseHandlers()
    }
    
    private fun setupMouseHandlers() {
        val mouseHandler = object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                lastMouseX = e.x
                lastMouseY = e.y
                isDragging = true
            }
            
            override fun mouseReleased(e: MouseEvent) {
                isDragging = false
            }
            
            override fun mouseDragged(e: MouseEvent) {
                if (isDragging) {
                    val dx = e.x - lastMouseX
                    val dy = e.y - lastMouseY
                    offsetX += dx / scale
                    offsetY += dy / scale
                    lastMouseX = e.x
                    lastMouseY = e.y
                    repaint()
                }
            }
            
            override fun mouseWheelMoved(e: MouseWheelEvent) {
                val oldScale = scale
                val scaleFactor = if (e.wheelRotation < 0) 1.1 else 0.9
                scale = (scale * scaleFactor).coerceIn(0.1, 10.0)
                
                // Adjust offset to zoom towards mouse position
                val mouseX = e.x.toDouble()
                val mouseY = e.y.toDouble()
                offsetX += (mouseX / oldScale - mouseX / scale)
                offsetY += (mouseY / oldScale - mouseY / scale)
                
                repaint()
            }
        }
        
        addMouseListener(mouseHandler)
        addMouseMotionListener(mouseHandler)
        addMouseWheelListener(mouseHandler)
    }
    
    fun updateGameState(newGameState: GameState) {
        gameState = newGameState
        repaint()
    }
    
    fun clearGameState() {
        gameState = null
        repaint()
    }
    
    fun resetView() {
        scale = 1.0
        offsetX = 0.0
        offsetY = 0.0
        repaint()
    }
    
    fun fitToWindow() {
        val state = gameState ?: return
        val padding = 50.0
        
        val scaleX = (width - 2 * padding) / state.arenaWidth
        val scaleY = (height - 2 * padding) / state.arenaHeight
        scale = minOf(scaleX, scaleY).coerceAtLeast(0.1)
        
        offsetX = (width / scale - state.arenaWidth) / 2
        offsetY = (height / scale - state.arenaHeight) / 2
        
        repaint()
    }
    
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        
        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        
        val state = gameState
        if (state == null) {
            drawNoGameMessage(g2d)
            return
        }
        
        // Save original transform
        val originalTransform = g2d.transform
        
        // Apply view transformation
        g2d.scale(scale, scale)
        g2d.translate(offsetX, offsetY)
        
        // Draw arena
        drawArena(g2d, state)
        
        // Draw game elements
        drawTanks(g2d, state.tanks)
        drawBullets(g2d, state.bullets)
        
        // Restore original transform
        g2d.transform = originalTransform
        
        // Draw UI overlay
        drawOverlay(g2d, state)
    }
    
    private fun drawNoGameMessage(g2d: Graphics2D) {
        g2d.color = Color.WHITE
        g2d.font = Font("SansSerif", Font.BOLD, 24)
        val message = "No game connected"
        val fm = g2d.fontMetrics
        val x = (width - fm.stringWidth(message)) / 2
        val y = height / 2
        g2d.drawString(message, x, y)
        
        g2d.font = Font("SansSerif", Font.PLAIN, 16)
        val instruction = "Use Connection menu to connect to a Tank Royale server"
        val fm2 = g2d.fontMetrics
        val x2 = (width - fm2.stringWidth(instruction)) / 2
        val y2 = y + 40
        g2d.drawString(instruction, x2, y2)
    }
    
    private fun drawArena(g2d: Graphics2D, state: GameState) {
        // Draw arena background
        g2d.color = Color.DARK_GRAY
        g2d.fillRect(0, 0, state.arenaWidth.toInt(), state.arenaHeight.toInt())
        
        // Draw arena border
        g2d.color = Color.WHITE
        g2d.stroke = BasicStroke(2.0f)
        g2d.drawRect(0, 0, state.arenaWidth.toInt(), state.arenaHeight.toInt())
        
        // Draw grid
        g2d.color = Color(64, 64, 64)
        g2d.stroke = BasicStroke(1.0f)
        val gridSize = 50
        
        // Vertical lines
        var x = gridSize
        while (x < state.arenaWidth) {
            g2d.drawLine(x, 0, x, state.arenaHeight.toInt())
            x += gridSize
        }
        
        // Horizontal lines
        var y = gridSize
        while (y < state.arenaHeight) {
            g2d.drawLine(0, y, state.arenaWidth.toInt(), y)
            y += gridSize
        }
    }
    
    private fun drawTanks(g2d: Graphics2D, tanks: List<TankState>) {
        for (tank in tanks) {
            drawTank(g2d, tank)
        }
    }
    
    private fun drawTank(g2d: Graphics2D, tank: TankState) {
        val size = 30.0
        val x = tank.x - size / 2
        val y = tank.y - size / 2
        
        // Save transform
        val originalTransform = g2d.transform
        
        // Draw tank body
        g2d.color = Color.decode(tank.bodyColor)
        g2d.fillOval(x.toInt(), y.toInt(), size.toInt(), size.toInt())
        g2d.color = Color.WHITE
        g2d.stroke = BasicStroke(2.0f)
        g2d.drawOval(x.toInt(), y.toInt(), size.toInt(), size.toInt())
        
        // Draw gun
        g2d.translate(tank.x, tank.y)
        g2d.rotate(Math.toRadians(tank.gunDirection))
        g2d.color = Color.decode(tank.gunColor)
        g2d.stroke = BasicStroke(4.0f)
        g2d.drawLine(0, 0, (size * 0.8).toInt(), 0)
        
        // Reset transform and draw radar
        g2d.transform = originalTransform
        g2d.translate(tank.x, tank.y)
        g2d.rotate(Math.toRadians(tank.radarDirection))
        g2d.color = Color.decode(tank.radarColor)
        g2d.stroke = BasicStroke(2.0f)
        g2d.drawLine(0, 0, (size * 0.6).toInt(), 0)
        
        // Reset transform
        g2d.transform = originalTransform
        
        // Draw tank name and stats
        g2d.color = Color.WHITE
        g2d.font = Font("SansSerif", Font.PLAIN, 10)
        g2d.drawString(tank.name, (tank.x - 20).toInt(), (tank.y + size).toInt())
        g2d.drawString("E:${tank.energy.toInt()}", (tank.x - 15).toInt(), (tank.y + size + 12).toInt())
    }
    
    private fun drawBullets(g2d: Graphics2D, bullets: List<BulletState>) {
        for (bullet in bullets) {
            drawBullet(g2d, bullet)
        }
    }
    
    private fun drawBullet(g2d: Graphics2D, bullet: BulletState) {
        val size = (bullet.power * 2 + 2).toInt()
        g2d.color = Color.decode(bullet.color)
        g2d.fillOval(
            (bullet.x - size / 2).toInt(),
            (bullet.y - size / 2).toInt(),
            size,
            size
        )
    }
    
    private fun drawOverlay(g2d: Graphics2D, state: GameState) {
        // Draw round and turn info
        g2d.color = Color.WHITE
        g2d.font = Font("SansSerif", Font.BOLD, 16)
        g2d.drawString("Round: ${state.round}", 10, 25)
        g2d.drawString("Turn: ${state.turn}", 10, 45)
        
        // Draw scale info
        g2d.font = Font("SansSerif", Font.PLAIN, 12)
        g2d.drawString("Scale: ${String.format("%.1f", scale * 100)}%", 10, height - 20)
        
        // Draw zoom instructions
        g2d.drawString("Mouse wheel: zoom, Drag: pan", width - 200, height - 20)
    }
}
