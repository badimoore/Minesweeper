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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 * This class handles the timer functionality for Minesweeper. The timer is incremented every 0.1s while timer is running
 *
 * @author Badi Moore
 */
public class MineTimer implements Runnable {

    private JLabel label; // the JLabel to update time value to
    private long startTime; // time at timer start
    private double currentSeconds; // currently elapsed seconds
    private boolean running; // is timer running or not?

    /**
     * Create new MineTimer
     * 
     * @param label JLabel to update time value to
     */
    public MineTimer(JLabel label) {
        this.label = label;
        running = false; // timer isn't running when object created
        currentSeconds = 0;
    }

    @Override
    public void run() {

        // run an infinite while loop, the loop will continue regardless of whether the timer is actively running or not
        // the actual timer is started or stopped using the running attribute
        while (true) {
            // if the timer is set to running and at least 0.1s has elapsed since last timer incrementation
            if (running && System.currentTimeMillis() - startTime > currentSeconds * 1000 + 100) {
                currentSeconds += 0.1;
                currentSeconds = Math.round(currentSeconds * 10) / 10.0; // round time to one decimal
                label.setText(String.valueOf(currentSeconds));
            }

            // put thread to sleep for 10ms
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(MineTimer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /**
     * Get timer value
     * 
     * @return elapsed time in seconds (one decimal accuracy)
     */
    public double getTime() {
        return currentSeconds;
    }
    
    /**
     * Start the timer
     */
    public void start() {
        currentSeconds = 0;
        label.setText("0.0");
        running = true;
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Stop the timer
     */
    public void stop() {
        // make sure timer's final value is accurate at moment of stopping
        if (System.currentTimeMillis() - startTime > currentSeconds * 1000 + 100) {
                currentSeconds += 0.1;
                currentSeconds = Math.round(currentSeconds * 10) / 10.0; // round to one decimal
                label.setText(String.valueOf(currentSeconds));
            }
        running = false;
    }
}
