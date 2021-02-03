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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * This class handles reading/writing data to disk. It has methods for reading and writing text files, loading icon images and checking if a file exists.
 *
 * @author Badi Moore
 */
public class FileHandler {

    /**
     * Reads a text file from disk, and returns it as a String
     * 
     * @param filename the name of the file
     * @return the text from the file
     */
    public String readText(String filename) {
        StringBuilder text = new StringBuilder();
        String nextLine = "";

        try {
            BufferedReader br;
            // check the first character of the filename.
            // If it starts with "/", the file is inside the JAR archive and needs to be loaded as an input stream using the getResourceAsStream() method.
            // otherwise a FileReader is used
            if (filename.startsWith("/")) {
                br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filename)));
            } else {
                br = new BufferedReader(new FileReader(filename));
            }
            // go through each line of the file and append to StringBuilder
            while ((nextLine = br.readLine()) != null) {
                text.append(nextLine + "\n");
            }
            br.close();
            return text.toString();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading file: " + e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
            return "";
        }
    }

    /**
     * Writes a text file to disk
     * 
     * @param filename the name of the file to write
     * @param text the text to write to the file
     * @return was the file successfully written
     */
    public boolean writeText(String filename, String text) {

        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(filename));
            br.write(text);
            br.close();
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing file: " + e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Checks if a file with the given name exists
     * 
     * @param filename the name of the file to check
     * @return does the file exist?
     */
    public boolean fileExists(String filename) {
        return new File(filename).exists();
    }
    
    /**
     * Loads an image from the disk and returns it as an ImageIcon
     * 
     * @param filename name of the image file
     * @return the image file packaged as an ImageIcon
     */
    public ImageIcon loadIcon(String filename) {
        try {
            return new ImageIcon(getClass().getResource(filename));            
        } catch (Exception e) {
            return new ImageIcon();
        }
    }
}
