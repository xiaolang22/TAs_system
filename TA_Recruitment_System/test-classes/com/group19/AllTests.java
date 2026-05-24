package com.group19;

/**
 * Master test suite that runs all unit tests across every layer of the project.
 *
 * <p>Run this class to execute every test in one go.  The suite discovers
 * all test classes and calls each one's {@code runTests()} method in turn,
 * producing a final aggregated pass/fail summary.
 *
 * <p>To run from the command line (from the project root):
 * <pre>{@code
 * javac -cp "web/WEB-INF/lib/*;src;test-classes" -d out \
 *       test-classes/com/group19/AllTests.java
 * java  -cp "web/WEB-INF/lib/*;out" com.group19.AllTests
 * }</pre>
 *
 * @author Group19
 * @since 1.0
 */
public class AllTests {

    @SuppressWarnings("unchecked")
    private static final Class<? extends TestRunner>[] SUITES = new Class[]{
            // ---- model ----
            com.group19.model.ApplicationTest.class,
            com.group19.model.JobTest.class,
            com.group19.model.LoginUserTest.class,
            com.group19.model.NotificationTest.class,
            com.group19.model.SavedJobTest.class,
            com.group19.model.TATest.class,
            com.group19.model.TimelineEventTest.class,
            com.group19.model.UserAccountTest.class,

            // ---- dto ----
            com.group19.dto.AdminFeedItemTest.class,
            com.group19.dto.CVExtractedInfoTest.class,
            com.group19.dto.CVUploadResultTest.class,
            com.group19.dto.CandidateMatchResultTest.class,
            com.group19.dto.DeadlineReminderViewTest.class,
            com.group19.dto.ParsedCVDataTest.class,
            com.group19.dto.ServiceResultTest.class,
            com.group19.dto.TARecommendationTest.class,
            com.group19.dto.TaApplicationOverviewTest.class,
            com.group19.dto.TaWorkloadRowTest.class,

            // ---- dao ----
            com.group19.dao.ApplicationDaoTest.class,
            com.group19.dao.JobDaoTest.class,
            com.group19.dao.NotificationDaoTest.class,
            com.group19.dao.SavedJobDaoTest.class,
            com.group19.dao.TADaoTest.class,
            com.group19.dao.TimelineDaoTest.class,
            com.group19.dao.UserAccountDaoTest.class,

            // ---- service ----
            com.group19.service.ApplicationServiceTest.class,
            com.group19.service.AuthServiceTest.class,
            com.group19.service.JobServiceTest.class,
            com.group19.service.SavedJobServiceTest.class,

            // ---- util ----
            com.group19.util.ApplicationServiceFactoryTest.class,
            com.group19.util.CVParserUtilTest.class,
            com.group19.util.DataPathResolverTest.class,
            com.group19.util.FileUploadUtilTest.class,
            com.group19.util.HtmlEscapeTest.class,
            com.group19.util.JsonFileUtilTest.class,

            // ---- filter ----
            com.group19.filter.AuthFilterTest.class,
    };

    public static void main(String[] args) {
        TestRunner.runAll(SUITES);
    }
}
