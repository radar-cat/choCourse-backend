package com.example.springbootchocourse.redis;

public class ChoCourseKey extends BasePrefix{

    private ChoCourseKey(String prefix) {
        super(prefix);
    }

    public static ChoCourseKey isCourseOver = new ChoCourseKey("go");
}
