# Secure Notes App – Desktop (JavaFX)

A secure, offline desktop application for storing sensitive notes.

## Security Architecture (Industry Standard)
This application uses state-of-the-art cryptography to ensure your data is safe:
- **Algorithm**: AES/GCM/NoPadding (Authenticated Encryption).
- **Key Derivation**: PBKDF2WithHmacSHA256 (65,536 iterations).
- **Randomness**: Unique 16-byte Salt and 12-byte IV (Initialization Vector) generated for every save using `SecureRandom`.
- **Integrity**: GCM mode ensures data cannot be tampered with without detection.

## Features
- **Strength Checker**: Real-time password strength analysis.
- **Secure Storage**: Save your notes to any location with custom filenames (e.g., `my_secret.enc`). Each file contains `[Salt] + [IV] + [Ciphertext]`.
- **User Feedback**: Visual alerts for success, errors, and wrong passwords.

## How to Run (Windows)
1. Ensure Java 17+ is installed.
2. Double-click `run.bat` to compile and start the application.

## Requirements
- Java JDK 17 or higher
- JavaFX 21 SDK (Included locally)
