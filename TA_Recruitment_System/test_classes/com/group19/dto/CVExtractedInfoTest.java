package com.group19.dto;

import com.group19.TestRunner;

/**
 * Unit tests for {@link CVExtractedInfo}.
 *
 * @author Group19
 */
public class CVExtractedInfoTest extends TestRunner {

    // ---- Constructor: trim whitespace ----

    public void testConstructorTrimsLeadingWhitespace() {
        CVExtractedInfo info = new CVExtractedInfo("  北京大学  ", "Java", "2年经验");
        assertEquals("北京大学", info.getEducation());
    }

    public void testConstructorTrimsTrailingWhitespace() {
        CVExtractedInfo info = new CVExtractedInfo("北京大学  ", "  Java  ", "2年经验");
        assertEquals("Java", info.getSkills());
    }

    public void testConstructorTrimsAllFields() {
        CVExtractedInfo info = new CVExtractedInfo("  清华大学  ", "  Python  ", "  3年经验  ");
        assertEquals("清华大学", info.getEducation());
        assertEquals("Python", info.getSkills());
        assertEquals("3年经验", info.getExperience());
    }

    public void testConstructorDoesNotTrimInternalSpaces() {
        CVExtractedInfo info = new CVExtractedInfo("计算机 科学", "Java Python", "前后端 开发");
        assertEquals("计算机 科学", info.getEducation());
        assertEquals("Java Python", info.getSkills());
        assertEquals("前后端 开发", info.getExperience());
    }

    // ---- Constructor: handling null ----

    public void testConstructorTurnsNullEducationToEmpty() {
        CVExtractedInfo info = new CVExtractedInfo(null, "Java", "经验");
        assertEquals("", info.getEducation());
    }

    public void testConstructorTurnsNullSkillsToEmpty() {
        CVExtractedInfo info = new CVExtractedInfo("教育", null, "经验");
        assertEquals("", info.getSkills());
    }

    public void testConstructorTurnsNullExperienceToEmpty() {
        CVExtractedInfo info = new CVExtractedInfo("教育", "Java", null);
        assertEquals("", info.getExperience());
    }

    public void testConstructorTurnsAllNullToEmpty() {
        CVExtractedInfo info = new CVExtractedInfo(null, null, null);
        assertEquals("", info.getEducation());
        assertEquals("", info.getSkills());
        assertEquals("", info.getExperience());
    }

    // ---- hasAny() ----

    public void testHasAnyReturnsTrueWhenEducationNonEmpty() {
        CVExtractedInfo info = new CVExtractedInfo("北京大学", "", "");
        assertTrue("hasAny() should return true when education is non-empty", info.hasAny());
    }

    public void testHasAnyReturnsTrueWhenSkillsNonEmpty() {
        CVExtractedInfo info = new CVExtractedInfo("", "Java", "");
        assertTrue("hasAny() should return true when skills is non-empty", info.hasAny());
    }

    public void testHasAnyReturnsTrueWhenExperienceNonEmpty() {
        CVExtractedInfo info = new CVExtractedInfo("", "", "2年经验");
        assertTrue("hasAny() should return true when experience is non-empty", info.hasAny());
    }

    public void testHasAnyReturnsTrueWhenMultipleFieldsNonEmpty() {
        CVExtractedInfo info = new CVExtractedInfo("清华", "Python", "3年");
        assertTrue("hasAny() should return true when multiple fields are non-empty", info.hasAny());
    }

    public void testHasAnyReturnsFalseWhenAllFieldsEmpty() {
        CVExtractedInfo info = new CVExtractedInfo("", "", "");
        assertFalse("hasAny() should return false when all fields are empty", info.hasAny());
    }

    public void testHasAnyReturnsFalseWhenAllFieldsNull() {
        CVExtractedInfo info = new CVExtractedInfo(null, null, null);
        assertFalse("hasAny() should return false when all fields are null", info.hasAny());
    }

    // ---- Getter methods ----

    public void testGettersReturnCorrectValues() {
        CVExtractedInfo info = new CVExtractedInfo("复旦大学", "C++, Java", "2年 TA 经验");
        assertEquals("复旦大学", info.getEducation());
        assertEquals("C++, Java", info.getSkills());
        assertEquals("2年 TA 经验", info.getExperience());
    }

    public static void main(String[] args) {
        new CVExtractedInfoTest().runTests();
    }
}
