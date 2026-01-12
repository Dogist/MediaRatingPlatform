## App-Design

Grundsätzlich wurde die App in 3 Schichten aufgeteilt.

- Die REST-Schicht, welche die verschiedenen REST-APIs und die Logik für das Marshalling der DTOs beinhält.
- Die SERVICE-Schicht, welche von der REST-Schicht verwendet wird, um verschiedene Effekte zu bewirken. Hier ist auch
  ein Großteil der Datenvalidierung angesiedelt.
- Die DAO-Schicht, wo die SQL-Abfragen hinterlegt sind, welche verwendet werden um auf die Datenbank zuzugreifen.

Zusätzlich zu diesen groben Schichten gibt es auch noch:

- die DTO-Klassen, welche zur Einfachheit, wo möglich als Record definiert wurden. Diese sind vor allem für den
  Datentransfer zu und von der REST-API in Verwendung, um eine klare Trennung von interner Logik und den APIs zu
  ermöglichen.
- die Entity-Klassen, welche die Datenbank-Objekte repräsentieren. (Hier würde JPA verwendet werden, wenn es erlaubt
  wäre.)
- und als Letztes die verschiedenen Custom-Exceptions, welche für das spezielle Behandeln von bestimmten Fehler
  verwendet werden.

Diese Trennung wurde gemacht, um eine klare Trennung der Zuständigkeiten zu ermöglichen.

Zwei weitere wichtige Design-Entscheidungen waren die folgenden:

- Quasi-CDI Implementierung via einem statischen Service. Dieser wurde eingebaut, um eine leichtere Erweiterbarkeit und
  Testbarkeit zu ermöglichen.
- REST-API Registrierungen via einer Custom-REST-Annotation. Diese Annotations wurden gebaut, damit leicht neue APIs
  angebunden werden können und sie generisch behandelt werden können.

## Lessons learned

1. Wie man com.sun.net.httpserver.HttpServer verwenden kann.
2. Docker-Compose für PostgreSQL einrichten
3. io.zonky.test.embedded-postgres verwenden für In-Memory DB beim Testen.

## Unit-Testing Strategie und Abdeckung

Hauptsächlich wurden Stellen im Code getestet, welche mehr Buisness-Logik haben,
um zu kontrollieren, dass diese korrekt funktionieren.

Deswegen wurden die DTOs bei welchen Validierungen möglich ist, getestet.
Zusätzlich wird auch noch der Service für die Token-Authentifizierung getestet, da dieser relativ alleinstehend und auch
wichtig ist.

Zusätzlich wurden auch noch die DB-Schnittstellen genauer getestet, da diese leicht übersehen werden können, da sie oft
keine Kompilierungsfehler verursachen.

TODO Test-Abdeckung?

## SOLID Prinzipien + Beispiele

- S = Single responsibility principle
    - Rest, Service und DAO-Klassen wurden je einzeln für die verschiedenen 3 Haupt-Entities erstellt, damit Änderungen
      in einem der Entities, möglichst eingeschränkten Effekt auf den restlichen Code hat.
- O = Open–closed principle
    - Hier ist die REST-Annotation ein gutes Beispiel, wodurch für neue REST-Services, diese Routen nirgendwo extra
      definiert werden müssen und nur in der Klasse bei der Funktion die Annotation hinzugefügt werden muss.
- L = Liskov substitution principle / I = Interface segregation principle
    - Für diese beiden Punkte spricht eigentlich das Validateable-Interface, welches von manchen DTOs implementiert wird
      und dazu führt, dass sie automatisch nach dem Einlesen in der REST-API validiert werden, bevor sie auch nur in die
      Nähe von der Business-Logik kommen. Bei DTOs welche das Interface nicht implementieren, wird einfach nichts
      genauer validiert.
- D = Dependency inversion principle
    - Hierfür wurden Interfaces für alle Services und das Quasi-CDI implementiert, welches es erleichtert die Klassen
      auszutauschen oder zu erweitern, falls notwendig.

## Git

https://github.com/Dogist/MediaRatingPlatform

## Zeiten

### 27.09.2025 14:30-19:00 = 4,5h

- Initiales Setup des Projektes
- Aufbau des REST-Frameworks
- Erste DB-Verbindung

### 28.09.2025 8:15-12:00 = 3,75h

- Authorization Logik mit Bearer-Token implementiert
- User-Service Erweiterung
- verschiedene Controller Fixes
- MediaEntry-Service start

### 08.10.2025 16:15-18:15 = 2h

- MediaEntry Erweiterungen
- Exception und DTO Refactoring

### 14.10.2025 ca. 3h

- Rating Allgemein
- MediaEntry Favorite
- Validation Verbesserungen

### 16.10.2025 1,5h

- AverageScore und verschiedene andere Erweiterungen.

### 24.10.2025 2,5h

- UserStatistics
- Recommendations

### 27.10.2025 2h

- Leaderboard
- PW Hashing
- Full-Search Fix

### 28.10.2025 1h

- Postman-Collection verbessert
- Dinge für Abgabe herrichten

### 13.11.2025 1h

- Refactoring
- Unit-Tests

### 25.11.2025 1,5h

- BearerAuthServiceImpl Synchronisierung verbessert
- AuthService Tests

### 02.12.2025 0,5h

- Refactoring

### 13.12.2025 0,5h

- Refactoring

### 16.12.2025 0,5h

- JUnit für normales Maven 3.X fixen

### 29.12.2025 2,5h

- DB-Tests aufsetzen
- UserDao-Tests implementieren

### 31.12.2025 1,5h

- RatingDao-Tests implementieren
- MediaEntryDao-Tests implementieren
- kleine Fixes bei der MediaEntry-Suche

### 12.01.2026 1h

- Protokoll-Erweiterungen