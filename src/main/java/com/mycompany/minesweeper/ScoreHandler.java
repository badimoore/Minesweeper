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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.JOptionPane;

/**
 * This class is used to handle all high scores in a game of minesweeper. Scores
 * are stored in a ScoreTable object for each difficulty level
 *
 * @author Badi Moore
 */
public class ScoreHandler {

    private HashMap<String, ScoreTable> scores;
    private FileHandler fileHandler;
    private boolean scoreFileExists;

    public ScoreHandler(FileHandler fileHandler) {
        this.fileHandler = fileHandler;

        // create ScoreTable for each difficulty and store in HashMap
        scores = new HashMap<>();
        scores.put("easy", new ScoreTable("easy"));
        scores.put("medium", new ScoreTable("medium"));
        scores.put("expert", new ScoreTable("expert"));

        // does score file exist
        scoreFileExists = fileHandler.fileExists(MineConstants.SCOREFILE);

        // if score file doesn't exist, ask user if file should be created
        if (!fileExists()) {
            newFileConfirmation();
        }
        // if file exists, read scores into the score tables
        if (fileExists()) {
            readScores();
        }
    }

    /**
     * Ask user if new score file should be created
     *
     * @return true if file was created, false if not
     */
    public boolean newFileConfirmation() {
        // open dialog to ask user for confirmation
        if (JOptionPane.showConfirmDialog(null, "The high score file could not be found, do you wish to create a new file?", "File not found",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            // write new file and set scoreFileExists to true, if write successful
            if (fileHandler.writeText(MineConstants.SCOREFILE, "")) {
                scoreFileExists = true;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get ScoreTable for given difficulty
     *
     * @param difficulty difficulty of scores
     * @return ScoreTable object with scores for difficulty
     */
    public ScoreTable getScores(String difficulty) {
        return scores.get(difficulty);
    }

    /**
     * Reset scores
     */
    public void resetScores() {
        // go through each ScoreTable and replace it with new ScoreTable
        for (Entry entry : scores.entrySet()) {
            entry.setValue(new ScoreTable((String) entry.getKey()));
        }
        fileHandler.writeText(MineConstants.SCOREFILE, ""); // overwrite score file with empty String
    }

    /**
     * Adds new score
     *
     * @param name score holder's name
     * @param score score
     * @param difficulty game difficulty
     */
    public void addScore(String name, double score, String difficulty) {
        scores.get(difficulty).addScore(name, score);
    }

    /**
     * Is given score a high score
     *
     * @param difficulty game difficulty
     * @param score score
     * @return is score a high score?
     */
    public boolean isHighScore(String difficulty, double score) {
        // if score file doesn't exist, return false
        if (!scoreFileExists) {
            return false;
        }
        return scores.get(difficulty).isHighScore(score);
    }

    /**
     * Does score file exist?
     *
     * @return does score file exist
     */
    public boolean fileExists() {
        return scoreFileExists;
    }

    /**
     * Read scores from file and store them in ScoreTable
     */
    private void readScores() {
        try {
            BufferedReader br = new BufferedReader(new StringReader(fileHandler.readText(MineConstants.SCOREFILE)));
            String newLine = ""; // variable to read new lines into
            // read lines until the new line is null
            while ((newLine = br.readLine()) != null) {
                String[] splitLine = newLine.split(";");

                // if line has more than two semicolons (;) there must be one or more semicolons in the name
                // the first string is always the difficulty level, the last string always the score
                // if splitLine is over 3 long, we much combine all the values in between the first and last one.
                if (splitLine.length > 3) {
                    String name = splitLine[1];
                    // combine values of indexes between first and last index and add in missing semicolons
                    for (int i = 2; i < splitLine.length - 1; i++) {
                        name += ";" + splitLine[i];
                    }

                    // create new size 3 array and assign difficulty, name and score values to it 
                    String[] newSplitLine = new String[3];
                    newSplitLine[0] = splitLine[0];
                    newSplitLine[2] = splitLine[splitLine.length - 1];
                    newSplitLine[1] = name;

                    splitLine = newSplitLine; // assign the new array to the old array's variable
                }

                // add the score to the table
                scores.get(splitLine[0]).addScore(splitLine[1], Double.valueOf(splitLine[2]));

            }
            br.close();
        } catch (IOException e) {
        }
    }

    /**
     * Write all scores to file
     */
    public void writeScores() {
        StringBuilder sb = new StringBuilder();
        scores.forEach((k, v) -> sb.append(v.toString())); // fetch each ScoreTable and 
        fileHandler.writeText(MineConstants.SCOREFILE, sb.toString());
    }
}
