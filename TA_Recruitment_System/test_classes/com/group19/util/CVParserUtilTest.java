package com.group19.util;

import com.group19.dto.CVExtractedInfo;
import com.group19.TestRunner;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Unit tests for {@link CVParserUtil}.
 * <p>
 * Full parsing of PDF/DOCX requires actual test files; this primarily tests boundary conditions:
 * null paths and non-existent file paths.
 *
 * @author Group19
 * @since 1.0
 */
public class CVParserUtilTest extends TestRunner {

    // ---- extract() with null path ----

    public void testExtractReturnsEmptyInfoForNullPath() {
        CVExtractedInfo result = CVParserUtil.extract(null);
        assertNotNull("result should not be null", result);
        assertEquals("", result.getEducation());
        assertEquals("", result.getSkills());
        assertEquals("", result.getExperience());
        assertFalse("empty info should not have any extracted results", result.hasAny());
    }

    // ---- extract() with non-existent file ----

    public void testExtractReturnsEmptyInfoForNonExistentFile() {
        Path nonExistent = Paths.get("/non/existent/file_that_does_not_exist_12345.txt");
        CVExtractedInfo result = CVParserUtil.extract(nonExistent);
        assertNotNull("result should not be null", result);
        assertEquals("", result.getEducation());
        assertEquals("", result.getSkills());
        assertEquals("", result.getExperience());
        assertFalse("non-existent file should return empty info", result.hasAny());
    }

    public void testExtractReturnsEmptyInfoForNonExistentPdfFile() {
        Path nonExistent = Paths.get("/tmp/nonexistent_cv.pdf");
        CVExtractedInfo result = CVParserUtil.extract(nonExistent);
        assertNotNull("result should not be null", result);
        assertEquals("", result.getEducation());
        assertEquals("", result.getSkills());
        assertEquals("", result.getExperience());
        assertFalse("non-existent PDF file should return empty info", result.hasAny());
    }

    public void testExtractReturnsEmptyInfoForNonExistentDocxFile() {
        Path nonExistent = Paths.get("/tmp/nonexistent_cv.docx");
        CVExtractedInfo result = CVParserUtil.extract(nonExistent);
        assertNotNull("result should not be null", result);
        assertEquals("", result.getEducation());
        assertEquals("", result.getSkills());
        assertEquals("", result.getExperience());
        assertFalse("non-existent DOCX file should return empty info", result.hasAny());
    }

    // ---- CVExtractedInfo constructor behaviour (null safety) ----

    public void testCvExtractedInfoHandlesNullFields() {
        CVExtractedInfo info = new CVExtractedInfo(null, null, null);
        assertEquals("", info.getEducation());
        assertEquals("", info.getSkills());
        assertEquals("", info.getExperience());
        assertFalse("info with all null fields should return hasAny() = false", info.hasAny());
    }

    public void testCvExtractedInfoHandlesEmptyStrings() {
        CVExtractedInfo info = new CVExtractedInfo("", "", "");
        assertEquals("", info.getEducation());
        assertEquals("", info.getSkills());
        assertEquals("", info.getExperience());
        assertFalse("info with all empty strings should return hasAny() = false", info.hasAny());
    }

    public void testCvExtractedInfoTrimsWhitespace() {
        CVExtractedInfo info = new CVExtractedInfo(
                "  Computer Science  ",
                "\tJava, Python\n",
                null);
        assertEquals("Computer Science", info.getEducation());
        assertEquals("Java, Python", info.getSkills());
        assertEquals("", info.getExperience());
        assertTrue("info with non-empty fields should return hasAny() = true", info.hasAny());
    }

    public void testCvExtractedInfoHasAnyReturnsTrueWhenEducationPresent() {
        CVExtractedInfo info = new CVExtractedInfo("Bachelor of Science", "", "");
        assertTrue(info.hasAny());
    }

    public void testCvExtractedInfoHasAnyReturnsTrueWhenSkillsPresent() {
        CVExtractedInfo info = new CVExtractedInfo("", "Java, Python", "");
        assertTrue(info.hasAny());
    }

    public void testCvExtractedInfoHasAnyReturnsTrueWhenExperiencePresent() {
        CVExtractedInfo info = new CVExtractedInfo("", "", "Software Developer Intern");
        assertTrue(info.hasAny());
    }

    // ---- utility class structure ----

    public void testClassIsFinalUtility() {
        assertTrue("CVParserUtil should be a final class to prevent subclassing",
                java.lang.reflect.Modifier.isFinal(CVParserUtil.class.getModifiers()));
    }

    public void testConstructorIsPrivate() {
        java.lang.reflect.Constructor<?>[] constructors = CVParserUtil.class.getDeclaredConstructors();
        assertEquals("utility class should have exactly one private constructor", 1, constructors.length);
        assertTrue("constructor should be private",
                java.lang.reflect.Modifier.isPrivate(constructors[0].getModifiers()));
    }

    public static void main(String[] args) {
        new CVParserUtilTest().runTestsAndExit();
    }
}
