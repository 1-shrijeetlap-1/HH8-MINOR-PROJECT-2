# Secure Notes App Project

Welcome to the Secure Notes App project! This repository contains a cross-platform solution for securely storing sensitive personal notes using industry-standard cryptography.

##If any problem in github project go to this google drive link:- 
https://drive.google.com/file/d/12SauCQkPSWjLz1U3z0hSkUMVyjCPIHho/view?usp=drivesdk

## Project Structure

This project is divided into two main components:

### 1. Desktop Application (`Desktop_JavaFX`)
A fully functional, offline desktop application build with **JavaFX**.
*   **Status**: Active & Functional.
*   **Key Features**:
    *   **AES-256 GCM Encryption**: Authenticated encryption ensures your notes are confidential and tamper-proof.
    *   **Multi-File Support**: Save different notes to different files (e.g., `work.enc`, `personal.enc`).
    *   **Password Strength Meter**: Real-time feedback on your encryption password.
    *   **Secure Implementation**: Uses `SecureRandom` for Salts/IVs and clears sensitive data from memory where possible.

### 2. Android Application (`Android_SecureNotes`)
A mobile version of the Secure Notes App.
*   **Status**: In Development.
*   **Planned Features**: Android Keystore integration, Biometric authentication.

---
<img width="883" height="733" alt="Screenshot 2026-01-31 181211" src="https://github.com/user-attachments/assets/3c68e217-a402-4866-971a-4d7d5f19f872" />

## Desktop App: Quick Start

**Prerequisites**:
*   Java JDK 17 or higher.

**How to Run (Windows)**:
1.  Navigate to the `Desktop_JavaFX` directory.
2.  Double-click `run.bat` to compile and launch the app.
3.  **To Save**: Type your note, enter a password, and click **Encrypted Save**. Choose a filename.
4.  **To Load**: Enter the same password and click **Decrypt Load**. Select your file.

## Security Architecture

The application is built on a "Privacy First" architecture:
*   **Encryption**: AES/GCM/NoPadding.
*   **Key Derivation**: PBKDF2WithHmacSHA256 (High iteration count).
*   **Data Structure**: Each file contains `[Salt (16 bytes)] + [IV (12 bytes)] + [Ciphertext]`.
*   **No Cloud Sync**: Data never leaves your device.
