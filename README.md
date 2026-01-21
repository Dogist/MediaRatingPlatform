## Git

https://github.com/Dogist/MediaRatingPlatform

## Getting Started

Zum Verwenden zuerst das docker-compose ausführen, wie in Intellij konfiguriert und
danach das Main, welches ebenfalls konfiguriert ist.

Für eine Übersicht/Testen der APIs gibt es hier auch eine Postman-Collection,
welche auch als Integration-Test fungieren kann.
Hier muss man aber aufpassen, dass der Register-Aufruf nur einmal funktionieren kann
und dieser dann bei künftigen Aufrufen fehlschlagen wird, solange die DB nicht manuell zurückgesetzt wird.