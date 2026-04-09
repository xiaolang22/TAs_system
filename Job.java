package com;

public class Job {
    private String title;
    private String course;
    private String skill;
    private String time;

    public Job(String title, String course, String skill, String time) {
        this.title = title;
        this.course = course;
        this.skill = skill;
        this.time = time;
    }

    public String getTitle() { return title; }
    public String getCourse() { return course; }
    public String getSkill() { return skill; }
    public String getTime() { return time; }
}