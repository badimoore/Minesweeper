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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * This class extends JButton, to include the MineCoordinate information of the tile and methods for updating the button graphics depending on the MineCoordinate state
 *
 * @author Badi Moore
 */
public class MineButton extends JButton {

    private Border border;
    private MineCoordinate coordinate;
    private Color bgColor; // background color after revealed
    private Color fgColor; //  text color
    private final ImageIcon mineIcon; // icon for mine
    private final ImageIcon flagIcon; // icon for flag
    private String text; // button text for tiles with numbers

    public MineButton(MineCoordinate coordinate, ImageIcon mineIcon, ImageIcon flagIcon) {
        super();

        this.coordinate = coordinate;
        this.mineIcon = mineIcon;
        this.flagIcon = flagIcon;
        
        setFont(new Font("arial", Font.BOLD, 16));

        border = javax.swing.BorderFactory.createLineBorder(new java.awt.Color(187, 187, 187)); // this is the default border for a revealed tile
        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)); // this is the initial border for unrevealed tiles
        setBackground(MineConstants.COLOR_UNREVEALED); // set the default background color for unrevealed tiles

        // set button size
        Dimension dim = new Dimension(MineConstants.BUTTONSIZE, MineConstants.BUTTONSIZE);
        setSize(dim);
        setPreferredSize(dim);
        setMaximumSize(dim);
        setMinimumSize(dim);
    }

    /**
     * read in the final appearance settings for the revealed tile, based on the tile information in the MineCoordinate
     */
    public void setAppearance() {
        text = "";
        bgColor = MineConstants.COLOR_REVEALED;

        switch (getContent()) {
            case MineConstants.MINE:
                bgColor = MineConstants.COLOR_MINE;
                break;
            case MineConstants.EMPTY:
                border = javax.swing.BorderFactory.createEmptyBorder();
                bgColor = MineConstants.COLOR_EMPTY;
                break;
            case 1:
                fgColor = MineConstants.COLOR_1;
                text = "1";
                break;
            case 2:
                fgColor = MineConstants.COLOR_2;
                text = "2";
                break;
            case 3:
                fgColor = MineConstants.COLOR_3;
                text = "3";
                break;
            case 4:
                fgColor = MineConstants.COLOR_4;
                text = "4";
                break;
            case 5:
                fgColor = MineConstants.COLOR_5;
                text = "5";
                break;
            case 6:
                fgColor = MineConstants.COLOR_6;
                text = "6";
                break;
            case 7:
                fgColor = MineConstants.COLOR_7;
                text = "7";
                break;
            case 8:
                fgColor = MineConstants.COLOR_8;
                text = "8";
                break;
        }
    }

    /**
     * Update how the button is displayed. This method is executed on all buttons every time the user performs an action.
     */
    public void update() {
        // has the tile been revealed?
        if (isRevealed()) {
            // if tile is mine, set mine icon, otherwise set null icon to remove a possible flag icon
            if (isMine()) {
                setIcon(mineIcon);
            } else {
                setIcon(null);
            }
            setAppearance(); // set the final appearance values
            setForeground(fgColor);
            setBackground(bgColor);
            setText(text);
            setBorder(border);
        } else {
            // if flagged, set flag icon, otherwise null icon to remove flag;
            if (isFlagged()) {
                setIcon(flagIcon);
            } else {
                setIcon(null);
            }
        }
    }

    /**
     * Has the tile been flagged?
     * 
     * @return has the tile been flagged or not?
     */
    public boolean isFlagged() {
        return coordinate.isFlagged();
    }
    
    /**
     * Is there a mine on the tile?
     * 
     * @return is the tile a mine?
     */
    public boolean isMine() {
        return coordinate.isMine();
    }

    /**
     * Is the tile empty? (tile not a mine and no adjacent mines)
     * 
     * @return is the tile empty?
     */
    public boolean isEmpty() {
        return coordinate.isEmpty();
    }

    /**
     * Has the tile been revealed?
     * 
     * @return has the tile been revealed?
     */
    public boolean isRevealed() {
        return coordinate.isRevealed();
    }

    /**
     * Fetch the tile's X-axis index on the game board
     * 
     * @return X-index from left
     */
    public int getXIndex() {
        return coordinate.getX();
    }

    /**
     * Fetch the tile's Y-axis index on the game board
     * 
     * @return Y-index from top
     */
    public int getYIndex() {
        return coordinate.getY();
    }

    /**
     * Fetch the tile's content:
     * -1 = mine
     * 0 = empty
     * 1+ = number of adjacent mines
     * 
     * @return content info for tile
     */
    public int getContent() {
        return coordinate.getContent();
    }
}
