package com.javaperformance;

public class StringPerformance implements Performance {

    private static final int SIZE = 100000;

    @Override
    public void execute() {
        System.out.println("=====문자일 합치기에 대한 성능 비교=====");

        long start = System.currentTimeMillis();
        test_singleLineSum();
        long end = System.currentTimeMillis();
        System.out.println("test_singleLineSum 수행시간 : " + Long.toString(end - start));

        start = System.currentTimeMillis();
        test_multiLineSum();
        end = System.currentTimeMillis();
        System.out.println("test_multiLineSum 수행시간 : " + Long.toString(end - start));
    }

    private void test_singleLineSum() {
        String s = null;

        for (int i = 0; i < SIZE; i++) {
            s = "abc" + "." + "blog code";
        }
    }

    private void test_multiLineSum() {
        String s = null;

        for (int i = 0; i < SIZE; i++) {
            s = "abc";
            s += ".";
            s += "blog code";
        }
    }

}
