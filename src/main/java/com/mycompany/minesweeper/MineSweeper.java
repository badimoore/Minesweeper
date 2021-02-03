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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * This is the main UI class for Minesweeper
 *
 * @author Badi Moore
 */
public class MineSweeper extends javax.swing.JFrame {

    /**
     * Creates new form MineSweeper
     */
    public MineSweeper(boolean test) {
        testMode = test; // sets the testMode variable, which determines whether tile info is visible for all tiles at all times.

        fileHandler = new FileHandler(); // create new filehandler for reading/writing data

        initComponents();
        
        // add start picture to gameboard
        JLabel startPicture = new JLabel(fileHandler.loadIcon("/start_filter.png"));
        GridLayout gl = new GridLayout(1, 1);
        gameboard.setLayout(gl);
        gameboard.add(startPicture);

        setLocationRelativeTo(null); // moves window to center of screen

        scoreHandler = new ScoreHandler(fileHandler); // create new scorehandler to keep track of high scores

        difficulty = "easy"; // default difficulty at beginning

        getContentPane().setBackground(new java.awt.Color(250, 250, 250));

        // create and start new timer + feed it the timer JLabel
        timer = new MineTimer(jLabelTimer);
        new Thread(timer).start();
    }

    /**
     * This method initializes and starts a new game of minesweeper.
     *
     * @param xSize horizontal of game grid
     * @param ySize vertical size of game grid
     * @param mines number of mines
     */
    private void startGame(int xSize, int ySize, int mines) {
        // set the stop button to "happy face" and enable (button is disabled at start)
        jButtonStop.setText(":)");
        jButtonStop.setEnabled(true);

        gameboard.removeAll(); //empty out previous buttons from game area
        gameboard.repaint();

        // Create new game grid and start up game logic
        coordinateGrid = MineFieldFactory.createMineField(xSize, ySize, mines);
        mineLogic = new MineLogic(coordinateGrid, mines);

        updateMineCounter();

        // setup layout for game buttons
        GridLayout layout = new GridLayout(ySize, xSize);
        layout.setHgap(0);
        layout.setVgap(0);
        gameboard.setLayout(layout);

        // resize gameboard and program window based on game size
        Dimension dimBoard = new Dimension(xSize * MineConstants.BUTTONSIZE, ySize * MineConstants.BUTTONSIZE);
        Dimension dimWindow = new Dimension(xSize * MineConstants.BUTTONSIZE + MineConstants.X_OFFSET, ySize * MineConstants.BUTTONSIZE + MineConstants.Y_OFFSET);
        gameboard.setMaximumSize(dimBoard);
        gameboard.setMinimumSize(dimBoard);
        gameboard.setPreferredSize(dimBoard);
        this.setSize(dimWindow);

        // load button icons
        ImageIcon mineIcon = fileHandler.loadIcon("/mine.png");
        ImageIcon flagIcon = fileHandler.loadIcon("/flag.png");

        // iterate through all game coordinates and create + initialize button for each tile
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                MineButton button = new MineButton(coordinateGrid[x][y], mineIcon, flagIcon);
                button.setFocusPainted(false); // prevents button text from being selected while clicking on button
                addListener(button); // add button listener to handle clicks
                gameboard.add(button);

                // display info on each button if program started in test mode.
                if (testMode) {
                    addTestText(button);
                }
            }
        }
        gameboard.revalidate();
        this.revalidate();

        timer.start(); // start counting time/updating timer label
    }

    /**
     * Refresh the state of each button on game board
     */
    public void refreshBoard() {

        // iterate through each button on gameboard and tell it to update
        for (Component comp : gameboard.getComponents()) {
            MineButton mb = (MineButton) comp;
            mb.update();

            // if program was launched in test mode, display coordinate content on button, so testers can see what's going on in game
            if (testMode) {
                addTestText(mb);
            }
        }

        updateMineCounter();

        // check if game has been won or lost
        if (mineLogic.getWinState() == MineLogic.LOST) {
            loseGame();
        }
        if (mineLogic.getWinState() == MineLogic.WON) {
            winGame();
        }
    }

    /**
     * Adds text to button for use in test mode
     *
     * @param mb MineButton to set text on
     */
    private void addTestText(MineButton mb) {
        // if coordinate is mine, display "M", if it is empty, display nothing, otherwise display adjacent mines 
        if (mb.getContent() == MineConstants.MINE) {
            mb.setText("M");
        } else if (mb.getContent() == MineConstants.EMPTY) {
            mb.setText("");
        } else {
            mb.setText(String.valueOf(mb.getContent()));
        }
    }

    /**
     * Add mouse listener to MineButton for handling mouse clicks
     *
     * @param mb MineButton to add listener to
     */
    private void addListener(MineButton mb) {

        mb.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
            }

            @Override
            public void mousePressed(MouseEvent me) {
                // skip if button not enabled
                if (!mb.isEnabled()) {
                    return;
                }
                // left click
                if (me.getButton() == MouseEvent.BUTTON1) {
                    mineLogic.leftMouse(mb.getXIndex(), mb.getYIndex());
                }
                // right click
                if (me.getButton() == MouseEvent.BUTTON3) {
                    mineLogic.rightMouse(mb.getXIndex(), mb.getYIndex());
                }
                // double-click
                if (me.getClickCount() == 2) {
                    mineLogic.doubleClick(mb.getXIndex(), mb.getYIndex());
                }
                refreshBoard(); // after mouse clicked and logic handled changes in game, refresh the game board to reflect changed game state
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
            }
        });

    }

    /**
     * Update current mine value to mine counter
     */
    public void updateMineCounter() {
        int mines = mineLogic.getMines(); // get current mine value from logic

        // add 0 to beginning of string, if value is <10, e.g. 01, 02, ...
        String newValue = "";
        if (mines < 10) {
            newValue = "0";
        }
        newValue += String.valueOf(mines);
        jLabelMines.setText(newValue);
    }

    /**
     * Lost game procedure
     */
    private void loseGame() {
        timer.stop(); // stop counting time

        // Go through each MineCoordinate in grid
        // if coordinate is mine, set it as revealed
        // this will reveal locations of all mines on board if game lost.
        for (MineCoordinate[] mcColumn : coordinateGrid) {
            for (MineCoordinate mc : mcColumn) {
                if (mc.isMine()) {
                    mc.setRevealed(true);
                }
            }
        }
        jButtonStop.setText(":("); // set sad smiley face on stop button
        endGame(); // general end game procedure
    }

    private void winGame() {
        timer.stop(); // stop counting time

        // if score is high score, open frame to ask player to enter name
        if (scoreHandler.isHighScore(difficulty, timer.getTime())) {
            NameEntryFrame name = new NameEntryFrame(timer.getTime(), difficulty, scoreHandler);
            name.setLocationRelativeTo(this); // open new frame on top of this one
            name.setVisible(true);
        }
        jButtonStop.setText(":D"); // set stop button text to big grin face
        endGame(); // general end game procedure
    }

    /**
     * Final wrap up for ending game
     */
    private void endGame() {

        // go through each button on game board, update the visuals and disable button
        for (Component comp : gameboard.getComponents()) {
            MineButton mb = (MineButton) comp;
            mb.update();
            mb.setEnabled(false);
        }
    }

    /**
     * Selects parameters and starts a new game by difficulty
     *
     * @param gameType new game difficulty
     */
    private void newGame(String gameType) {
        switch (gameType) {
            case "easy":
                startGame(MineConstants.EASY_WIDTH, MineConstants.EASY_HEIGHT, MineConstants.EASY_MINES);
                break;
            case "medium":
                startGame(MineConstants.MEDIUM_WIDTH, MineConstants.MEDIUM_HEIGHT, MineConstants.MEDIUM_MINES);
                break;
            case "expert":
                startGame(MineConstants.EXPERT_WIDTH, MineConstants.EXPERT_HEIGHT, MineConstants.EXPERT_MINES);
                break;
        }
    }

    /**
     * Shifts resized window, so it stays centered on same spot.
     *
     * @param oldGameType previous game difficulty
     * @param newGameType new game difficulty
     */
    private void relocateWindow(String oldGameType, String newGameType) {
        // get current location of window
        Point p = getLocation();

        int oldSizeX = 0;
        int oldSizeY = 0;
        int newSizeX = 0;
        int newSizeY = 0;

        // get window width and height for old difficulty
        switch (oldGameType) {
            case "easy":
                oldSizeX = MineConstants.EASY_WIDTH * MineConstants.BUTTONSIZE + MineConstants.X_OFFSET;
                oldSizeY = MineConstants.EASY_HEIGHT * MineConstants.BUTTONSIZE + MineConstants.Y_OFFSET;
                break;
            case "medium":
                oldSizeX = MineConstants.MEDIUM_WIDTH * MineConstants.BUTTONSIZE + MineConstants.X_OFFSET;
                oldSizeY = MineConstants.MEDIUM_HEIGHT * MineConstants.BUTTONSIZE + MineConstants.Y_OFFSET;
                break;
            case "expert":
                oldSizeX = MineConstants.EXPERT_WIDTH * MineConstants.BUTTONSIZE + MineConstants.X_OFFSET;
                oldSizeY = MineConstants.EXPERT_HEIGHT * MineConstants.BUTTONSIZE + MineConstants.Y_OFFSET;
                break;
        }
        // get window width and height for new difficulty
        switch (newGameType) {
            case "easy":
                newSizeX = MineConstants.EASY_WIDTH * MineConstants.BUTTONSIZE + MineConstants.X_OFFSET;
                newSizeY = MineConstants.EASY_HEIGHT * MineConstants.BUTTONSIZE + MineConstants.Y_OFFSET;
                break;
            case "medium":
                newSizeX = MineConstants.MEDIUM_WIDTH * MineConstants.BUTTONSIZE + MineConstants.X_OFFSET;
                newSizeY = MineConstants.MEDIUM_HEIGHT * MineConstants.BUTTONSIZE + MineConstants.Y_OFFSET;
                break;
            case "expert":
                newSizeX = MineConstants.EXPERT_WIDTH * MineConstants.BUTTONSIZE + MineConstants.X_OFFSET;
                newSizeY = MineConstants.EXPERT_HEIGHT * MineConstants.BUTTONSIZE + MineConstants.Y_OFFSET;
                break;
        }

        // calculate difference in X and Y location in order to keep window cebntered at same spot
        int deltaX = (oldSizeX - newSizeX) / 2;
        int deltaY = (oldSizeY - newSizeY) / 2;

        p.translate(deltaX, deltaY); // set target coordinate to new calculated one

        // if window left edge would move beyond screen left edge, set window to left edge of screen
        // if window top edge would move above screen top, set window to screen top
        if (p.x < 0) {
            p.x = 0;
        }
        if (p.y < 0) {
            p.y = 0;
        }
        setLocation(p); // move window
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        gameboard = new javax.swing.JPanel();
        jButtonStop = new javax.swing.JButton();
        jLabelTimer = new javax.swing.JLabel();
        jLabelMines = new javax.swing.JLabel();
        jLabelTimeIcon = new javax.swing.JLabel();
        jLabelMinesIcon = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemEasy = new javax.swing.JMenuItem();
        jMenuItemMedium = new javax.swing.JMenuItem();
        jMenuItemExpert = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuInfo = new javax.swing.JMenu();
        jMenuItemHiscores = new javax.swing.JMenuItem();
        jMenuItemRules = new javax.swing.JMenuItem();
        jMenuAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Minesweeper");
        setLocation(new java.awt.Point(0, 0));
        setMaximumSize(new java.awt.Dimension(396, 511));
        setMinimumSize(new java.awt.Dimension(396, 511));
        setPreferredSize(new java.awt.Dimension(396, 511));
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        gameboard.setBackground(new java.awt.Color(238, 255, 237));
        gameboard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        gameboard.setMinimumSize(new java.awt.Dimension(360, 360));
        gameboard.setPreferredSize(new java.awt.Dimension(360, 360));

        javax.swing.GroupLayout gameboardLayout = new javax.swing.GroupLayout(gameboard);
        gameboard.setLayout(gameboardLayout);
        gameboardLayout.setHorizontalGroup(
            gameboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 358, Short.MAX_VALUE)
        );
        gameboardLayout.setVerticalGroup(
            gameboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 358, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(gameboard, gridBagConstraints);

        jButtonStop.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jButtonStop.setText(":)");
        jButtonStop.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonStop.setEnabled(false);
        jButtonStop.setFocusPainted(false);
        jButtonStop.setMaximumSize(new java.awt.Dimension(60, 60));
        jButtonStop.setMinimumSize(new java.awt.Dimension(60, 60));
        jButtonStop.setPreferredSize(new java.awt.Dimension(60, 60));
        jButtonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        getContentPane().add(jButtonStop, gridBagConstraints);

        jLabelTimer.setBackground(new java.awt.Color(0, 0, 0));
        jLabelTimer.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelTimer.setForeground(new java.awt.Color(255, 255, 0));
        jLabelTimer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTimer.setText("Time");
        jLabelTimer.setMaximumSize(new java.awt.Dimension(60, 20));
        jLabelTimer.setMinimumSize(new java.awt.Dimension(60, 20));
        jLabelTimer.setOpaque(true);
        jLabelTimer.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(30, 10, 0, 0);
        getContentPane().add(jLabelTimer, gridBagConstraints);

        jLabelMines.setBackground(new java.awt.Color(0, 0, 0));
        jLabelMines.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelMines.setForeground(new java.awt.Color(255, 255, 0));
        jLabelMines.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMines.setText("Mines");
        jLabelMines.setMaximumSize(new java.awt.Dimension(60, 20));
        jLabelMines.setMinimumSize(new java.awt.Dimension(60, 20));
        jLabelMines.setOpaque(true);
        jLabelMines.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(30, 0, 0, 10);
        getContentPane().add(jLabelMines, gridBagConstraints);

        jLabelTimeIcon.setBackground(new java.awt.Color(230, 230, 230));
        jLabelTimeIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTimeIcon.setIcon(fileHandler.loadIcon("/clock.png")
        );
        jLabelTimeIcon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabelTimeIcon.setMaximumSize(new java.awt.Dimension(20, 20));
        jLabelTimeIcon.setMinimumSize(new java.awt.Dimension(20, 20));
        jLabelTimeIcon.setOpaque(true);
        jLabelTimeIcon.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(30, 70, 0, 0);
        getContentPane().add(jLabelTimeIcon, gridBagConstraints);

        jLabelMinesIcon.setBackground(new java.awt.Color(230, 230, 230));
        jLabelMinesIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMinesIcon.setIcon(fileHandler.loadIcon("/mine_small.png")
        );
        jLabelMinesIcon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabelMinesIcon.setMaximumSize(new java.awt.Dimension(20, 20));
        jLabelMinesIcon.setMinimumSize(new java.awt.Dimension(20, 20));
        jLabelMinesIcon.setOpaque(true);
        jLabelMinesIcon.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(30, 0, 0, 70);
        getContentPane().add(jLabelMinesIcon, gridBagConstraints);

        jMenu1.setText("New Game");

        jMenuItemEasy.setText("Easy (9 x 9)");
        jMenuItemEasy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEasyActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemEasy);

        jMenuItemMedium.setText("Medium (16 x 16)");
        jMenuItemMedium.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMediumActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemMedium);

        jMenuItemExpert.setText("Expert (30 x 16)");
        jMenuItemExpert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExpertActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemExpert);
        jMenu1.add(jSeparator1);

        jMenuItemExit.setText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemExit);

        jMenuBar1.add(jMenu1);

        jMenuInfo.setText("Info");

        jMenuItemHiscores.setText("High scores");
        jMenuItemHiscores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemHiscoresActionPerformed(evt);
            }
        });
        jMenuInfo.add(jMenuItemHiscores);

        jMenuItemRules.setText("Rules");
        jMenuItemRules.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRulesActionPerformed(evt);
            }
        });
        jMenuInfo.add(jMenuItemRules);

        jMenuAbout.setText("About");
        jMenuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAboutActionPerformed(evt);
            }
        });
        jMenuInfo.add(jMenuAbout);

        jMenuBar1.add(jMenuInfo);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemEasyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEasyActionPerformed
        // if difficulty changed, move window
        if (difficulty != "easy") {
            relocateWindow(difficulty, "easy");
        }
        difficulty = "easy";
        newGame(difficulty);
    }//GEN-LAST:event_jMenuItemEasyActionPerformed

    private void jMenuItemMediumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMediumActionPerformed
        // if difficulty changed, move window
        if (difficulty != "medium") {
            relocateWindow(difficulty, "medium");
        }
        difficulty = "medium";
        newGame(difficulty);
    }//GEN-LAST:event_jMenuItemMediumActionPerformed

    private void jMenuItemExpertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExpertActionPerformed
        // if difficulty changed, move window
        if (difficulty != "expert") {
            relocateWindow(difficulty, "expert");
        }
        difficulty = "expert";
        newGame(difficulty);
    }//GEN-LAST:event_jMenuItemExpertActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jButtonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopActionPerformed
        // stop button functionality
        // if game is running and not finished, lose game
        if (mineLogic.getWinState() == MineLogic.UNFINISHED) {
            mineLogic.setWinState(MineLogic.LOST);
            loseGame();

        // if game is either won or lost, start new game at same difficulty
        } else if (mineLogic.getWinState() == MineLogic.LOST || mineLogic.getWinState() == MineLogic.WON) {
            newGame(difficulty);
        }
    }//GEN-LAST:event_jButtonStopActionPerformed

    private void jMenuItemRulesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRulesActionPerformed
        // open rules window
        RulesFrame rules = new RulesFrame();
        rules.setLocationRelativeTo(this);
        rules.setVisible(true);
    }//GEN-LAST:event_jMenuItemRulesActionPerformed

    private void jMenuItemHiscoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHiscoresActionPerformed
        // check if highscore file exists. If not, prompt user to create new file
        if (!scoreHandler.fileExists()) {
            if (!scoreHandler.newFileConfirmation()) {
                return; // if new file not created, do nothing
            }
        }

        // display high score window
        HighScoreFrame score = new HighScoreFrame(scoreHandler);
        score.setLocationRelativeTo(this);
        score.setVisible(true);
    }//GEN-LAST:event_jMenuItemHiscoresActionPerformed

    private void jMenuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAboutActionPerformed
        // open about-info
        AboutFrame about = new AboutFrame();
        about.setLocationRelativeTo(this);
        about.setVisible(true);
    }//GEN-LAST:event_jMenuAboutActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MineSweeper.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MineSweeper.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MineSweeper.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MineSweeper.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                // check if "-test" added as launch parameter
                // if added, enable test mode (tile contents are displayed at all times during game)
                boolean test = false;
                for (String arg : args) {
                    if (arg.equals("-test")) {
                        test = true;
                    }
                }
                new MineSweeper(test).setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel gameboard;
    private javax.swing.JButton jButtonStop;
    private javax.swing.JLabel jLabelMines;
    private javax.swing.JLabel jLabelMinesIcon;
    private javax.swing.JLabel jLabelTimeIcon;
    private javax.swing.JLabel jLabelTimer;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuAbout;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuInfo;
    private javax.swing.JMenuItem jMenuItemEasy;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemExpert;
    private javax.swing.JMenuItem jMenuItemHiscores;
    private javax.swing.JMenuItem jMenuItemMedium;
    private javax.swing.JMenuItem jMenuItemRules;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    // End of variables declaration//GEN-END:variables

    private MineLogic mineLogic; // handles game logic
    private MineCoordinate[][] coordinateGrid; // grid of MineCoordinates that makes up the game area
    private MineTimer timer;
    private ScoreHandler scoreHandler; // keeps track of high scores
    private FileHandler fileHandler; // handles file I/O
    private String difficulty; // difficulty of current game
    private boolean testMode; // was the game launched in test mode?
}
