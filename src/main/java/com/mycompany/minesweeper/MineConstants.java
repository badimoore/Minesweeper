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

/**
 * This class is meant to store all constants used by MineSweeper for easy reference
 *
 * @author Badi Moore
 */
public final class MineConstants {

    public static final int EASY_WIDTH = 9;
    public static final int EASY_HEIGHT = 9;
    public static final int EASY_MINES = 10;
    public static final int MEDIUM_WIDTH = 16;
    public static final int MEDIUM_HEIGHT = 16;
    public static final int MEDIUM_MINES = 40;
    public static final int EXPERT_WIDTH = 30;
    public static final int EXPERT_HEIGHT = 16;
    public static final int EXPERT_MINES = 99;

    public static final int X_OFFSET = 36;
    public static final int Y_OFFSET = 151;

    public static final int BUTTONSIZE = 40;
    public static final Color COLOR_UNREVEALED = new Color(204, 255, 153); 
    public static final Color COLOR_REVEALED = new Color(233, 233, 233);
    public static final Color COLOR_EMPTY = new Color(242, 242, 242);
    public static final Color COLOR_MINE = new Color(255, 102, 102);
    public static final Color COLOR_1 = new Color(60, 116, 230); //light blue
    public static final Color COLOR_2 = new Color(24, 189, 21); //green
    public static final Color COLOR_3 = new Color(240, 31, 31); //bright red
    public static final Color COLOR_4 = new Color(25, 35, 227); //dark blue
    public static final Color COLOR_5 = new Color(117, 29, 47); //burgundy
    public static final Color COLOR_6 = new Color(33, 181, 207); //teal
    public static final Color COLOR_7 = new Color(0, 0, 0); //black
    public static final Color COLOR_8 = new Color(105, 105, 105); //grey

    public static final int MINE = -1;
    public static final int EMPTY = 0;

    public static final String SCOREFILE = "MineScores.txt";
    public static final int MAXSCORES = 10;

}
