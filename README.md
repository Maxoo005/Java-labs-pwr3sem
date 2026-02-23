# 🚀 Java Advanced Labs - 3rd Semester
### Politechnika Wrocławska | Wydział Informatyki i Telekomunikacji

Zbiór zaawansowanych projektów zrealizowanych w języku Java (JDK 17/21), skupiających się na architekturze systemów rozproszonych, wielowątkowości oraz nowoczesnych standardach budowy aplikacji modularnych.

---

## 📂 Przegląd Projektów

### 🔹 Lab 01: Modular Sound System
**Technologie:** `Java 9+`, `JPMS (Jigsaw)`

Inicjalny projekt wykorzystujący system modułów Javy. Aplikacja symuluje kolekcję interaktywnych obiektów generujących dźwięki, kładąc nacisk na silną enkapsulację i architekturę modułową.
* **Kluczowe aspekty:** Deskryptory `module-info.java`, polimorfizm, czysta architektura.

### 🔹 Lab 02: Stack Optimization Engine
**Technologie:** `Java IO/NIO`, `Algorithms`

System rozwiązujący problem optymalizacyjny polegający na zaślepianiu otworów technicznych stosami pierścieni o zmiennych parametrach.
* **Funkcjonalność:** Implementacja algorytmów minimalizacji/maksymalizacji wysokości stosów na podstawie danych z plików płaskich.

### 🔹 Lab 03: Hair Salon Distributed Management (CRUD)
**Technologie:** `Concurrency`, `Persistence Layer (H2/SQLite)`, `Custom Exceptions`

Rozproszony system typu CRUD do zarządzania siecią salonów fryzjerskich. Architektura wspiera jednoczesną pracę czterech ról: **Owner**, **Cashier**, **Employee** oraz **Client**.
* **Concurrency Control:** Implementacja autorskich mechanizmów obsługi wyjątków dla kolizji zapisu i blokad IO przy wielodostępie.
* **Time Simulation:** Autorski silnik czasu ("Time Ticks") umożliwiający testowanie stanów rezerwacji w dynamicznym środowisku.

### 🔹 Lab 04: Maritime Transport Data Visualizer
**Technologie:** `HTTP Client API`, `JSON Parsing`, `JavaFX/Swing`, `Maven`

Modularny klient HTTP konsumujący dane z publicznego API GUS (TranStat). System izoluje warstwę komunikacji od prezentacji danych.
* **Multi-module Maven:** Rozdzielenie artefaktów na `lab04_client` (Business Logic) oraz `lab04_gui` (Presentation).
* **Network Layer:** Wykorzystanie asynchronicznego `java.net.http.HttpClient` oraz mechanizmów *Throttling* (Rate Limiting).



### 🔹 Lab 05: Multi-threaded Agent Simulation
**Technologie:** `Multi-threading`, `ReentrantLock`, `Thread-per-Object`

Wielowątkowy symulator środowiska agentowego operujący na współdzielonej macierzy (Thread-Safe Grid).
* **Synchronizacja:** Wykorzystanie `ReentrantLock` do detekcji kolizji i atomowych operacji na planszy.
* **Agenci:** Implementacja maszyn stanów dla autonomicznych jednostek (Strzelec, Szperacz, Spychacz) działających w osobnych wątkach.



### 🔹 Lab 06: Distributed River System (TCP/IP)
**Technologie:** `TCP/IP Sockets`, `Distributed Systems`, `Network Programming`

Rozproszony symulator sieci hydrologicznej oparty na gniazdach TCP/IP. System modeluje przepływ wody przez kaskadę obiektów hydrotechnicznych.
* **Microservices:** Autonomiczne podsystemy (Zbiornik, Odcinek, Centrala) komunikujące się przez dedykowany protokół komunikacyjny.
* **Service Discovery:** Mechanizm automatycznej rejestracji węzłów w sieci rozproszonej.

### 🔹 Lab 07: RMI River System Migration
**Technologie:** `Java RMI`, `Distributed Objects`, `Service Brokerage`

Migracja systemu z Lab 06 na architekturę obiektów zdalnych (RMI), eliminująca niskopoziomowe zarządzanie gniazdami.
* **Tailor (Krawiec):** Autorski podsystem brokerujący, realizujący dynamiczne "zszywanie" (lookup) namiastek obiektów zdalnych.
* **Abstraction:** Pełna przejrzystość lokalizacji obiektów dzięki wykorzystaniu rejestru RMI.



---

## 🛠️ Wymagania i Uruchomienie

1.  **Środowisko:** Java 17 lub nowsza, Maven 3.8+.
2.  **Klonowanie:**
    ```bash
    git clone [https://github.com/twoj-login/Java-labs-pwr3sem.git](https://github.com/twoj-login/Java-labs-pwr3sem.git)
    ```
3.  **Kompilacja:**
    ```bash
    mvn clean install
    ```
4.  **Uruchomienie:** Instrukcje startowe (parametry VM, ścieżki modułów) znajdują się w komentarzach Javadoc nad klasami `Main` każdego modułu.

---
*Projekt zrealizowany w celach edukacyjnych na Politechnice Wrocławskiej.*
