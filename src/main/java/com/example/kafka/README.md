# Kafka Avro Producer Example

This package demonstrates how to use Kafka with Avro serialization and Confluent Schema Registry.

## Overview

The example shows how to:
- Define Avro schemas for complex data structures
- Serialize Java objects using Confluent's `KafkaAvroSerializer`
- Publish messages to Kafka with automatic schema registration
- Aggregate data by product and publish as a single consolidated message

## Components

### Avro Schemas

Located in `src/main/resources/schemas/`:
- **ProductRule.avsc**: Defines a product with associated rules
- **ProductRulesEnvelope.avsc**: Wraps multiple ProductRule records

### Java Classes

- **ProductRule.java**: POJO representing a product and its rules
- **ProductRulesEnvelope.java**: Envelope class containing multiple ProductRule objects
- **AvroController.java**: Kafka producer that serializes and publishes Avro messages
- **Main.java**: Example application demonstrating usage

## Prerequisites

1. **Kafka Broker** running on `localhost:9092`
2. **Schema Registry** running on `http://localhost:8081`

You can start these services using the docker-compose.yml in the project root:

```bash
docker-compose up -d
```

## Running the Example

### Using Gradle

```bash
./gradlew runAvroExample
```

### Using IDE

Run the `com.example.kafka.Main` class directly from your IDE.

## Example Input/Output

### Input Data

```java
List<Map<String, String>> inputList = new ArrayList<>();
inputList.add(Map.of("ruleName", "Rule#1", "product", "Zelle-Send-Decline"));
inputList.add(Map.of("ruleName", "Rule#2", "product", "SSW_Domestic-Send-Hold"));
inputList.add(Map.of("ruleName", "Rule#3", "product", "Zelle-Send-Decline"));
```

### Published Avro Record

```json
{
  "products": [
    {
      "product": "Zelle-Send-Decline",
      "rules": ["Rule#1", "Rule#3"]
    },
    {
      "product": "SSW_Domestic-Send-Hold",
      "rules": ["Rule#2"]
    }
  ]
}
```

## Customization

### Kafka Configuration

Modify the `AvroController` constructor call in `Main.java`:

```java
AvroController controller = new AvroController(
    "your-kafka-server:9092",      // Bootstrap servers
    "your-topic-name",              // Topic name
    "http://your-schema-registry:8081"  // Schema Registry URL
);
```

### Schema Changes

1. Update the `.avsc` files in `src/main/resources/schemas/`
2. Update the corresponding Java POJO classes
3. Rebuild the project

## Integration with Existing Code

This example demonstrates using Confluent's `KafkaAvroSerializer` with Schema Registry, which differs from the main project's approach that uses `AvroReflectSerializer`. Both approaches are valid:

- **AvroReflectSerializer** (used in main project): Uses Java reflection, no Schema Registry needed
- **KafkaAvroSerializer** (this example): Requires Schema Registry, provides schema evolution and validation

Choose the approach that best fits your use case.

## Troubleshooting

### Schema Registry Connection Error

Ensure Schema Registry is running:
```bash
curl http://localhost:8081/subjects
```

### Kafka Connection Error

Verify Kafka is running:
```bash
docker-compose ps
```

### ClassNotFoundException

Clean and rebuild the project:
```bash
./gradlew clean build
```

## References

- [Confluent Schema Registry Documentation](https://docs.confluent.io/platform/current/schema-registry/index.html)
- [Apache Avro Documentation](https://avro.apache.org/docs/current/)
- [Kafka Producer API](https://kafka.apache.org/documentation/#producerapi)
