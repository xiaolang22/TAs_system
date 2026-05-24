package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.TA;
import com.group19.util.JsonFileUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for Teaching Assistant (TA) entities, responsible for reading
 * and writing TA profile data stored in JSON files. Supports lookup by student ID
 * and save-or-update semantics (replaces if exists, inserts otherwise).
 *
 * @author Group19
 * @since 1.0
 */
public class TADao {

    /** File path to the TA data JSON file. */
    private final Path taFilePath;

    /** Type token required for Gson deserialisation. */
    private final Type listType = new TypeToken<List<TA>>() {
    }.getType();

    /**
     * Constructs a new TADao instance.
     *
     * @param taFilePath file path to the TA data JSON file
     */
    public TADao(Path taFilePath) {
        this.taFilePath = taFilePath;
    }

    /**
     * Retrieves all TA records.
     *
     * @return a list of TA records
     * @throws IOException if the file cannot be read
     */
    public List<TA> findAll() throws IOException {
        return JsonFileUtil.readList(taFilePath, listType);
    }

    /**
     * Finds a TA by student ID (case-insensitive).
     *
     * @param studentId the student ID
     * @return the matching TA, or {@code null} if not found
     * @throws IOException if the file cannot be read
     */
    public TA findByStudentId(String studentId) throws IOException {
        if (studentId == null || studentId.isBlank()) {
            return null;
        }
        for (TA ta : findAll()) {
            if (studentId.equalsIgnoreCase(ta.getStudentId())) {
                return ta;
            }
        }
        return null;
    }

    /**
     * Saves or updates a TA record (replaces if a record with the same student ID
     * exists; otherwise appends a new record).
     *
     * @param target the TA object to save or update
     * @return the saved TA object
     * @throws IOException if the file cannot be written
     */
    public TA saveOrUpdate(TA target) throws IOException {
        List<TA> all = new ArrayList<>(findAll());
        int existingIndex = -1;

        for (int i = 0; i < all.size(); i++) {
            TA current = all.get(i);
            if (target.getStudentId().equalsIgnoreCase(current.getStudentId())) {
                existingIndex = i;
                break;
            }
        }

        if (existingIndex >= 0) {
            all.set(existingIndex, target);
        } else {
            all.add(target);
        }

        JsonFileUtil.writeList(taFilePath, all);
        return target;
    }
}
