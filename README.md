# 🗺️ Route Planner – Java Swing & MySQL

The **Route Planner** is a Java Swing-based desktop application that helps users plan trips across **50+ tourist destinations spanning 10 states** in India.
It features a secure **user login system**, an **admin control panel**, and **optimal route calculation algorithms** – all backed by **real-time MySQL database integration**.

## ✨ Features

### 👤 User Module

* 🔐 **Login & Signup** – Secure authentication with MySQL backend
* 🌍 **Route Planning** – Select source & destination from 50+ locations across 10 states
* 🛣️ **Optimal Route Calculation** – Shortest path algorithms for efficient trip planning
* 📊 **User-Friendly GUI** – Intuitive Swing interface for smooth navigation

### 🔑 Admin Module

* 👥 **User Management** – View, update, or remove registered users
* 🗺️ **Tourist Place Management** – Add, update, and delete locations in the database
* 🔄 **Real-Time Sync** – MySQL ensures instant reflection of updates in the application

### ⚙️ Core Highlights

* 📌 Supports **multi-state, multi-destination travel planning**
* 📌 Fully modular Java codebase with **MVC-style structure**
* 📌 **Scalable** – easily extendable for more states/places
* 📌 Database-driven approach for dynamic updates

## 🛠️ Tech Stack

* **Language**: Java (JDK 8+)
* **GUI**: Java Swing
* **Database**: MySQL
* **Connectivity**: JDBC
* **IDE**: Eclipse

## 🏗️ System Design

* **Frontend (Java Swing)** – Graphical interface for user/admin
* **Backend (Java + Algorithms)** – Route calculation & validations
* **Database (MySQL)** – Persistent storage for users & places
* **Admin Controls** – Direct DB sync for live updates

## 📂 Project Structure

```text
src/
│── Guide/
│   ├── BookingFrame.java       # Booking interface for guides
│   ├── Guide.java              # Guide details & model
│
│── Location/
│   ├── LocationFrame.java      # Location selection GUI
│   ├── Place.java              # Tourist place details
│   ├── RouteMapGenerator.java  # Algorithm for route generation
│   ├── RouteMapViewer.java     # GUI to display routes
│
│── ui/
│   ├── AdminDashboard.java     # Admin panel
│   ├── LoginPage.java          # User/Admin login
│   ├── MainFrame.java          # Main navigation screen
│   ├── SignupPage.java         # User signup
│
│── images/                     # GUI assets (icons, maps, etc.)
```
## 📊 Database Schema

The application uses a **MySQL relational schema** to store users, places, routes, and bookings.

**Tables Overview:**

| Table            | Purpose                                                                  |
| ---------------- | ------------------------------------------------------------------------ |
| `users`          | Stores registered user details (id, username, password, email, role)     |
| `UserLog`        | Tracks user login activity and actions                                   |
| `places`         | List of all tourist places with attributes like name, state, description |
| `locations`      | Stores available source-destination mappings for route planning          |
| `guides`         | Guide details (name, contact info, assigned places)                      |
| `Booking`        | Stores guide booking details by users                                    |
| `UserSelections` | Tracks user-selected routes and preferences                              |

## 📸 Screenshots

### Home Screen
<img width="800" height="598" alt="Home Screen" src="https://github.com/user-attachments/assets/818ebbfa-cdab-47de-b107-4c7d9167fc3d" />

### Sign Up
<img width="794" height="582" alt="Sign Up" src="https://github.com/user-attachments/assets/8e28bdc0-5241-4b50-889a-d93e1133066b" />

### Login
<img width="796" height="599" alt="Login" src="https://github.com/user-attachments/assets/8e65bade-6444-4357-8b0f-316a5a04bea1" />

### Location Selection
<img width="795" height="598" alt="Location Selection" src="https://github.com/user-attachments/assets/6b7ffc6b-af3d-4e9b-9420-b8bd90b66096" />

### Places
<img width="799" height="598" alt="Places" src="https://github.com/user-attachments/assets/3af04af5-c102-4c32-889c-f31ec567a5b9" />

### Route Map
<img width="804" height="602" alt="Route Map" src="https://github.com/user-attachments/assets/b30496e9-99bd-4aac-9e13-c0c037387390" />

### Guide Selection
<img width="806" height="603" alt="Guide Selection" src="https://github.com/user-attachments/assets/97b91e10-aade-4d8b-9ca3-fc5b229ee8fc" />

### Book Guide
<img width="804" height="603" alt="Book Guide" src="https://github.com/user-attachments/assets/c88ddd9f-715a-4eb7-a5ac-b6a4cab939ed" />

### Fake Payment Processor
<img width="801" height="602" alt="Payment Step 2" src="https://github.com/user-attachments/assets/6ba2ede6-214f-46ea-8c91-440d01599e49" />
<img width="803" height="603" alt="Payment Step 1" src="https://github.com/user-attachments/assets/fba55ab5-68c1-4feb-a90e-9020d3297084" />
<img width="803" height="604" alt="Payment Step 3" src="https://github.com/user-attachments/assets/338b8f5b-6661-4890-a48a-89d2ec03eb1f" />

### Admin Login
<img width="802" height="600" alt="Admin Login" src="https://github.com/user-attachments/assets/99fa02e9-d400-4e42-9c43-80aaf99d8e71" />

### Admin Dashboard
<img width="802" height="602" alt="Locations & Places" src="https://github.com/user-attachments/assets/7ad8d400-d889-437b-aec9-d74feed64942" />
<img width="799" height="601" alt="User Logs" src="https://github.com/user-attachments/assets/e76c3924-946d-4686-a655-2402bf4ea21c" />
<img width="803" height="600" alt="Bookings" src="https://github.com/user-attachments/assets/211dfd39-3a94-4211-9a06-5728adb34e7d" />
<img width="802" height="599" alt="Guides" src="https://github.com/user-attachments/assets/ebc32abf-8b57-4cda-9534-756917b43b06" />

## ⚙️ Installation & Setup

1. Clone the repository

   ```bash
   git clone https://github.com/SriVarshaCheruku/Route_Planner.git
   cd RoutePlanner
   ```
2. Import the project into **Eclipse IDE**
3. Setup MySQL database

   * Create a schema `route_planner`
   * Import the provided SQL file (if available)
   * Update DB credentials in the code
4. Run the application

   * Start from `MainFrame.java`

## 👥 Team & Duration

* **Team Size**: 3 Members
* **Duration**: 16 Weeks
