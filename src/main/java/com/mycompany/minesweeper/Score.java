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
 * This class encompasses one high score in a game of minesweeper.
 *
 * @author Badi Moore
 */
public class Score {
    
    private String name;
    private double score;
    
    public Score(String name, double score) {
        this.name = name;
        this.score = score;
    }
    
    /**
     * Get score holder's name
     * 
     * @return name of score holder
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get score
     * 
     * @return score
     */
    public double getScore() {
        return score;
    }
    
}
