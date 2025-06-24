# Tank Royale Observer

A standalone monitoring application for observing Tank Royale games in real-time.

## Overview

The Tank Royale Observer provides a graphical interface to connect to and monitor Tank Royale game servers. It displays the game arena, tanks, bullets, and game statistics in real-time, making it perfect for spectating battles and analyzing gameplay.

## Features

- **Real-time Game Visualization**: Watch Tank Royale battles as they happen
- **Interactive Arena View**: Zoom, pan, and navigate around the battlefield
- **Tank Information**: View tank positions, directions, energy levels, and names
- **Bullet Tracking**: See bullets in flight with visual indicators
- **Connection Management**: Easy connection to local or remote Tank Royale servers
- **Game Statistics**: Display round, turn, and other game information

## Building and Running

### Prerequisites

- Java 11 or higher
- Gradle (included via wrapper)

### Building

From the root Tank Royale directory:

```bash
./gradlew observer:build
```

### Running

#### Development Mode
```bash
./gradlew observer:run
```

#### Standalone JAR
```bash
# Build the fat JAR
./gradlew observer:fatJar

# Run the standalone application
java -jar observer/build/libs/robocode-tankroyale-observer-*.jar
```

#### Optimized JAR (ProGuard)
```bash
# Build the optimized JAR
./gradlew observer:proguard

# Run the optimized application
java -jar observer/build/libs/robocode-tankroyale-observer-*-min.jar
```

#### Using VS Code Tasks
If you're using VS Code, there's a "Run Observer" task available in the tasks menu that will build and run the observer.

## Usage

1. **Start the Observer**: Launch the application using one of the methods above
2. **Connect to Server**: Use the "Connection" menu to connect to a Tank Royale server
   - Default host: `localhost`
   - Default port: `7654`
3. **Observe Games**: Once connected, the observer will automatically display any active games
4. **Navigate the View**:
   - Mouse wheel: Zoom in/out
   - Click and drag: Pan around the arena
   - View menu: Reset view or fit to window

## Connection Settings

The observer connects to Tank Royale servers via WebSocket. Make sure:

- The Tank Royale server is running
- The server allows observer connections
- Network connectivity is available (for remote servers)
- Firewall settings allow the connection

## Controls

### Mouse Controls
- **Scroll Wheel**: Zoom in/out
- **Left Click + Drag**: Pan the view around the arena
- **Double Click**: Reset view to default

### Menu Options
- **Connection → Connect to Server**: Open connection dialog
- **Connection → Disconnect**: Disconnect from current server
- **View → Reset View**: Reset zoom and position to default
- **View → Fit to Window**: Scale the arena to fit the window
- **Help → About**: Show application information

## Technical Details

### Architecture
- **Kotlin/JVM**: Main application framework
- **Java Swing**: User interface toolkit
- **WebSocket**: Real-time communication with Tank Royale servers
- **Coroutines**: Asynchronous networking and UI updates

### Project Structure
```
observer/
├── build.gradle.kts           # Build configuration
├── proguard-rules.pro         # Code obfuscation rules
├── README.md                  # This file
└── src/main/kotlin/dev/robocode/tankroyale/observer/
    ├── ObserverApp.kt         # Main application and UI
    ├── GameConnection.kt      # WebSocket client and game state
    ├── GamePanel.kt           # Game visualization panel
    └── ConnectionDialog.kt    # Server connection dialog
```

### Dependencies
- Kotlin standard library and coroutines
- Java WebSocket client library
- Kotlinx serialization for JSON parsing
- Java Swing/AWT for GUI (built-in)

## Troubleshooting

### Common Issues

**Cannot connect to server**
- Verify the server is running
- Check the host and port settings
- Ensure no firewall is blocking the connection

**Poor performance**
- Try the optimized ProGuard build
- Reduce window size or zoom level
- Close other applications to free memory

**Display issues**
- Try different look and feel settings
- Update Java to the latest version
- Check graphics driver compatibility

### Logging

The observer outputs connection and error information to the console. Run from terminal to see debug output:

```bash
java -jar observer/build/libs/robocode-tankroyale-observer-*.jar
```

## Contributing

This observer is part of the Tank Royale project. See the main project's CONTRIBUTING.md for guidelines on contributing to the codebase.

## License

Licensed under the Apache License, Version 2.0. See the main project LICENSE file for details.
