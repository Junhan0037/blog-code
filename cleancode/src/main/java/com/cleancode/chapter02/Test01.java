package com.cleancode.chapter02;

import java.util.ArrayList;
import java.util.List;

public class Test01 {

    int[][] theList = new int[10][10];

    public List<int []> getThem() {
        List<int []> list1 = new ArrayList<>();
        for (int[] x : theList) {
            if (x[0] == 4) {
                list1.add(x);
            }
        }
        return list1;
    }

    //==================================================================================================================

    static final int FLAGGED = 4;
    static final int STATUS_VALUE = 0;
    int[][] gameBoard = new int[10][10];

    public List<int[]> getFlaggedCells() {
        List<int[]> flaggedCells = new ArrayList<>();
        for (int[] cell : gameBoard) {
            if (cell[STATUS_VALUE] == FLAGGED) {
                flaggedCells.add(cell);
            }
        }
        return flaggedCells;
    }

}
