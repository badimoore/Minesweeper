/*
 * Copyright (C) 2021 Badi Moore
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mycompany.minesweeper;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class is used to generate a grid of MineCoordinates for a game of
 * minesweeper. The size of the grid and number of mines can be specified.
 *
 * @author Badi Moore
 */
public class MineFieldFactory {

    /**
     * Create a 2-dimensional array of MineCoordinates for use in a game of
     * minesweeper.
     *
     * @param width number of tiles in x-dimension
     * @param height number of tiles in y-dimension
     * @param mineCount number of mines
     * @return 2-d array of MineCoordinates - coordinate (0,0) is at top left
     */
    public static MineCoordinate[][] createMineField(int width, int height, int mineCount) {

        MineCoordinate[][] mineField = new MineCoordinate[width][height];
        ArrayList<MineCoordinate> mineList = new ArrayList<>();

        // initially create and assign the coordinates to an ArrayList
        // this will be used to randomly assign mine locations before transferring the MineCoordinates to the 2d array
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                mineList.add(new MineCoordinate(i, j));
            }
        }

        mineList = shuffleCoordinates(mineList); // shuffle the coordinate indexes
        addMines(mineList, mineCount); // add the mines

        // move the MineCoordinates to the array
        for (MineCoordinate mine : mineList) {
            mineField[mine.getX()][mine.getY()] = mine;
        }

        // set value on adjacent mines for each coordinate
        setAdjacentMines(mineField);

        return mineField;
    }

    /**
     * Set the adjacent mine values for MineCoordinate grid
     * mines must be assigned before running this
     * 
     * @param mineField the 2d array of MineCoordinates describing the game board
     */
    public static void setAdjacentMines(MineCoordinate[][] mineField) {
        // iterate through each coordinate and calculate adjacent mines for each one that doesn't have a mine.
        for (int i = 0; i < mineField.length; i++) {
            for (int j = 0; j < mineField[i].length; j++) {
                // skip over any mines
                if (mineField[i][j].isMine()) {
                    continue;
                }
                mineField[i][j].setContent(getAdjacentMines(mineField, i, j)); // fetch the value for adjacent mines
            }
        }
    }

    /**
     * Counts the number of mines adjacent to one one coordinate in a MineCoordinate grid
     * 
     * @param mineField grid of MineCoordinates
     * @param x X-coordinate to calculate from
     * @param y Y-coordinate to calculate from
     * @return number of mines adjacent to (x,y)
     */
    public static int getAdjacentMines(MineCoordinate[][] mineField, int x, int y) {
        int mines = 0;

        // iterate through a 3x3 grid centered on the coordinate (x,y)
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                // skip over any coordinates that are out of bounds, e.g. negative values, or too high for grid size
                if (i < 0 || i >= mineField.length
                        || j < 0 || j >= mineField[i].length) {
                    continue;
                }
                // skip over reference coordinate
                if (i == x && j == y) {
                    continue;
                }
                // count any mines for coordinates that pass first two tests
                if (mineField[i][j].isMine()) {
                    mines++;
                }
            }
        }
        return mines;
    }

    /**
     * Randomly shuffle the contents of a MineCoordinate ArrayList and return shuffled list
     * 
     * @param baseList list to shuffle
     * @return shuffled list
     */
    private static ArrayList<MineCoordinate> shuffleCoordinates(ArrayList<MineCoordinate> baseList) {

        ArrayList<MineCoordinate> shuffledList = new ArrayList<>();
        
        // remove a random element from original list and add to new list
        // repeat for every element in original list
        int size = baseList.size();
        for (int i = 0; i < size; i++) {
            shuffledList.add(baseList.remove(new Random().nextInt(baseList.size())));
        }
        // new list is now in random order
        return shuffledList;
    }

    /**
     * Set the first coordinates in ArrayList as mines. The List should be shuffled with shuffleCoordinates() first to ensure random placement.
     * 
     * @param mineList coordinate list to add mines to
     * @param mines number of mines to add
     */
    private static void addMines(ArrayList<MineCoordinate> mineList, int mines) {
        for (int i = 0; i < mines; i++) {
            mineList.get(i).setContent(MineConstants.MINE); // set content value of coordinate as mine
        }
    }
}
