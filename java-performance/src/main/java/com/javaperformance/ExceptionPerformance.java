package com.javaperformance;

import java.util.ArrayList;
import java.util.List;

public class ExceptionPerformance implements Performance {

    private List<String> list;
    private static final int SIZE = 100000;

    @Override
    public void execute() {
        System.out.println("=====예외처리에 대한 성능 비교=====");

        long start = System.currentTimeMillis();
        test_throwException();
        long end = System.currentTimeMillis();
        System.out.println("test_throwException 수행시간 : " + Long.toString(end - start));

        start = System.currentTimeMillis();
        test_tryCatch();
        end = System.currentTimeMillis();
        System.out.println("test_tryCatch 수행시간 : " + Long.toString(end - start));

        start = System.currentTimeMillis();
        test_notNull();
        end = System.currentTimeMillis();
        System.out.println("test_notNull 수행시간 : " + Long.toString(end - start));
    }

    private void test_throwException() { // i가 홀수일 경우 null pointer exception을 throw 하여 바깥부분에서 catch
        list = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            try {
                if (i % 2 != 0) {
                    throw new NullPointerException();
                }
                String s = "performance";
                list.add(s);
            } catch (NullPointerException npe) {

            }
        }
    }

    private void test_tryCatch() { // null pointer exception이 발생할 부분인 list.add() 를 try catch로 감싼다.
        list = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            String s = null;
            if (i % 2 != 0) {
                s = "performance";
            }

            try {
                list.add(s);
                list.get(i).indexOf(0); // NPE 강제 발생
            } catch (NullPointerException npe) {

            }
        }
    }

    private void test_notNull() { // null pointer exception이 발생하지 않도록 방어 코딩
        list = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            String s = null;
            if (i % 2 != 0) {
                s = "performance";
            }
            if (s != null) {
                list.add(s);
            }
        }
    }

}
