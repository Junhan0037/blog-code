package com.javaperformance;

public class Main {
    public static void main(String[] args) {
        Performance[] performances = {new ExceptionPerformance(), new StringPerformance(), new StreamPerformance()};

        for (Performance performance : performances) {
            performance.execute();
            System.out.println();
        }

    }
}
