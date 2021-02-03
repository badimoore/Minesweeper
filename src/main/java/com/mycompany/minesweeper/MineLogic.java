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

import java.util.Random;

/**
 * This class handles the game flow for a game of minesweeper on an abstract level.
 * It takes user input and updates changes to the game board by modifying values in individual MineCoordinates.
 *
 * @author Badi Moore
 */
public class MineLogic {

    // values for winstate
    public static final int UNFINISHED = 0;
    public static final int WON = 1;
    public static final int LOST = 2;

    private MineCoordinate[][] coordinateGrid;
    private int mines; // number of unflagged mines
    private int unrevealed; // number of unrevealed tiles;
    private int flags; // number of flagged tiles;
    private int winState; // is game won, unfinished, or lost?
    private boolean firstMove; // are all tiles still unrevealed? this is used to make sure the first revealed tile can't be a mine

    /**
     * Set up game
     * 
     * @param coordinateGrid 2d array of MineCoordinate objects
     * @param mines number of mines in game
     */
    public MineLogic(MineCoordinate[][] coordinateGrid, int mines) {
        this.coordinateGrid = coordinateGrid;
        this.mines = mines;
        flags = 0;
        firstMove = true;
        unrevealed = coordinateGrid.length * coordinateGrid[0].length; // total size of game board
        winState = UNFINISHED;
    }

    /**
     * Get number of unflagged mines
     * 
     * @return unflagged mines
     */
    public int getMines() {
        return mines;
    }
    
    /**
     * Is tile at (x,y) revealed?
     * @param x tile X-coordinate from left
     * @param y tile Y-coordinate from top
     * @return is tile revealed?
     */
    public boolean isRevealed(int x, int y) {
        return coordinateGrid[x][y].isRevealed();
    }
    
    /**
     * Is tile at (x,y) flagged?
     * @param x tile X-coordinate from left
     * @param y tile Y-coordinate from top
     * @return is tile flagged?
     */
    public boolean isFlagged(int x, int y) {
        return coordinateGrid[x][y].isFlagged();
    }

    /**
     * Handle a user left click on a tile
     * 
     * @param x X coordinate of clicked tile from left
     * @param y Y coordinate of clicked tile from top
     */
    public void leftMouse(int x, int y) {
        // if this is the first move and user just clicked on a mine, move mine to new tile first before continuing
        if (coordinateGrid[x][y].isMine() && firstMove) {
            moveMine(x, y);
        }
        firstMove = false;
        // if coordinate is not revealed, reveal it
        if (!coordinateGrid[x][y].isRevealed()) {
            reveal(x, y);
        }
        checkWinState();
    }

    /**
     * Handle a user right click on a tile
     * @param x X coordinate of clicked tile from left
     * @param y Y coordinate of clicked tile from top
     */
    public void rightMouse(int x, int y) {
        MineCoordinate coord = coordinateGrid[x][y];
        
        // abort, if tile has already been revealed
        if (coord.isRevealed()) {
            return;
        }
        // remove flag if tile already flagged
        if (coord.isFlagged()) {
            coord.setFlagged(false);
            flags--;
            mines++;
        // set flagged if the tile has not been revealed and there are still unflagged mines
        } else if (!coord.isRevealed() && mines > 0) {
            coord.setFlagged(true);
            mines--;
            flags++;
        }
        checkWinState();
    }
    
