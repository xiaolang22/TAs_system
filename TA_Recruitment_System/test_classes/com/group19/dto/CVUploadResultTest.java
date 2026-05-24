package com.group19.dto;

import com.group19.TestRunner;
import com.group19.model.TA;

/**
 * Unit tests for {@link CVUploadResult}.
 *
 * @author Group19
 */
public class CVUploadResultTest extends TestRunner {

    private TA profile;

    @Override
    protected void setUp() {
        profile = new TA();
        profile.setStudentId("2024001");
        profile.setName("Alice Johnson");
    }

    // ---- Constructor: null extractedInfo ----

    public void testConstructorWithNullExtractedInfoUsesEmpty() {
        CVUploadResult result = new CVUploadResult(profile, null);
        assertNotNull("extractedInfo should not be null", result.getExtractedInfo());
        // An empty CVExtractedInfo should have all fields as empty strings
        assertEquals("", result.getExtractedInfo().getEducation());
        assertEquals("", result.getExtractedInfo().getSkills());
        assertEquals("", result.getExtractedInfo().getExperience());
        assertFalse("empty extractedInfo should have hasAny() = false", result.getExtractedInfo().hasAny());
    }

    // ---- Constructor: valid extractedInfo ----

    public void testConstructorWithValidExtractedInfoPreservesIt() {
        CVExtractedInfo info = new CVExtractedInfo("清华大学", "Java, Python", "3年经验");
        CVUploadResult result = new CVUploadResult(profile, info);

        assertNotNull("extractedInfo should not be null", result.getExtractedInfo());
        assertEquals("清华大学", result.getExtractedInfo().getEducation());
        assertEquals("Java, Python", result.getExtractedInfo().getSkills());
        assertEquals("3年经验", result.getExtractedInfo().getExperience());
        assertTrue("extractedInfo should have hasAny() = true", result.getExtractedInfo().hasAny());
    }

    public void testConstructorWithBothNullProfile() {
        CVUploadResult result = new CVUploadResult(null, new CVExtractedInfo("教育", "技能", "经验"));
        assertNull("profile may be null", result.getProfile());
        assertNotNull("extractedInfo should not be null", result.getExtractedInfo());
    }

    // ---- getProfile() ----

    public void testGetProfileReturnsProfile() {
        CVUploadResult result = new CVUploadResult(profile, null);
        TA returned = result.getProfile();
        assertNotNull("returned profile should not be null", returned);
        assertEquals("2024001", returned.getStudentId());
        assertEquals("Alice Johnson", returned.getName());
    }

    public void testGetProfileReturnsNullWhenProfileIsNull() {
        CVUploadResult result = new CVUploadResult(null, null);
        assertNull("should return null when profile is null", result.getProfile());
    }

    // ---- getExtractedInfo() ----

    public void testGetExtractedInfoReturnsExtractedInfo() {
        CVExtractedInfo info = new CVExtractedInfo("上海交大", "Go", "1年");
        CVUploadResult result = new CVUploadResult(profile, info);
        CVExtractedInfo returned = result.getExtractedInfo();

        assertNotNull("returned extractedInfo should not be null", returned);
        assertEquals("上海交大", returned.getEducation());
        assertEquals("Go", returned.getSkills());
        assertEquals("1年", returned.getExperience());
    }

    public static void main(String[] args) {
        new CVUploadResultTest().runTests();
    }
}
