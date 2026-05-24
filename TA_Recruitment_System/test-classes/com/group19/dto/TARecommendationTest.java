package com.group19.dto;

import com.group19.TestRunner;

/**
 * Unit tests for {@link TARecommendation}.
 *
 * @author Group19
 */
public class TARecommendationTest extends TestRunner {

    // ---- 13 getter / setter pairs ----

    // 1. taStudentId
    public void testSetAndGetTaStudentId() {
        TARecommendation rec = new TARecommendation();
        rec.setTaStudentId("2024001");
        assertEquals("2024001", rec.getTaStudentId());
    }

    // 2. taName
    public void testSetAndGetTaName() {
        TARecommendation rec = new TARecommendation();
        rec.setTaName("Alice Johnson");
        assertEquals("Alice Johnson", rec.getTaName());
    }

    // 3. matchedSkillsText
    public void testSetAndGetMatchedSkillsText() {
        TARecommendation rec = new TARecommendation();
        rec.setMatchedSkillsText("Java, Python");
        assertEquals("Java, Python", rec.getMatchedSkillsText());
    }

    // 4. missingSkillsText
    public void testSetAndGetMissingSkillsText() {
        TARecommendation rec = new TARecommendation();
        rec.setMissingSkillsText("C++, Rust");
        assertEquals("C++, Rust", rec.getMissingSkillsText());
    }

    // 5. matchedRequiredSkillCount
    public void testSetAndGetMatchedRequiredSkillCount() {
        TARecommendation rec = new TARecommendation();
        rec.setMatchedRequiredSkillCount(5);
        assertEquals(5, rec.getMatchedRequiredSkillCount());
    }

    // 6. missingRequiredSkillCount
    public void testSetAndGetMissingRequiredSkillCount() {
        TARecommendation rec = new TARecommendation();
        rec.setMissingRequiredSkillCount(2);
        assertEquals(2, rec.getMissingRequiredSkillCount());
    }

    // 7. totalRequiredSkillCount
    public void testSetAndGetTotalRequiredSkillCount() {
        TARecommendation rec = new TARecommendation();
        rec.setTotalRequiredSkillCount(7);
        assertEquals(7, rec.getTotalRequiredSkillCount());
    }

    // 8. currentWorkload
    public void testSetAndGetCurrentWorkload() {
        TARecommendation rec = new TARecommendation();
        rec.setCurrentWorkload(20);
        assertEquals(20, rec.getCurrentWorkload());
    }

    // 9. currentWorkloadLabel
    public void testSetAndGetCurrentWorkloadLabel() {
        TARecommendation rec = new TARecommendation();
        rec.setCurrentWorkloadLabel("20h/周");
        assertEquals("20h/周", rec.getCurrentWorkloadLabel());
    }

    // 10. skillMatchScore (double)
    public void testSetAndGetSkillMatchScore() {
        TARecommendation rec = new TARecommendation();
        rec.setSkillMatchScore(85.5);
        assertEquals(85.5, rec.getSkillMatchScore(), 0.001);
    }

    public void testSetAndGetSkillMatchScoreZero() {
        TARecommendation rec = new TARecommendation();
        rec.setSkillMatchScore(0.0);
        assertEquals(0.0, rec.getSkillMatchScore(), 0.001);
    }

    public void testSetAndGetSkillMatchScoreHundred() {
        TARecommendation rec = new TARecommendation();
        rec.setSkillMatchScore(100.0);
        assertEquals(100.0, rec.getSkillMatchScore(), 0.001);
    }

    // 11. workloadScore (double)
    public void testSetAndGetWorkloadScore() {
        TARecommendation rec = new TARecommendation();
        rec.setWorkloadScore(70.0);
        assertEquals(70.0, rec.getWorkloadScore(), 0.001);
    }

    public void testSetAndGetWorkloadScoreWithDecimals() {
        TARecommendation rec = new TARecommendation();
        rec.setWorkloadScore(67.89);
        assertEquals(67.89, rec.getWorkloadScore(), 0.001);
    }

    // 12. finalScore (double)
    public void testSetAndGetFinalScore() {
        TARecommendation rec = new TARecommendation();
        rec.setFinalScore(78.25);
        assertEquals(78.25, rec.getFinalScore(), 0.001);
    }

