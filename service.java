package com;

import com.Job;
import java.util.*;
import java.util.stream.Collectors;

public class service {

    private static final List<Job> jobs = new ArrayList<>();

    static {
        jobs.add(new Job("数据结构助教", "数据结构", "Java", "周一"));
        jobs.add(new Job("AI课程助教", "人工智能", "Python", "周三"));
        jobs.add(new Job("操作系统助教", "操作系统", "C++", "周五"));
        jobs.add(new Job("Web开发助教", "Web开发", "JavaScript", "周二"));
    }

    public static List<Job> search(String keyword, String skill) {
        return jobs.stream()
                .filter(j -> (keyword == null || j.getTitle().contains(keyword) || j.getCourse().contains(keyword)))
                .filter(j -> (skill == null || skill.isEmpty() || j.getSkill().equalsIgnoreCase(skill)))
                .collect(Collectors.toList());
    }
}
