package com.pnc.efg.kafka;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.io.DatumReader;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

/**
 * Simple Avro deserializer using Avro Reflect to read POJOs from binary.
 * Must be configured with property `avro.reflect.class` pointing to the fully-qualified class name.
 */
public class AvroReflectDeserializer<T> implements Deserializer<T> {

    private Class<T> targetClass;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Object cls = configs.get("avro.reflect.class");
        if (cls instanceof String) {
            try {
                //noinspection unchecked
                targetClass = (Class<T>) Class.forName((String) cls);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("avro.reflect.class not found: " + cls, e);
            }
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) return null;
        try {
            DatumReader<T> reader = new ReflectDatumReader<>(targetClass);
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize Avro data", e);
        }
    }

    @Override
    public void close() {
    }
}
