package com.group19.util;

import com.google.gson.reflect.TypeToken;
import com.group19.TestRunner;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for {@link JsonFileUtil}.
 * <p>
 * Uses temporary files for testing, with automatic cleanup after tests complete.
 *
 * @author Group19
 * @since 1.0
 */
public class JsonFileUtilTest extends TestRunner {

    private static final Type STRING_LIST_TYPE = new TypeToken<List<String>>() {}.getType();
    private static final Type INTEGER_LIST_TYPE = new TypeToken<List<Integer>>() {}.getType();

    private final List<Path> tempFiles = new ArrayList<>();

    @Override
    protected void tearDown() throws Exception {
        // Clean up all created temporary files
        for (Path tempFile : tempFiles) {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException ignored) {
                // Silently ignore cleanup errors
            }
        }
        tempFiles.clear();
    }

    private Path createTempFile() throws IOException {
        Path tempFile = Files.createTempFile("json_test_", ".json");
        tempFiles.add(tempFile);
        return tempFile;
    }

    // ---- readList() ----

    public void testReadListReadsFromExistingJsonFile() throws IOException {
        Path tempFile = createTempFile();

        // Write a known JSON array
        Files.writeString(tempFile, "[\"item1\", \"item2\", \"item3\"]");

        List<String> result = JsonFileUtil.readList(tempFile, STRING_LIST_TYPE);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("item1", result.get(0));
        assertEquals("item2", result.get(1));
        assertEquals("item3", result.get(2));
    }

    public void testReadListReturnsEmptyListForEmptyFile() throws IOException {
        Path tempFile = createTempFile();
        // Write empty content; readList replaces empty content with "[]" before parsing
        Files.writeString(tempFile, "");

        List<String> result = JsonFileUtil.readList(tempFile, STRING_LIST_TYPE);
        assertNotNull("result should not be null", result);
        assertTrue("empty file should return empty list", result.isEmpty());
    }

    public void testReadListReturnsEmptyListForNonExistentFile() throws IOException {
        Path tempFile = createTempFile();
        // Delete the file first to verify that readList auto-creates the file and returns an empty list
        Files.deleteIfExists(tempFile);

        List<String> result = JsonFileUtil.readList(tempFile, STRING_LIST_TYPE);
        assertNotNull("result should not be null", result);
        assertTrue("non-existent file should return empty list", result.isEmpty());
        // Verify the file was auto-created
        assertTrue("file should be auto-created", Files.exists(tempFile));
    }

    public void testReadListHandlesNonExistentDirectory() throws IOException {
        Path tempFile = createTempFile();
        Path nonExistentDir = tempFile.getParent().resolve("nonexistent-" + System.nanoTime());
        Path fileInNonExistentDir = nonExistentDir.resolve("data.json");
        tempFiles.add(fileInNonExistentDir);
        // Also track the parent directory for cleanup
        try {
            List<String> result = JsonFileUtil.readList(fileInNonExistentDir, STRING_LIST_TYPE);
            assertNotNull(result);
            assertTrue(result.isEmpty());
            assertTrue("file in non-existent directory should be auto-created", Files.exists(fileInNonExistentDir));
        } finally {
            try {
                Files.deleteIfExists(fileInNonExistentDir);
                Files.deleteIfExists(nonExistentDir);
            } catch (IOException ignored) {
            }
        }
    }

    // ---- writeList() ----

    public void testWriteListWritesDataThatCanBeReadBack() throws IOException {
        Path tempFile = createTempFile();
        List<String> data = Arrays.asList("alpha", "beta", "gamma");

        JsonFileUtil.writeList(tempFile, data);

        List<String> readBack = JsonFileUtil.readList(tempFile, STRING_LIST_TYPE);
        assertNotNull(readBack);
        assertEquals(3, readBack.size());
        assertEquals("alpha", readBack.get(0));
        assertEquals("beta", readBack.get(1));
        assertEquals("gamma", readBack.get(2));
    }

    public void testWriteListHandlesNullList() throws IOException {
        Path tempFile = createTempFile();

        // Writing a null list should write an empty array
        JsonFileUtil.writeList(tempFile, null);

        List<String> readBack = JsonFileUtil.readList(tempFile, STRING_LIST_TYPE);
        assertNotNull("result should not be null", readBack);
        assertTrue("null list should write an empty array", readBack.isEmpty());
    }

    public void testWriteListHandlesEmptyList() throws IOException {
        Path tempFile = createTempFile();
        List<String> emptyList = new ArrayList<>();

        JsonFileUtil.writeList(tempFile, emptyList);

        List<String> readBack = JsonFileUtil.readList(tempFile, STRING_LIST_TYPE);
        assertNotNull("result should not be null", readBack);
        assertTrue("empty list should remain empty", readBack.isEmpty());
    }

    // ---- round-trip ----

    public void testRoundTripWriteThenReadPreservesData() throws IOException {
        Path tempFile = createTempFile();
        List<String> original = Arrays.asList(
                "Java", "Python", "C++", "JavaScript", "SQL");

        JsonFileUtil.writeList(tempFile, original);

        List<String> restored = JsonFileUtil.readList(tempFile, STRING_LIST_TYPE);
        assertNotNull(restored);
        assertEquals("list size should be consistent", original.size(), restored.size());

        for (int i = 0; i < original.size(); i++) {
            assertEquals("element at index " + i + " should be consistent", original.get(i), restored.get(i));
        }
    }

    public void testReadAfterMultipleWritesReturnsLatestData() throws IOException {
        Path tempFile = createTempFile();

        // First write
        JsonFileUtil.writeList(tempFile, Arrays.asList("v1"));
        List<String> firstRead = JsonFileUtil.readList(tempFile, STRING_LIST_TYPE);
        assertEquals(1, firstRead.size());
        assertEquals("v1", firstRead.get(0));

        // Second write (overwrites)
        JsonFileUtil.writeList(tempFile, Arrays.asList("v2", "v3"));
        List<String> secondRead = JsonFileUtil.readList(tempFile, STRING_LIST_TYPE);
        assertEquals(2, secondRead.size());
        assertEquals("v2", secondRead.get(0));
        assertEquals("v3", secondRead.get(1));
    }

    public void testWriteListHandlesSingleElement() throws IOException {
        Path tempFile = createTempFile();
        List<String> data = Arrays.asList("single");

        JsonFileUtil.writeList(tempFile, data);

        List<String> readBack = JsonFileUtil.readList(tempFile, STRING_LIST_TYPE);
        assertEquals(1, readBack.size());
        assertEquals("single", readBack.get(0));
    }

    // ---- integer type round-trip ----

    public void testWriteAndReadWithIntegers() throws IOException {
        Path tempFile = createTempFile();
        List<Integer> data = Arrays.asList(10, 20, 30);

        JsonFileUtil.writeList(tempFile, data);

        List<Integer> result = JsonFileUtil.readList(tempFile, INTEGER_LIST_TYPE);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(10, result.get(0).intValue());
        assertEquals(20, result.get(1).intValue());
        assertEquals(30, result.get(2).intValue());
    }

    public static void main(String[] args) {
        new JsonFileUtilTest().runTestsAndExit();
    }
}
