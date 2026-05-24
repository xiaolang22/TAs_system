package com.group19.dto;

import com.group19.TestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for {@link CandidateMatchResult}.
 *
 * @author Group19
 */
public class CandidateMatchResultTest extends TestRunner {

    private CandidateMatchResult result;
    private List<String> matchedSkills;
    private List<String> missingSkills;

    @Override
    protected void setUp() {
        matchedSkills = Arrays.asList("Java", "Python");
        missingSkills = Arrays.asList("C++", "Rust");
        result = new CandidateMatchResult(
                "Alice Johnson",
                "2024001",
                "Java, Python, SQL",
                75,
                matchedSkills,
                missingSkills,
                "技能匹配度较高，但缺少 C++ 和 Rust"
        );
    }

    // ---- Constructor and getters ----

    public void testConstructorSetsCandidateName() {
        assertEquals("Alice Johnson", result.getCandidateName());
    }

    public void testConstructorSetsStudentId() {
        assertEquals("2024001", result.getStudentId());
    }

    public void testConstructorSetsCandidateSkillsText() {
        assertEquals("Java, Python, SQL", result.getCandidateSkillsText());
    }

    public void testConstructorSetsMatchScore() {
        assertEquals(75, result.getMatchScore());
    }

    public void testConstructorSetsMatchedSkills() {
        List<String> returned = result.getMatchedSkills();
        assertNotNull("matchedSkills should not be null", returned);
        assertEquals(2, returned.size());
        assertTrue("should contain Java", returned.contains("Java"));
        assertTrue("should contain Python", returned.contains("Python"));
    }

    public void testConstructorSetsMissingSkills() {
        List<String> returned = result.getMissingSkills();
        assertNotNull("missingSkills should not be null", returned);
        assertEquals(2, returned.size());
        assertTrue("should contain C++", returned.contains("C++"));
        assertTrue("should contain Rust", returned.contains("Rust"));
    }

    public void testConstructorSetsNote() {
        assertEquals("技能匹配度较高，但缺少 C++ 和 Rust", result.getNote());
    }

    // ---- Edge cases ----

    public void testConstructorWithZeroMatchScore() {
        CandidateMatchResult r = new CandidateMatchResult(
                "Bob Smith", "2024002", "无", 0,
                Collections.emptyList(), Collections.singletonList("Java"), "完全不匹配"
        );
        assertEquals(0, r.getMatchScore());
        assertEquals("完全不匹配", r.getNote());
        assertEquals(0, r.getMatchedSkills().size());
        assertEquals(1, r.getMissingSkills().size());
    }

    public void testConstructorWithPerfectMatchScore() {
        CandidateMatchResult r = new CandidateMatchResult(
                "Charlie Brown", "2024003", "Java, Python, C++", 100,
                Arrays.asList("Java", "Python", "C++"),
                Collections.emptyList(),
                "完美匹配"
        );
        assertEquals(100, r.getMatchScore());
        assertEquals(3, r.getMatchedSkills().size());
        assertEquals(0, r.getMissingSkills().size());
    }

    public void testConstructorWithNullLists() {
        CandidateMatchResult r = new CandidateMatchResult(
                "赵六", "2024004", "技能文本", 50,
                null, null, null
        );
        assertEquals("赵六", r.getCandidateName());
        assertEquals(50, r.getMatchScore());
        assertNull(r.getMatchedSkills());
        assertNull(r.getMissingSkills());
        assertNull(r.getNote());
    }

    public void testConstructorWithNullStrings() {
        CandidateMatchResult r = new CandidateMatchResult(
                null, null, null, -1,
                Collections.emptyList(), Collections.emptyList(), null
        );
        assertNull(r.getCandidateName());
        assertNull(r.getStudentId());
        assertNull(r.getCandidateSkillsText());
        assertEquals(-1, r.getMatchScore());
    }

    // ---- All 7 getters independently verified ----

    public void testGetCandidateName() {
        assertEquals("Alice Johnson", result.getCandidateName());
    }

    public void testGetStudentId() {
        assertEquals("2024001", result.getStudentId());
    }

    public void testGetCandidateSkillsText() {
        assertEquals("Java, Python, SQL", result.getCandidateSkillsText());
    }

    public void testGetMatchScore() {
        assertEquals(75, result.getMatchScore());
    }

    public void testGetMatchedSkills() {
        assertEquals(matchedSkills, result.getMatchedSkills());
    }

    public void testGetMissingSkills() {
        assertEquals(missingSkills, result.getMissingSkills());
    }

    public void testGetNote() {
        assertEquals("技能匹配度较高，但缺少 C++ 和 Rust", result.getNote());
    }

    public static void main(String[] args) {
        new CandidateMatchResultTest().runTests();
    }
}
