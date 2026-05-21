package com.group19.dto;

import com.group19.model.TA;

public class CVUploadResult {
    private final TA profile;
    private final CVExtractedInfo extractedInfo;

    public CVUploadResult(TA profile, CVExtractedInfo extractedInfo) {
        this.profile = profile;
        this.extractedInfo = extractedInfo == null
                ? new CVExtractedInfo("", "", "")
                : extractedInfo;
    }

    public TA getProfile() {
        return profile;
    }

    public CVExtractedInfo getExtractedInfo() {
        return extractedInfo;
    }
}
