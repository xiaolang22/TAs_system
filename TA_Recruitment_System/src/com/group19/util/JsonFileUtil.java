package com.group19.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class JsonFileUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JsonFileUtil() {
    }

    public static synchronized <T> List<T> readList(Path filePath, Type listType) throws IOException {
        ensureJsonArrayFile(filePath);
        String content = Files.readString(filePath, StandardCharsets.UTF_8).trim();
        if (content.isEmpty()) {
            content = "[]";
        }

        List<T> data = GSON.fromJson(content, listType);
        return data == null ? new ArrayList<>() : data;
    }

    public static synchronized <T> void writeList(Path filePath, List<T> data) throws IOException {
        ensureJsonArrayFile(filePath);
        String json = GSON.toJson(data == null ? new ArrayList<>() : data);
        Files.writeString(filePath, json, StandardCharsets.UTF_8);
    }

    private static void ensureJsonArrayFile(Path filePath) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        if (!Files.exists(filePath)) {
            Files.writeString(filePath, "[]", StandardCharsets.UTF_8);
        }
    }
}
