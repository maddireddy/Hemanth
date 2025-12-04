# efg-platform-list - Quickstart

Short quickstart extracted from Notes.txt.

Prerequisites
- Java 8 (set JAVA_HOME to Java 1.8)
- Docker (for Kafka) or native Kafka installation
- Gradle wrapper is included (`./gradlew`)

Build
```bash
./gradlew clean build --no-daemon
```

Run (dev)
```bash
./gradlew bootRun --no-daemon
```

Test
```bash
./gradlew test --no-daemon
```

Start Kafka (docker)
```bash
docker compose up -d
```

Publish examples are in `Notes.txt`.
