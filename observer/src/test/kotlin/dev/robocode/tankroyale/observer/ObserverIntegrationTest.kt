package dev.robocode.tankroyale.observer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Integration tests for the observer components.
 */
class ObserverIntegrationTest {
    
    @Test
    fun `test GamePanel can be created and initialized`() {
        val gamePanel = GamePanel()
        assertNotNull(gamePanel)
        
        // Test that we can update with empty game state
        val emptyState = GameState(
            round = 0,
            turn = 0,
            tanks = emptyList(),
            bullets = emptyList(),
            events = emptyList(),
            arenaWidth = 800.0,
            arenaHeight = 600.0
        )
        
        // This should not throw any exceptions
        gamePanel.updateGameState(emptyState)
        gamePanel.clearGameState()
        gamePanel.resetView()
        gamePanel.fitToWindow()
    }
    
    @Test
    fun `test ConnectionDialog can be created`() {
        // Test that we can create connection dialog without GUI
        // Just testing constructor doesn't fail
        assertDoesNotThrow {
            // We can't actually test the dialog without a display,
            // but we can at least verify the class can be loaded
            ConnectionDialog::class.java
        }
    }
    
    @Test
    fun `test GameConnection with invalid parameters`() {
        var callbackInvoked = false
        
        val connection = GameConnection("invalid-host", -1) { gameState ->
            callbackInvoked = true
        }
        
        assertNotNull(connection)
        
        // Test disconnect on non-connected connection
        assertDoesNotThrow {
            connection.disconnect()
        }
        
        assertFalse(callbackInvoked)
    }
    
    @Test
    fun `test complex game state creation and manipulation`() {
        val tanks = listOf(
            TankState(
                id = "tank1",
                name = "Robot1",
                x = 100.0,
                y = 200.0,
                direction = 45.0,
                gunDirection = 90.0,
                radarDirection = 135.0,
                speed = 5.0,
                energy = 100.0,
                gunHeat = 0.5
            ),
            TankState(
                id = "tank2",
                name = "Robot2",
                x = 300.0,
                y = 400.0,
                direction = 180.0,
                gunDirection = 270.0,
                radarDirection = 0.0,
                speed = 3.0,
                energy = 75.0,
                gunHeat = 1.0
            )
        )
        
        val bullets = listOf(
            BulletState(
                id = "bullet1",
                ownerId = "tank1",
                x = 150.0,
                y = 250.0,
                direction = 45.0,
                speed = 20.0,
                power = 2.0
            )
        )
        
        val events = listOf(
            GameEvent(
                type = "tank_hit",
                turn = 50,
                data = mapOf("victim" to "tank2", "attacker" to "tank1", "damage" to 25.0)
            )
        )
        
        val gameState = GameState(
            round = 2,
            turn = 100,
            tanks = tanks,
            bullets = bullets,
            events = events,
            arenaWidth = 1000.0,
            arenaHeight = 800.0
        )
        
        assertEquals(2, gameState.round)
        assertEquals(100, gameState.turn)
        assertEquals(2, gameState.tanks.size)
        assertEquals(1, gameState.bullets.size)
        assertEquals(1, gameState.events.size)
        assertEquals(1000.0, gameState.arenaWidth)
        assertEquals(800.0, gameState.arenaHeight)
        
        // Test tank properties
        val tank1 = gameState.tanks[0]
        assertEquals("tank1", tank1.id)
        assertEquals("Robot1", tank1.name)
        assertEquals(100.0, tank1.x)
        assertEquals(200.0, tank1.y)
        
        // Test bullet properties
        val bullet = gameState.bullets[0]
        assertEquals("bullet1", bullet.id)
        assertEquals("tank1", bullet.ownerId)
        assertEquals(20.0, bullet.speed)
        
        // Test event properties
        val event = gameState.events[0]
        assertEquals("tank_hit", event.type)
        assertEquals(50, event.turn)
        assertEquals("tank1", event.data["attacker"])
    }
}
