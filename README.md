# 🛡️ Secure P2P Java Chat Application

A robust, multithreaded peer-to-peer messaging platform built entirely from scratch using Core Java. This project demonstrates low-level network programming, modern desktop GUI design, and symmetric encryption without relying on external frameworks.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Sockets](https://img.shields.io/badge/TCP_Sockets-007396?style=for-the-badge&logo=java&logoColor=white)

## ✨ Key Features

* **Multithreaded Backend:** The server handles multiple concurrent users seamlessly using `java.lang.Thread` and `Runnable` client handlers.
* **Custom Modern GUI:** Built with Java Swing, featuring custom-painted rounded chat bubbles, dynamic scrolling, and real-time timestamps.
* **Encrypted Traffic:** All messages sent across the network are encrypted before transmission and decrypted upon receipt using a custom `CryptoUtil`.
* **Universal Connectivity:** Includes a dynamic login interface allowing users to connect via Localhost, Wi-Fi LAN, or across the internet using TCP tunneling (e.g., Ngrok).
* **System Notifications:** Centralized server alerts notify all active clients when a user joins or leaves the chat.

## 🛠️ Tech Stack

* **Language:** Java SE (JDK 17+)
* **Networking:** `java.net.Socket`, `java.net.ServerSocket`
* **Concurrency:** `java.lang.Thread`
* **GUI:** `javax.swing`, `java.awt` (with `Graphics2D` rendering)
* **I/O:** `java.io.BufferedReader`, `java.io.BufferedWriter`

## 🚀 How to Run

### Prerequisites
Ensure you have the Java Development Kit (JDK) installed on your machine.

### Step 1: Start the Server
The server must be running before any clients can connect.
1. Navigate to the `src` directory.
2. Compile and run the `Server.java` file.
   *Alternatively, double-click the `StartServer.bat` script if testing on Windows.*
3. The server will silently open Port `1234` and listen for incoming connections.

### Step 2: Start the Client
1. Run the `SecureChatClient` class (or double-click the exported `.jar` application).
2. A login prompt will appear:
   * **Username:** Enter your display name.
   * **IP Address:** * Type `localhost` if testing on the same machine.
       * Type the host's IPv4 address if testing on a local Wi-Fi network.
       * Type your Ngrok URL if testing over the internet.
   * **Port:** Default is `1234` (Update this if using an Ngrok tunnel).

## 📂 Architecture & Flow

1.  **`Server.java`**: Binds to a port and continuously listens for incoming `Socket` connections.
2.  **`ClientHandler.java`**: Created by the Server for every new user. It maintains a static list of all active handlers and broadcasts incoming messages to everyone.
3.  **`SecureChatClient.java`**: The frontend UI and client-side networking logic. It runs a background listener thread to constantly read the incoming byte stream without freezing the GUI.
4.  **`CryptoUtil.java`**: Intercepts `String` data, encrypts it before it hits the `BufferedWriter`, and decrypts it after it leaves the `BufferedReader`.

## 🎓 Academic Context
This application was developed as a comprehensive Object-Oriented Programming and Java (OOPJ) project at Adani University. It serves as a practical implementation of multithreading, socket networking, and event-driven GUI programming.