    /**
     * Handle a user double-click on a tile
     * This will reveal all non-flagged tiles adjacent to the double-clicked tile, if the number of adjacent mines equals the number of adjacent flags
     * 
     * @param x X coordinate of clicked tile from left
     * @param y Y coordinate of clicked tile from top
     */
    public void doubleClick(int x, int y) {
        // does the clicked coordinate
        // -have adjacent mines
        // -have an equal number of adjacent flags and adjacent mines
        if (coordinateGrid[x][y].getContent() > 0
            && coordinateGrid[x][y].getContent() == getAdjacentFlags(coordinateGrid, x, y)) {
            // iterate through a 3x3 box centered on the coordinate
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    // jump over coordinates that are out of bounds for coordinateGrid
                    if (i < 0 || i >= coordinateGrid.length
                        || j < 0 || j >= coordinateGrid[i].length) {
                        continue;
                    }
                    // jump over coordinate itself
                    if (i == x && j == y) {
                        continue;
                    }
                    // if coordinate passed previous tests and is not flagged, reveal it
                    if (!coordinateGrid[i][j].isFlagged()) {
                        reveal(i, j);
                    }
                }
            }
            checkWinState();
        }
    }

    /**
     * Reveal a tile
     * 
     * @param x X-coordinate of tile to be revealed
     * @param y Y-coordinate of tile to be revealed
     */
    private void reveal(int x, int y) {
        MineCoordinate coord = coordinateGrid[x][y];
        
        if (coord.isRevealed()) {
            return;
        }
        // if tile is flagged, remove flag
        if (coord.isFlagged()) {
            coord.setFlagged(false);
            flags--;
            mines++;
        }
        // decide action based on coordinate's content
        switch (coord.getContent()) {
            // if tile is mine, game is lost
            case MineConstants.MINE:
                winState = LOST;
                break;
            // if tile is empty, reveal it, then reveal all surrounding tiles
            case MineConstants.EMPTY:
                coord.setRevealed(true);
                unrevealed--;
                cascadeEmptyTiles( x, y);
                break;
            // otherwise, reveal tile
            default:
                coord.setRevealed(true);
                unrevealed--;
        }
    }
    
    /**
     * Handle player revealing empty tile.
     * If empty tile is revealed, all surrounding tiles are also revealed. If one or more of them is also empty, this process repeats until no more empty tiles are revealed.
     * 
     * @param x X-coordinate of empty tile
     * @param y Y-coordinate of empty tile
     */
    private void cascadeEmptyTiles(int x, int y) {

        // iterate through 3x3 box centered on empty tile
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                // skip over coordinates that are out of bounds for coordinateGrid and the empty tile itself
                if (i < 0 || i >= coordinateGrid.length
                        || j < 0 || j >= coordinateGrid[i].length
                        || (i == x && j == y)) {
                    continue;
                }
                // if the coordinate has not already been revealed, reveal it
                // if this coordinate is also empty, it will lead back to this method in a "chain reaction"
                if (!coordinateGrid[i][j].isRevealed()) {
                    reveal(i, j);
                }
            }
        }
    }
    
    /**
     * Move mine from given coordinate to random new coordinate
     * This is used when the first tile revealed is a mine, to prevent a game over on move 1
     * @param x X-coordinate of tile to move mine from
     * @param y Y-coordinate of tile to move mine from
     */
    private void moveMine(int x, int y) {
        Random random = new Random();
        
        // define a new coordinate for the mine
        // it is initially set to the coordinate of the mine that was just clicked.
        MineCoordinate newCoordinate = coordinateGrid[x][y];
        // go through random coordinates until find one that isn't a mine
        while (newCoordinate.isMine()) {
            int newX = random.nextInt(coordinateGrid.length);
            int newY = random.nextInt(coordinateGrid[0].length);
            
            newCoordinate = coordinateGrid[newX][newY];
        }
        
        // set new coordinate as mine and old coordinate as empty
        newCoordinate.setContent(MineConstants.MINE);
        coordinateGrid[x][y].setContent(MineConstants.EMPTY);
        
        // now that the mine has been moved, we must recalculate the adjacency information for the entire MineCoordinate grid
        MineFieldFactory.setAdjacentMines(coordinateGrid);
    }
    
    /**
     * Get the number of adjacent flags for given coordinate
     * 
     * @param mineField 2d array of MineCoordinates
     * @param x X-coordinate for tile
     * @param y Y-coordinate for tile
     * @return number of flags adjacent to (x,y)
     */
    public static int getAdjacentFlags(MineCoordinate[][] mineField, int x, int y) {
        int flags = 0;

        // iterate through 3x3 box centered on (x,y)
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                //skip over coordinates that are out of bounds for mineField
                if (i < 0 || i >= mineField.length
                        || j < 0 || j >= mineField[i].length) {
                    continue;
                }
                //skip over center tile
                if (i == x && j == y) {
                    continue;
                }
                // add up flags
                if (mineField[i][j].isFlagged()) {
                    flags++;
                }
            }
        }
        return flags;
    }
    
    /**
     * Set game's win state
     * This is used when the player surrenders the game.
     * 
     * @param state new win state
     */
    public void setWinState(int state) {
        winState = state;
    }
    
    /**
     * Check if game has been won.
     * The game is won if all tiles but mines have been revealed, and every mine tile has been flagged
     */
    private void checkWinState() {
        if (unrevealed == flags && winState != LOST) {
            winState = WON;
        }
    }
    
    /**
     * Get the game's win state
     * 
     * @return the win state of the game
     */
    public int getWinState() {
        return winState;
    }
}
