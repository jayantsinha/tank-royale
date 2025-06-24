package dev.robocode.tankroyale.observer

import kotlinx.serialization.json.Json
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

/**
 * Handles WebSocket connection to the Tank Royale server for observing games.
 */
class GameConnection(
    private val host: String,
    private val port: Int,
    private val onGameStateUpdate: (GameState) -> Unit
) {
    private var webSocketClient: WebSocketClient? = null
    private val json = Json { ignoreUnknownKeys = true }
    
    fun connect() {
        val uri = URI("ws://$host:$port/observer")
        
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshake: ServerHandshake?) {
                println("Connected to Tank Royale server at $host:$port")
                // Request to start observing
                send("""{"type":"observer_join"}""")
            }
            
            override fun onMessage(message: String?) {
                message?.let { handleMessage(it) }
            }
            
            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                println("Disconnected from server: $reason")
            }
            
            override fun onError(ex: Exception?) {
                println("WebSocket error: ${ex?.message}")
                ex?.printStackTrace()
            }
        }
        
        webSocketClient?.connect()
    }
    
    fun disconnect() {
        webSocketClient?.close()
        webSocketClient = null
    }
    
    private fun handleMessage(message: String) {
        try {
            // Parse the message and extract game state information
            // For now, we'll create a simple game state from the message
            val gameState = parseGameState(message)
            onGameStateUpdate(gameState)
        } catch (e: Exception) {
            println("Error parsing message: ${e.message}")
        }
    }
    
    private fun parseGameState(message: String): GameState {
        // This is a simplified parser - in a real implementation,
        // you would parse the actual Tank Royale message format
        return try {
            val data = json.parseToJsonElement(message)
            GameState(
                round = 1,
                turn = 0,
                tanks = emptyList(),
                bullets = emptyList(),
                events = emptyList(),
                arenaWidth = 800.0,
                arenaHeight = 600.0
            )
        } catch (e: Exception) {
            // Return empty game state if parsing fails
            GameState(
                round = 0,
                turn = 0,
                tanks = emptyList(),
                bullets = emptyList(),
                events = emptyList(),
                arenaWidth = 800.0,
                arenaHeight = 600.0
            )
        }
    }
}

/**
 * Represents the current state of a Tank Royale game.
 */
data class GameState(
    val round: Int,
    val turn: Int,
    val tanks: List<TankState>,
    val bullets: List<BulletState>,
    val events: List<GameEvent>,
    val arenaWidth: Double,
    val arenaHeight: Double
)

/**
 * Represents the state of a tank in the game.
 */
data class TankState(
    val id: String,
    val name: String,
    val x: Double,
    val y: Double,
    val direction: Double,
    val gunDirection: Double,
    val radarDirection: Double,
    val speed: Double,
    val energy: Double,
    val gunHeat: Double,
    val bodyColor: String = "#0000FF",
    val gunColor: String = "#808080",
    val radarColor: String = "#FF0000"
)

/**
 * Represents the state of a bullet in the game.
 */
data class BulletState(
    val id: String,
    val ownerId: String,
    val x: Double,
    val y: Double,
    val direction: Double,
    val speed: Double,
    val power: Double,
    val color: String = "#FFFF00"
)

/**
 * Represents a game event.
 */
data class GameEvent(
    val type: String,
    val turn: Int,
    val data: Map<String, Any> = emptyMap()
)
