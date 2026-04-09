package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.TA;
import com.group19.util.JsonFileUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TADao {
    private final Path taFilePath;
    private final Type listType = new TypeToken<List<TA>>() {
    }.getType();

    public TADao(Path taFilePath) {
        this.taFilePath = taFilePath;
    }

    public List<TA> findAll() throws IOException {
        return JsonFileUtil.readList(taFilePath, listType);
    }

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
