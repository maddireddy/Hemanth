package com.pnc.efg.util;

import com.pnc.efg.avro.schema.platformList.EfgPlatformList;
import com.pnc.efg.avro.schema.platformList.ProductRule;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Small CLI to generate the Avro schema .avsc from the POJO, write a sample .avro container file,
 * and optionally register the schema with a Confluent Schema Registry.
 */
public class AvroSchemaTool {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java AvroSchemaTool write-schema|write-sample-avro|register-schema <schemaRegistryUrl> <subject>");
            return;
        }

        String cmd = args[0];
        Schema schema = ReflectData.get().getSchema(EfgPlatformList.class);

        if ("write-schema".equals(cmd)) {
            writeSchemaFile(schema, "src/main/resources/schemas/EfgPlatformList.generated.avsc");
            System.out.println("Wrote schema to src/main/resources/schemas/EfgPlatformList.generated.avsc");
            return;
        }

        if ("write-sample-avro".equals(cmd)) {
            writeSampleAvro(schema, "sample-efg-platform-list.avro");
            System.out.println("Wrote sample Avro container file to sample-efg-platform-list.avro");
            return;
        }

        if ("register-schema".equals(cmd)) {
            if (args.length < 3) {
                System.err.println("register-schema requires schemaRegistryUrl and subject");
                return;
            }
            String registry = args[1];
            String subject = args[2];
            String schemaJson = schema.toString();
            registerSchema(registry, subject, schemaJson);
            return;
        }

        System.out.println("Unknown command: " + cmd);
    }

    private static void writeSchemaFile(Schema schema, String path) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();
        try (FileWriter w = new FileWriter(f)) {
            w.write(schema.toString(true));
        }
    }

    private static void writeSampleAvro(Schema schema, String outPath) throws IOException {
        // Build a sample object
        ProductRule p1 = new ProductRule("P1", asList("r1", "r2"));
        ProductRule p2 = new ProductRule("P2", asList("r4"));
        List<ProductRule> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);

        EfgPlatformList sample = new EfgPlatformList("2025-12-03T10:00:00Z", "SYS", list);

        DatumWriter<EfgPlatformList> writer = new ReflectDatumWriter<>(EfgPlatformList.class);
        try (DataFileWriter<EfgPlatformList> dfw = new DataFileWriter<>(writer)) {
            dfw.create(schema, new File(outPath));
            dfw.append(sample);
        }
    }

    private static List<String> asList(String... vals) {
        List<String> l = new ArrayList<>();
        for (String v : vals) l.add(v);
        return l;
    }

    private static void registerSchema(String registryUrl, String subject, String schemaJson) throws IOException {
        // POST /subjects/{subject}/versions
        String endpoint = registryUrl;
        if (!endpoint.endsWith("/")) endpoint = endpoint + "/";
        endpoint += "subjects/" + subject + "/versions";

        String payload = "{\"schema\":\"" + escapeForJson(schemaJson) + "\"}";

        URL url = new URL(endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/vnd.schemaregistry.v1+json");
        con.setDoOutput(true);
        con.getOutputStream().write(payload.getBytes(StandardCharsets.UTF_8));

        int code = con.getResponseCode();
        if (code >= 200 && code < 300) {
            System.out.println("Schema registered successfully (HTTP " + code + ")");
        } else {
            System.err.println("Failed to register schema, HTTP " + code);
        }
    }

    private static String escapeForJson(String s) {
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n");
    }
}
