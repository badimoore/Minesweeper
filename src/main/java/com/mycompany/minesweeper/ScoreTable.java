/*
 * Copyright (C) 2021 Badi Moore
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

/**
 * This class keeps track off all Scores for one difficulty level of minesweeper
 *
 * @author Badi Moore
 */
public class ScoreTable {

    private ArrayList<Score> scores;
    private String difficulty;

    /**
     *  Create new ScoreTable
     * 
     * @param difficulty difficulty of table
     */
    public ScoreTable(String difficulty) {
        this.difficulty = difficulty;
        scores = new ArrayList<>();
    }

    /**
     * Get score at given index
     * 
     * @param index of score
     * @return score at index
     */
    public Score getScoreAtIndex(int index) {
        return scores.get(index);
    }

    /**
     * Get all scores
     * 
     * @return ArrayList with Scores 
     */
    public ArrayList<Score> getScoreList() {
        return scores;
    }

    /**
     * Would the given score be inserted onto this high score table?
     * 
     * @param score to be tested
     * @return would the score be inserted into this table
     */
    public boolean isHighScore(double score) {
        // iterate through all scores in table, if given score is better (smaller) than any of them, return true
        for (int i = 0; i < 10 && i < scores.size(); i++) {
            if (score < scores.get(i).getScore()) {
                return true;
            }
        }
        // if score list still has room, return true
        if (scores.size() < MineConstants.MAXSCORES) {
            return true;
        }
        return false;
    }
    
    /**
     * Insert score into table
     * 
     * @param name score holder's name
     * @param score score to insert
     */
    public void addScore(String name, double score) {
        // iterate through each index of list
        for (int i = 0; i < MineConstants.MAXSCORES; i++) {
            // if list has fewer entries than max, add score to available index
            if (i >= scores.size()) {
                scores.add(i, new Score(name, score));
                return;
            // if this score is better than score at index, insert score into index
            // the ArrayList will push worse scores down the list
            } else if (score < scores.get(i).getScore()) {
                scores.add(i, new Score(name, score));
                
                // if list is now longer than max, remove last entry
                if (scores.size() > MineConstants.MAXSCORES) {
                    scores.remove(MineConstants.MAXSCORES);
                }
                return;
            }
        }
    }
    
    /**
     * Return String representation of Score table. This is used to write scores to file
     * @return 
     */
    @Override
    public String toString() {
        // write each score on own line in format <difficulty>;<name>;<score>
        String returnValue = "";
        for (Score score : scores) {
            returnValue += difficulty + ";" + score.getName() + ";" + score.getScore() + "\n";
        }
        return returnValue;
    }
}
