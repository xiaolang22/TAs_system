package com.group19.dto;

import com.group19.model.TA;

/**
 * CV (Curriculum Vitae) upload result data transfer object (DTO).
 * <p>
 * Encapsulates the result of a CV upload operation, containing the updated TA profile
 * and the structured information automatically extracted from the CV ({@link CVExtractedInfo}).
 * All fields are immutable (final).
 * </p>
 *
 * @author Group 19
 */
public class CVUploadResult {

    /** The updated TA profile */
    private final TA profile;

    /** The structured information extracted from the CV */
    private final CVExtractedInfo extractedInfo;

    /**
     * Constructor, creates a CV upload result object.
     *
     * @param profile       the updated TA profile
     * @param extractedInfo the information extracted from the CV (an empty info object
     *                      is used as a fallback when null)
     */
    public CVUploadResult(TA profile, CVExtractedInfo extractedInfo) {
        this.profile = profile;
        this.extractedInfo = extractedInfo == null
                ? new CVExtractedInfo("", "", "")
                : extractedInfo;
    }

    /** @return the TA profile */
    public TA getProfile() {
        return profile;
    }

    /** @return the structured information extracted from the CV */
    public CVExtractedInfo getExtractedInfo() {
        return extractedInfo;
    }
}
