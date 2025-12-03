package com.pnc.efg.kafka;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Simple Avro serializer using Avro Reflect to write POJOs to binary.
 * This does NOT integrate with Schema Registry. If you want Schema Registry support, use
 * Confluent's KafkaAvroSerializer or implement schema registration.
 */
public class AvroReflectSerializer<T> implements Serializer<T> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do currently. Optionally read a config for the class name if needed.
    }

    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) return null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            @SuppressWarnings("unchecked")
            DatumWriter<T> writer = new ReflectDatumWriter<>((Class<T>) data.getClass());
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            writer.write(data, encoder);
            encoder.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Avro object", e);
        }
    }

    @Override
    public void close() {
    }
}
