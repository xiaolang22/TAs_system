package com.group19.dto;

import com.group19.TestRunner;

/**
 * Unit tests for {@link ParsedCVData}.
 *
 * @author Group19
 */
public class ParsedCVDataTest extends TestRunner {

    // ---- Default constructor ----

    public void testDefaultConstructorCreatesObject() {
        ParsedCVData data = new ParsedCVData();
        assertNotNull("default constructor should create a non-null object", data);
    }

    public void testDefaultConstructorAllFieldsAreNull() {
        ParsedCVData data = new ParsedCVData();
        assertNull("name should be null", data.getName());
        assertNull("email should be null", data.getEmail());
        assertNull("studentId should be null", data.getStudentId());
        assertNull("programme should be null", data.getProgramme());
        assertNull("skills should be null", data.getSkills());
        assertNull("experience should be null", data.getExperience());
        assertNull("availability should be null", data.getAvailability());
    }

    // ---- 7 getter / setter pairs ----

    // 1. name
    public void testSetAndGetName() {
        ParsedCVData data = new ParsedCVData();
        data.setName("Alice Johnson");
        assertEquals("Alice Johnson", data.getName());
    }

    // 2. email
    public void testSetAndGetEmail() {
        ParsedCVData data = new ParsedCVData();
        data.setEmail("zhangsan@example.com");
        assertEquals("zhangsan@example.com", data.getEmail());
    }

    // 3. studentId
    public void testSetAndGetStudentId() {
        ParsedCVData data = new ParsedCVData();
        data.setStudentId("2024001");
        assertEquals("2024001", data.getStudentId());
    }

    // 4. programme
    public void testSetAndGetProgramme() {
        ParsedCVData data = new ParsedCVData();
        data.setProgramme("计算机科学");
        assertEquals("计算机科学", data.getProgramme());
    }

    // 5. skills
    public void testSetAndGetSkills() {
        ParsedCVData data = new ParsedCVData();
        data.setSkills("Java, Python, SQL");
        assertEquals("Java, Python, SQL", data.getSkills());
    }

    // 6. experience
    public void testSetAndGetExperience() {
        ParsedCVData data = new ParsedCVData();
        data.setExperience("2年 TA 经验，担任过算法课助教");
        assertEquals("2年 TA 经验，担任过算法课助教", data.getExperience());
    }

    // 7. availability
    public void testSetAndGetAvailability() {
        ParsedCVData data = new ParsedCVData();
        data.setAvailability("周一至周五 9:00-17:00");
        assertEquals("周一至周五 9:00-17:00", data.getAvailability());
    }

    // ---- Integration tests ----

    public void testSetAllFieldsThenGetAll() {
        ParsedCVData data = new ParsedCVData();
        data.setName("Bob Smith");
        data.setEmail("lisi@university.edu.cn");
        data.setStudentId("2024002");
        data.setProgramme("软件工程");
        data.setSkills("C++, Rust, Go");
        data.setExperience("1年 助教经验");
        data.setAvailability("周三全天");

        assertEquals("Bob Smith", data.getName());
        assertEquals("lisi@university.edu.cn", data.getEmail());
        assertEquals("2024002", data.getStudentId());
        assertEquals("软件工程", data.getProgramme());
        assertEquals("C++, Rust, Go", data.getSkills());
        assertEquals("1年 助教经验", data.getExperience());
        assertEquals("周三全天", data.getAvailability());
    }

    public void testOverwriteAllFields() {
        ParsedCVData data = new ParsedCVData();

        // First round of setting
        data.setName("旧名");
        data.setEmail("old@email.com");
        data.setStudentId("OLD001");
        data.setProgramme("旧专业");
        data.setSkills("旧技能");
        data.setExperience("旧经验");
        data.setAvailability("旧时间");

        // Second round of overwriting
        data.setName("新名");
        data.setEmail("new@email.com");
        data.setStudentId("NEW001");
        data.setProgramme("新专业");
        data.setSkills("新技能");
        data.setExperience("新经验");
        data.setAvailability("新时间");

        assertEquals("新名", data.getName());
        assertEquals("new@email.com", data.getEmail());
        assertEquals("NEW001", data.getStudentId());
        assertEquals("新专业", data.getProgramme());
        assertEquals("新技能", data.getSkills());
        assertEquals("新经验", data.getExperience());
        assertEquals("新时间", data.getAvailability());
    }

    public static void main(String[] args) {
        new ParsedCVDataTest().runTests();
    }
}
