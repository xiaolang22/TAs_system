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

/**
 * Profile service responsible for querying and saving TA personal profiles.
 * Includes validation logic for form fields (name, student ID, email, programme,
 * skills, experience, availability).
 *
 * @author Group19
 * @since 1.0
 */
public class ProfileService {

    /** Email format validation regex. */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /** Timestamp formatter. */
    private static final DateTimeFormatter TS_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** TA data access object. */
    private final TADao taDao;

    /**
     * Construct the service instance.
     *
     * @param taDao TA data access object
     */
    public ProfileService(TADao taDao) {
        this.taDao = taDao;
    }

    /**
     * Look up a TA profile by student ID.
     *
     * @param studentId student ID
     * @return operation result containing the TA profile
     */
    public ServiceResult<TA> getProfileByStudentId(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return ServiceResult.failure("Student ID cannot be empty.");
        }

        try {
            TA found = taDao.findByStudentId(studentId.trim());
            if (found == null) {
                return ServiceResult.failure("Profile not found.");
            }
            return ServiceResult.success(found, "Profile loaded successfully.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to read the profile.");
        }
    }

    /**
     * Save (insert or update) a TA personal profile. Includes validation of
     * all mandatory fields.
     *
     * @param name         TA name
     * @param studentId    student ID
     * @param email        email address
     * @param programme    programme/course
     * @param skills       skills list
     * @param experience   experience description
     * @param availability availability
     * @return operation result
     */
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

        String normalizedStudentId = normalize(studentId);
        TA existing = null;
        try {
            existing = taDao.findByStudentId(normalizedStudentId);
        } catch (IOException ignored) {
            // Read failure is handled when saving later.
        }

        TA profile = new TA(
                normalize(name),
                normalizedStudentId,
                normalize(email),
                normalize(programme),
                normalize(skills),
                normalize(availability));
        profile.setExperience(normalize(experience));
        profile.setUpdatedAt(LocalDateTime.now().format(TS_FORMATTER));
        if (existing != null && !isBlank(existing.getCvFilePath())) {
            profile.setCvFilePath(existing.getCvFilePath());
        }
        if (existing != null && isBlank(experience)) {
            profile.setExperience(existing.getExperience());
        }

        try {
            TA saved = taDao.saveOrUpdate(profile);
            return ServiceResult.success(saved, "Profile saved successfully.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to save the profile.");
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
            errors.add("Name cannot be empty.");
        }
        if (isBlank(studentId)) {
            errors.add("Student ID cannot be empty.");
        }
        if (isBlank(email)) {
            errors.add("Email cannot be empty.");
        } else if (!EMAIL_PATTERN.matcher(normalize(email)).matches()) {
            errors.add("Email format is invalid.");
        }
        if (isBlank(programme)) {
            errors.add("Programme cannot be empty.");
        }
        if (isBlank(skills)) {
            errors.add("Skills cannot be empty.");
        }
        if (isBlank(availability)) {
            errors.add("Availability cannot be empty.");
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
