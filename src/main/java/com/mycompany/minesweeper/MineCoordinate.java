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

/**
 * This class holds all the relevant information for one tile in a minesweeper game:
 * X,Y -coordinates of the tile
 * Is the tile a mine, if not, how many adjacent mines are there?
 * Has the tile been flagged?
 * Has the tile been revealed?
 *
 * @author Badi Moore
 */
public class MineCoordinate {
    
    private int x;
    private int y;
    private int content; // -1 = mine, 0 = empty, 1+ = adjacent mines
    private boolean flagged;
    private boolean revealed;
    
    /**
     * Constructor for MineCoordinate'
     * 
     * @param x X-coordinate from left
     * @param y Y-Coordinate from top
     */
    public MineCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
        this.content = 0;
        this.flagged = false;
    }

    /**
     * Get tile's X-coordinate
     * 
     * @return X-coordinate from left
     */
    public int getX() {
        return x;
    }

     /**
     * Get tile's Y-coordinate
     * 
     * @return Y-coordinate from top
     */
    public int getY() {
        return y;
    }

    /**
     * Get tile's content (-1 = mine, 0 = empty, 1+ = adjacent mines)
     * 
     * @return tile's content
     */
    public int getContent() {
        return content;
    }
    
    /**
     * Has the tile been flagged?
     * 
     * @return is the tile flagged?
     */
    public boolean isFlagged() {
        return this.flagged;
    }
    
    /**
     * Has the tile been revealed?
     * 
     * @return is the tile revealed? 
     */
    public boolean isRevealed() {
        return this.revealed;
    }

    /**
     * Set the tile's content (-1 = mine, 0 = empty, 1+ = adjacent mines)
     * 
     * @param content content to set  
     */
    public void setContent(int content) {
        this.content = content;
    }
    
    /**
     * Set the flagged-status of the tile
     * 
     * @param flagged set the tile to flagged or not?
     */
    public void setFlagged (boolean flagged) {
        this.flagged = flagged;
    }
    
    /**
     * Set the revealed-status of the tile
     * 
     * @param revealed is the tile revealed or not?
     */
    public void setRevealed (boolean revealed) {
        this.revealed = revealed;
    }
    
    /**
     * Is the tile a mine?
     * 
     * @return is the tile a mine?
     */
    public boolean isMine() {
        return content == MineConstants.MINE;
    }
    
    /**
     * Is the tile empty?
     * @return is the tile empty?
     */
    public boolean isEmpty() {
        return content == MineConstants.EMPTY;
    }
}
