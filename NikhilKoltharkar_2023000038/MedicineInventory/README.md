# 💊 Medicine Inventory Management System

A full-stack web application for pharmacy inventory management with CRUD operations.

**Student:** Nikhil Koltharkar (Roll: 2023000038)  
**Project:** Cloud Computing - Semester V  
**Tech Stack:** Java 17, MySQL 8.0, HTML/CSS/JavaScript, Jenkins CI/CD

---

## 🚀 Quick Start (5 Minutes)

### Prerequisites
- Java JDK 8+ ([Download](https://adoptium.net/))
- MySQL 8.0 ([Download](https://dev.mysql.com/downloads/mysql/))

### Step 1: Setup Database
Run this in MySQL Workbench or command line:
```sql
CREATE DATABASE medicine_inventory;
-- Then execute database_setup.sql file
```

### Step 2: Configure Database Password
Edit `src/DatabaseConnection.java` lines 7-8:
```java
private static final String USERNAME = "root";
private static final String PASSWORD = "your_mysql_password";
```

### Step 3: Compile & Run

**Windows:**
```bash
javac -cp ".;lib/*" src/*.java
java -cp ".;lib/*;src" RestAPIServer
```

**Mac/Linux:**
```bash
javac -cp ".:lib/*" src/*.java
java -cp ".:lib/*:src" RestAPIServer
```

### Step 4: Access Application
Open browser: **http://localhost:8081**

---

## ✨ Features
- ✅ Add, View, Update, Delete medicines
- ✅ Search by medicine name
- ✅ Modern responsive UI
- ✅ Real-time notifications

---

## 📁 Project Structure
```
MedicineInventory/
├── lib/                  # JAR dependencies
├── src/                  # Java source code
├── frontend/             # HTML, CSS, JavaScript
└── database_setup.sql    # Database setup
```

---

## 🔧 Jenkins CI/CD (Optional)

This project includes Jenkins integration for automated builds.

**To setup Jenkins:**
See [JENKINS_SETUP_GUIDE.md](JENKINS_SETUP_GUIDE.md) for complete instructions.

**Quick summary:**
1. Download jenkins.war from jenkins.io
2. Run: `java -jar jenkins.war`
3. Access: http://localhost:8080
4. Create build job for this project

**Note:** Jenkins runs separately from the application.
- Jenkins (CI/CD): http://localhost:8080
- Application: http://localhost:8081

---

## 📊 Database Schema

**Table:** medicines

| Field | Type | Description |
|-------|------|-------------|
| medicine_id | INT | Primary key (auto-increment) |
| medicine_name | VARCHAR(100) | Medicine name |
| manufacturer | VARCHAR(100) | Company name |
| quantity | INT | Stock quantity |
| price | DECIMAL(10,2) | Price per unit |
| expiry_date | DATE | Expiration date |
| category | VARCHAR(50) | Medicine category |

---

## 🐛 Common Issues

**"Connection failed"**  
→ Update MySQL password in DatabaseConnection.java

**"JDBC Driver not found"**  
→ Ensure mysql-connector-j-9.5.0.jar is in lib/ folder

**"Port already in use"**  
→ Change port in RestAPIServer.java line 19

---

## 📚 Documentation

- **This file:** Quick start guide
- **JENKINS_SETUP_GUIDE.md:** Complete Jenkins CI/CD setup
- **PROJECT_DOCUMENTATION.txt:** Full project documentation
- **database_setup.sql:** Database creation script

---

## 🛠️ Tech Stack

**Backend:** Java 17, REST API, JDBC, Gson  
**Frontend:** HTML5, CSS3, JavaScript ES6  
**Database:** MySQL 8.0  
**DevOps:** Jenkins 2.528.2, Git

---

## 👨‍💻 Author

Nikhil Koltharkar  
Roll Number: 2023000038  
MSSU Kharghar - Semester V  
Cloud Computing Project

---

## ✅ Testing Checklist

- [ ] Java and MySQL installed
- [ ] Database created with sample data
- [ ] Password updated in DatabaseConnection.java
- [ ] Project compiles without errors
- [ ] Application runs on http://localhost:8081
- [ ] Can add, view, update, delete medicines
- [ ] Search functionality works

---

**For Jenkins setup and advanced configuration, see JENKINS_SETUP_GUIDE.md**