package com.group19.service;

import com.group19.dao.ApplicationDao;
import com.group19.dto.TARecommendation;
import com.group19.model.Job;
import com.group19.model.TA;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class RecommendationServiceTest {
    public static void main(String[] args) throws Exception {
        Path applicationsFile = Files.createTempFile("applications", ".json");
        Files.writeString(applicationsFile, """
                [
                  {"applicationId":"A1","jobId":"J1","taStudentId":"TA-A","taName":"Ada","status":"ACCEPTED"},
                  {"applicationId":"A2","jobId":"J2","taStudentId":"TA-A","taName":"Ada","status":"accepted"},
                  {"applicationId":"B1","jobId":"J3","taStudentId":"TA-B","taName":"Ben","status":"ACCEPTED"},
                  {"applicationId":"B2","jobId":"J4","taStudentId":"TA-B","taName":"Ben","status":"REJECTED"}
                ]
                """, StandardCharsets.UTF_8);

        Job job = new Job();
        job.setJobId("JOB-1");
        job.setTitle("Programming TA");
        job.setRequirements("Java, Python");

        TA highSkillLowWorkload = ta("TA-C", "Cara", "Java, Python");
        TA highSkillHighWorkload = ta("TA-A", "Ada", "Java, Python");
        TA partialSkill = ta("TA-B", "Ben", "Java");

        RecommendationService service = new RecommendationService(new ApplicationDao(applicationsFile));
        List<TARecommendation> recommendations = service.recommend(job,
                Arrays.asList(highSkillHighWorkload, partialSkill, highSkillLowWorkload));

        assertEquals(3, recommendations.size(), "all TAs should be considered");
        assertEquals("TA-C", recommendations.get(0).getTaStudentId(), "lower workload should win when skills match");
        assertEquals(0, recommendations.get(0).getCurrentWorkload(), "TA-C workload");
        assertDoubleEquals(1.0, recommendations.get(0).getFinalScore(), "TA-C final score");

        TARecommendation ada = findByStudentId(recommendations, "TA-A");
        assertEquals(2, ada.getCurrentWorkload(), "only ACCEPTED applications count for workload");
        assertDoubleEquals(0.8, ada.getFinalScore(), "TA-A final score");

        TARecommendation ben = findByStudentId(recommendations, "TA-B");
        assertEquals("java", ben.getMatchedSkillsText(), "matched skills");
        assertEquals("python", ben.getMissingSkillsText(), "missing skills");
        assertEquals(1, ben.getCurrentWorkload(), "rejected applications do not count");
        assertDoubleEquals(0.5, ben.getFinalScore(), "TA-B final score");
    }

    private static TA ta(String studentId, String name, String skills) {
        TA ta = new TA();
        ta.setStudentId(studentId);
        ta.setName(name);
        ta.setSkills(skills);
        return ta;
    }

    private static TARecommendation findByStudentId(List<TARecommendation> recommendations, String studentId) {
        for (TARecommendation recommendation : recommendations) {
            if (studentId.equals(recommendation.getTaStudentId())) {
                return recommendation;
            }
        }
        throw new AssertionError("Missing recommendation for " + studentId);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + ": expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static void assertDoubleEquals(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 0.0001) {
            throw new AssertionError(message + ": expected <" + expected + "> but was <" + actual + ">");
        }
    }
}
