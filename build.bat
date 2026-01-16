@echo off
echo ==========================================
echo   Mouse Keep-Alive Builder (Internal)
echo ==========================================

echo [1] Cleaning up previous build...
del *.class
del *.jar

echo [2] Compiling Java source...
javac -encoding UTF-8 MouseMover.java

echo [3] Packaging JAR file...
if exist META-INF\MANIFEST.MF (
    jar cvfm MouseMover.jar META-INF/MANIFEST.MF MouseMover.class
    echo [SUCCESS] MouseMover.jar created!
) else (
    echo [ERROR] META-INF/MANIFEST.MF not found!
)

pause