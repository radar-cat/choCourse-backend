package com.example.springbootchocourse.redis;

public class CourseKey extends BasePrefix{

    private CourseKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static CourseKey getCourseList = new CourseKey(60, "cl");
    public static CourseKey getCourseDetail = new CourseKey(60, "cd");
    public static CourseKey getCourseStock = new CourseKey(0, "cs");
    public static CourseKey getStartDate = new CourseKey(0, "sd");
    public static CourseKey getEndDate = new CourseKey(0, "ed");
}
