# 🎮 Pixel WAR - Networked Pixel Coloring Game

![Java](https://img.shields.io/badge/Java-JavaFX-orange) ![TCP](https://img.shields.io/badge/Protocol-TCP-blue) ![Architecture](https://img.shields.io/badge/Architecture-Client--Serveur-green)

Real-time multiplayer pixel coloring application, developed in Java with JavaFX and a TCP client-server architecture.

---

## 📌 Description

Pixel WAR is a network game where multiple players connected to the same server compete to color as many pixels as possible on a shared grid. Each colored pixel is locked for **1 minute** before it can be recolored by another player. The player with the most pixels wins!

---

## 🧩 Architecture

```
┌─────────────────────────────────────┐
│            Client (Main.java)        │  ← JavaFX interface + TCP connection
│         Controller.java              │  ← UI logic and message reception
│         Client.java                  │  ← Client socket management
└──────────────────┬──────────────────┘
                   │ TCP (ObjectStream)
┌──────────────────▼──────────────────┐
│            Serveur.java              │  ← Accepts connections
│            ClientSocket.java         │  ← Thread per client
│            Grille.java               │  ← Shared grid state
└─────────────────────────────────────┘
```

---

## 📁 Project Structure

```
PixelWAR/
├── src/application/
│   ├── Main.java                  # JavaFX entry point
│   ├── Controller.java            # UI controller
│   ├── Client.java                # Client-side socket management
│   ├── ClientSocket.java          # Thread per client on server side
│   ├── Serveur.java               # Multi-client TCP server
│   ├── Grille.java                # Shared grid on server side
│   ├── Pixel.java                 # Serializable pixel object
│   ├── Request.java               # Serializable request object
│   ├── RequestType.java           # Request types enum
│   ├── PixelVerrouilleException.java  # Embargoed pixel exception
│   ├── CooldownException.java     # Player cooldown exception
│   ├── inter_mini_prj.fxml        # JavaFX interface
│   └── pixelwar.css               # Dark stylesheet
├── Mini Projet.pdf                # Project statement
└── exe-interface.png              # Interface screenshot
```

---

## ⚙️ Tech Stack

- **Language** : Java
- **Interface** : JavaFX
- **Communication** : TCP (ObjectInputStream / ObjectOutputStream)
- **Serialization** : Java Serializable
- **OS** : macOS / Linux / Windows

---

## 🧠 Features

### 🔌 Connection
- Enter username, server address and port
- Username uniqueness verification
- Fields disabled after connection
- Automatic detection if the server is unreachable

### 🎨 Game
- Shared 18×17 pixel grid in real time
- 28 available colors
- Click on a free pixel → place the color
- **30-second** cooldown between two pixels
- **1-minute** embargo per placed pixel
- Click on a locked pixel → displays remaining time

### 📊 Real-time Information
- Number of free / locked pixels
- My Pixels (personal score)
- Cooldown timer (orange) - before next pixel
- Embargo timer (blue) - remaining time of selected pixel
- List of connected players

### 🔔 Error Handling
- Empty fields → error popup
- Invalid port → error popup
- Username already taken → error popup
- Server unreachable → popup + field reactivation

---

## 🚀 Launch

### Prerequisites
- Java 17+
- JavaFX SDK

### Start the server
```bash
java --enable-native-access=ALL-UNNAMED \
     --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp bin application.Serveur 7777
```

### Start the client
```bash
java --enable-native-access=ALL-UNNAMED \
     --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp bin application.Main
```

---

## 📸 Preview

![Pixel WAR Interface](exe-interface.png)

---

## 👥 Team

Project carried out as a **duo** as part of the Networks IHM module - L3 Computer Science, Université de Bretagne Occidentale, 2025-2026.

---

## 👨‍💻 Author

**Lenaïc Love HOUNLEBA**

CEO & Full Stack Developer - [ComeUp](https://comeup.com/fr/@lenaic-1)

🔗 [github.com/lenaic-hounleba](https://github.com/lenaic-hounleba)

📧 lovehounleba@gmail.com

---

*Project carried out as part of the Networks IHM module - L3 Computer Science, Université de Bretagne Occidentale, 2025-2026.*
