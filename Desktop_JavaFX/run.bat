@echo off
echo Compiling...
javac --module-path "javafx-sdk\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml SecureNotesFX.java SecurityUtil.java
if %errorlevel% neq 0 (
    echo Compilation Failed!
    pause
    exit /b %errorlevel%
)
echo Starting Secure Notes App...
java --module-path "javafx-sdk\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml SecureNotesFX
pause
