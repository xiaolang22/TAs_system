package com.group19.service;

import com.group19.dao.TADao;
import com.group19.dto.ServiceResult;
import com.group19.model.TA;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ProfileService {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final DateTimeFormatter TS_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TADao taDao;

    public ProfileService(TADao taDao) {
        this.taDao = taDao;
    }

    public ServiceResult<TA> getProfileByStudentId(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return ServiceResult.failure("Student ID is required to load profile.");
        }

        try {
            TA found = taDao.findByStudentId(studentId.trim());
            if (found == null) {
                return ServiceResult.failure("Profile not found.");
            }
            return ServiceResult.success(found, "Profile loaded.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to load profile data.");
        }
    }

    public ServiceResult<TA> saveProfile(
            String name,
            String studentId,
            String email,
            String programme,
            String skills,
            String experience,
            String availability) {
        List<String> errors = validate(name, studentId, email, programme, skills, availability);
        if (!errors.isEmpty()) {
            return ServiceResult.failure(String.join(" ", errors));
        }

        TA profile = new TA(
                normalize(name),
                normalize(studentId),
                normalize(email),
                normalize(programme),
                normalize(skills),
                normalize(experience),
                normalize(availability));
        profile.setUpdatedAt(LocalDateTime.now().format(TS_FORMATTER));

        try {
            TA saved = taDao.saveOrUpdate(profile);
            return ServiceResult.success(saved, "Profile saved successfully.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to save profile data.");
        }
    }

    private List<String> validate(
            String name,
            String studentId,
            String email,
            String programme,
            String skills,
            String availability) {
        List<String> errors = new ArrayList<>();

        if (isBlank(name)) {
            errors.add("Name is required.");
        }
        if (isBlank(studentId)) {
            errors.add("Student ID is required.");
        }
        if (isBlank(email)) {
            errors.add("Email is required.");
        } else if (!EMAIL_PATTERN.matcher(normalize(email)).matches()) {
            errors.add("Email format is invalid.");
        }
        if (isBlank(programme)) {
            errors.add("Programme is required.");
        }
        if (isBlank(skills)) {
            errors.add("Skills are required.");
        }
        if (isBlank(availability)) {
            errors.add("Availability is required.");
        }
        return errors;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