    public void testSetAndGetFinalScoreHighPrecision() {
        TARecommendation rec = new TARecommendation();
        rec.setFinalScore(92.777);
        assertEquals(92.777, rec.getFinalScore(), 0.0001);
    }

    // 13. explanation
    public void testSetAndGetExplanation() {
        TARecommendation rec = new TARecommendation();
        rec.setExplanation("该候选人技能匹配度较高，当前工作量适中，推荐录用。");
        assertEquals("该候选人技能匹配度较高，当前工作量适中，推荐录用。", rec.getExplanation());
    }

    // ---- Integration tests ----

    public void testSetAllFieldsThenGetAll() {
        TARecommendation rec = new TARecommendation();
        rec.setTaStudentId("2024001");
        rec.setTaName("Alice Johnson");
        rec.setMatchedSkillsText("Java, Python, SQL");
        rec.setMissingSkillsText("C++");
        rec.setMatchedRequiredSkillCount(3);
        rec.setMissingRequiredSkillCount(1);
        rec.setTotalRequiredSkillCount(4);
        rec.setCurrentWorkload(15);
        rec.setCurrentWorkloadLabel("15h/周");
        rec.setSkillMatchScore(75.0);
        rec.setWorkloadScore(80.0);
        rec.setFinalScore(77.5);
        rec.setExplanation("中上匹配，推荐面试");

        assertEquals("2024001", rec.getTaStudentId());
        assertEquals("Alice Johnson", rec.getTaName());
        assertEquals("Java, Python, SQL", rec.getMatchedSkillsText());
        assertEquals("C++", rec.getMissingSkillsText());
        assertEquals(3, rec.getMatchedRequiredSkillCount());
        assertEquals(1, rec.getMissingRequiredSkillCount());
        assertEquals(4, rec.getTotalRequiredSkillCount());
        assertEquals(15, rec.getCurrentWorkload());
        assertEquals("15h/周", rec.getCurrentWorkloadLabel());
        assertEquals(75.0, rec.getSkillMatchScore(), 0.001);
        assertEquals(80.0, rec.getWorkloadScore(), 0.001);
        assertEquals(77.5, rec.getFinalScore(), 0.001);
        assertEquals("中上匹配，推荐面试", rec.getExplanation());
    }

    public void testEdgeCasesNegativeScores() {
        TARecommendation rec = new TARecommendation();
        rec.setSkillMatchScore(-1.0);
        rec.setWorkloadScore(-5.5);
        rec.setFinalScore(-10.0);

        assertEquals(-1.0, rec.getSkillMatchScore(), 0.001);
        assertEquals(-5.5, rec.getWorkloadScore(), 0.001);
        assertEquals(-10.0, rec.getFinalScore(), 0.001);
    }

    public void testEdgeCasesNullStrings() {
        TARecommendation rec = new TARecommendation();
        rec.setTaStudentId(null);
        rec.setTaName(null);
        rec.setMatchedSkillsText(null);
        rec.setMissingSkillsText(null);
        rec.setCurrentWorkloadLabel(null);
        rec.setExplanation(null);

        assertNull(rec.getTaStudentId());
        assertNull(rec.getTaName());
        assertNull(rec.getMatchedSkillsText());
        assertNull(rec.getMissingSkillsText());
        assertNull(rec.getCurrentWorkloadLabel());
        assertNull(rec.getExplanation());
    }

    public void testEdgeCasesZeroInts() {
        TARecommendation rec = new TARecommendation();
        rec.setMatchedRequiredSkillCount(0);
        rec.setMissingRequiredSkillCount(0);
        rec.setTotalRequiredSkillCount(0);
        rec.setCurrentWorkload(0);

        assertEquals(0, rec.getMatchedRequiredSkillCount());
        assertEquals(0, rec.getMissingRequiredSkillCount());
        assertEquals(0, rec.getTotalRequiredSkillCount());
        assertEquals(0, rec.getCurrentWorkload());
    }

    public static void main(String[] args) {
        new TARecommendationTest().runTests();
    }
}
