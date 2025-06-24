package dev.robocode.tankroyale.observer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Unit tests for GameState and related data classes.
 */
class GameStateTest {
    
    @Test
    fun `test GameState creation`() {
        val gameState = GameState(
            round = 1,
            turn = 10,
            tanks = listOf(
                TankState(
                    id = "tank1",
                    name = "TestTank",
                    x = 100.0,
                    y = 200.0,
                    direction = 45.0,
                    gunDirection = 90.0,
                    radarDirection = 135.0,
                    speed = 5.0,
                    energy = 100.0,
                    gunHeat = 0.0
                )
            ),
            bullets = listOf(
                BulletState(
                    id = "bullet1",
                    ownerId = "tank1",
                    x = 150.0,
                    y = 250.0,
                    direction = 45.0,
                    speed = 20.0,
                    power = 2.0
                )
            ),
            events = emptyList(),
            arenaWidth = 800.0,
            arenaHeight = 600.0
        )
        
        assertEquals(1, gameState.round)
        assertEquals(10, gameState.turn)
        assertEquals(1, gameState.tanks.size)
        assertEquals(1, gameState.bullets.size)
        assertEquals(800.0, gameState.arenaWidth)
        assertEquals(600.0, gameState.arenaHeight)
    }
    
    @Test
    fun `test TankState properties`() {
        val tank = TankState(
            id = "test-tank",
            name = "My Tank",
            x = 50.0,
            y = 75.0,
            direction = 0.0,
            gunDirection = 30.0,
            radarDirection = 60.0,
            speed = 8.0,
            energy = 85.5,
            gunHeat = 1.2,
            bodyColor = "#FF0000",
            gunColor = "#00FF00",
            radarColor = "#0000FF"
        )
        
        assertEquals("test-tank", tank.id)
        assertEquals("My Tank", tank.name)
        assertEquals(50.0, tank.x)
        assertEquals(75.0, tank.y)
        assertEquals(0.0, tank.direction)
        assertEquals(30.0, tank.gunDirection)
        assertEquals(60.0, tank.radarDirection)
        assertEquals(8.0, tank.speed)
        assertEquals(85.5, tank.energy)
        assertEquals(1.2, tank.gunHeat)
        assertEquals("#FF0000", tank.bodyColor)
        assertEquals("#00FF00", tank.gunColor)
        assertEquals("#0000FF", tank.radarColor)
    }
    
    @Test
    fun `test BulletState properties`() {
        val bullet = BulletState(
            id = "test-bullet",
            ownerId = "owner-tank",
            x = 300.0,
            y = 400.0,
            direction = 180.0,
            speed = 15.0,
            power = 3.0,
            color = "#FFFF00"
        )
        
        assertEquals("test-bullet", bullet.id)
        assertEquals("owner-tank", bullet.ownerId)
        assertEquals(300.0, bullet.x)
        assertEquals(400.0, bullet.y)
        assertEquals(180.0, bullet.direction)
        assertEquals(15.0, bullet.speed)
        assertEquals(3.0, bullet.power)
        assertEquals("#FFFF00", bullet.color)
    }
    
    @Test
    fun `test GameEvent properties`() {
        val event = GameEvent(
            type = "tank_death",
            turn = 50,
            data = mapOf("tankId" to "tank1", "killer" to "tank2")
        )
        
        assertEquals("tank_death", event.type)
        assertEquals(50, event.turn)
        assertEquals(2, event.data.size)
        assertEquals("tank1", event.data["tankId"])
        assertEquals("tank2", event.data["killer"])
    }
}
