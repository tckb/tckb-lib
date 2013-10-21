/*
 * Copyright (C) 2013 tckb < Chandra [dot] Tungathurthi [at] rwth-aachen.de >
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
package com.tckb.samples;

import com.tckb.audio.NonTrivialAudio;
import com.tckb.audio.part.Label;
import com.tckb.audio.ui.AudioUI;
import com.tckb.audio.ui.display.AudioDisplay;
import com.tckb.util.Utility;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tckb
 */
public class AudioUIDemo extends javax.swing.JFrame {
    
    File audFile, audFile2;
    AudioUI myAudio = new AudioUI();
    AudioUI myAudio2 = new AudioUI();
    Label l;

    /**
     * Creates new form AudioUIDemo
     */
    public AudioUIDemo() {

        // Intialize the UI componenets
        initComponents();

        // Setup the optional UI components for UI interactivity
        // Initial UI setup
        myAudio.setDisplayContainer(myPanel);
        myAudio.setUIPlay(playAudio);
        myAudio.setUIPause(stopAudio);
        myAudio.setUISeeker(audSlider);
        myAudio.setChannelToRead(2);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        aboutFrame = new javax.swing.JFrame();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        audSlider = new javax.swing.JProgressBar();
        myPanel = new javax.swing.JScrollPane();
        filechooserBut = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        zoomStepUI = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        playAudio = new javax.swing.JButton();
        stopAudio = new javax.swing.JButton();
        currPos = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        jToggleButton3 = new javax.swing.JToggleButton();
        jButton8 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        label = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        time = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jToggleButton4 = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JSeparator();
        jButton9 = new javax.swing.JButton();
        pres = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jToggleButton2 = new javax.swing.JToggleButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        aboutFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        aboutFrame.setTitle("About");
        aboutFrame.setLocationByPlatform(true);
        aboutFrame.setResizable(false);

        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText(" jAudIO API");

        jLabel8.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("tckb");

        jLabel9.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(204, 0, 0));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("ver-0.1-alpha");

        jLabel10.setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("chandra.tungathurthi@rwth-aachen.de");

        org.jdesktop.layout.GroupLayout aboutFrameLayout = new org.jdesktop.layout.GroupLayout(aboutFrame.getContentPane());
        aboutFrame.getContentPane().setLayout(aboutFrameLayout);
        aboutFrameLayout.setHorizontalGroup(
            aboutFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(aboutFrameLayout.createSequentialGroup()
                .addContainerGap()
                .add(aboutFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        aboutFrameLayout.setVerticalGroup(
            aboutFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(aboutFrameLayout.createSequentialGroup()
                .add(39, 39, 39)
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel9)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel10)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        aboutFrame.setSize(500, 180);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("AudioUI Demo : ver0.1-alpha");
        setLocationByPlatform(true);

        myPanel.setDoubleBuffered(true);
        myPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                myPanelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                myPanelMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                myPanelMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                myPanelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                myPanelMouseEntered(evt);
            }
        });
        myPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                myPanelMouseDragged(evt);
            }
        });

        filechooserBut.setText("Load wav");
        filechooserBut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filechooserButActionPerformed(evt);
            }
        });

        jButton1.setText("Z+");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Z-");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        zoomStepUI.setText("1");

        jButton3.setText("<->");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel1.setText("sec");

        playAudio.setText("Play");

        stopAudio.setText("Pause");
        stopAudio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopAudioActionPerformed(evt);
            }
        });

        currPos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currPosActionPerformed(evt);
            }
        });

        jButton7.setText("Set cursor");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jToggleButton1.setText("Toggle Display");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jToggleButton3.setText("Cursor");
        jToggleButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton3ActionPerformed(evt);
            }
        });

        jButton8.setText("Refresh Display");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, audSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, myPanel)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(filechooserBut)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(playAudio)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(stopAudio)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 108, Short.MAX_VALUE)
                        .add(zoomStepUI, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jButton1)
                            .add(jButton2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jButton8)
                            .add(jToggleButton1))
                        .add(18, 18, 18)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(currPos, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jButton7))
                            .add(jToggleButton3))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(myPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                .add(18, 25, Short.MAX_VALUE)
                .add(audSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(zoomStepUI, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel1)))
                    .add(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jButton1)
                            .add(currPos, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jToggleButton1)
                            .add(jButton7)
                            .add(filechooserBut))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(playAudio)
                                .add(stopAudio))
                            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(jToggleButton3)
                                .add(jButton8)
                                .add(jButton2))))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(jButton3)))
                .addContainerGap())
        );

        jButton4.setText("Load Labels");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        label.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelActionPerformed(evt);
            }
        });

        jLabel2.setText("Label:");

        jLabel3.setText("Time:");

        jButton5.setText("Set");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel4.setText("sec");

        jButton6.setText("delete");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jToggleButton4.setText("Edit");
        jToggleButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton4ActionPerformed(evt);
            }
        });

        jButton9.setText("Export Labels");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        pres.setText("2");
        pres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                presActionPerformed(evt);
            }
        });

        jLabel5.setText("Precision :   #.");

        jLabel6.setText("digits");

        jButton10.setText("Update");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jToggleButton2.setText("Toggle Labels");
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });

        jMenu1.setText("About");

        jMenuItem1.setText("AudioUI Demo");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createSequentialGroup()
                        .add(jButton4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton9))
                    .add(layout.createSequentialGroup()
                        .add(jToggleButton4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jToggleButton2)))
                .add(54, 54, 54)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel3)
                        .add(18, 18, 18)
                        .add(time, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton6)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jButton10))
                    .add(layout.createSequentialGroup()
                        .add(jLabel2)
                        .add(18, 18, 18)
                        .add(label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabel5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pres, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel6)))
                .addContainerGap(136, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 739, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(56, 56, 56))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(3, 3, 3)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton4)
                    .add(label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton5)
                    .add(jButton9)
                    .add(pres, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jToggleButton4)
                    .add(jLabel3)
                    .add(time, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4)
                    .add(jButton6)
                    .add(jButton10)
                    .add(jToggleButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @SuppressWarnings("empty-statement")
    private void filechooserButActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filechooserButActionPerformed
        try {
            // Get the file
            audFile = Utility.UI.getFile(myPanel, Utility.wavFileFilter);
            // Attach the file
            myAudio.setAudioFile(audFile);
            // Suggest the type of display desired -- MUST call!
            // Must be called after the intial UI setup
            myAudio.setContainerDisplay(AudioDisplay.TYPE.WAVEFORM);

            // setup the display 
            myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).setCrosshairLen(10);
            myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).setZoomLevel(myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).getMinZoom());
            myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).showCursor(false);
            // myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).setDisplayInfo("Audio UI Demo - ver0.1-alpha");
        } catch (NonTrivialAudio.InvalidChannnelException ex) {
            Logger.getLogger(AudioUIDemo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_filechooserButActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).setZoomStep(Integer.parseInt(zoomStepUI.getText()));
        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).zoomIn();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).setZoomStep(Integer.parseInt(zoomStepUI.getText()));
        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).zoomOut();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).resetZoom();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        File textFile = Utility.UI.getFile(jPanel1, Utility.txtFileFilter);
        String[] words = Utility.getWordsInFile(textFile);
        int l = 0;
        while (l < words.length - 1) {
            String lbl = words[l + 1];
            Double t = Double.parseDouble(words[l]);
            myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).setLabelAt(lbl, t);
            l += 2;
        }
        
        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).refreshDisplay();

    }//GEN-LAST:event_jButton4ActionPerformed

    private void labelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_labelActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (!label.getText().isEmpty() && !time.getText().isEmpty()) {
            myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).setLabelAt(label.getText(), Double.parseDouble(time.getText()));
            myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).refreshDisplay();
            
        }
        

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (!time.getText().isEmpty()) {
            myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).deleteLabelAt(Double.parseDouble(time.getText()));
            myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).refreshDisplay();
            
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        if (!currPos.getText().isEmpty()) {
            myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).setCursorPos(Double.parseDouble(currPos.getText()));
            myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).refreshDisplay();
            
        }
        

    }//GEN-LAST:event_jButton7ActionPerformed

    private void stopAudioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopAudioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stopAudioActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).toggleDisplay();
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
        System.out.println(myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).toggleLabels());

    }//GEN-LAST:event_jToggleButton2ActionPerformed

    private void myPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_myPanelMouseClicked
        
        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).showCursorAt(evt.getX(), evt.getY());
    }//GEN-LAST:event_myPanelMouseClicked

    private void myPanelMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_myPanelMouseDragged
        
        Label l = myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).getLabelAtXY(evt.getX(), evt.getY());
        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).showCursorAt(evt.getX(), evt.getY());
        
        if (l != null) {
            myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).setLabelAtXY(l, evt.getX(), evt.getY());
        }

    }//GEN-LAST:event_myPanelMouseDragged

    private void myPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_myPanelMousePressed
        l = myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).getLabelAtXY(evt.getX(), evt.getY());
//        System.out.println(l.getText());
    }//GEN-LAST:event_myPanelMousePressed

    private void myPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_myPanelMouseReleased
    }//GEN-LAST:event_myPanelMouseReleased

    private void myPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_myPanelMouseEntered
//        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).showCursor(true);
    }//GEN-LAST:event_myPanelMouseEntered

    private void myPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_myPanelMouseExited
//        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).showCursor(false);
    }//GEN-LAST:event_myPanelMouseExited

    private void jToggleButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton3ActionPerformed
        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).showCursor(jToggleButton3.isSelected());
    }//GEN-LAST:event_jToggleButton3ActionPerformed

    private void jToggleButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton4ActionPerformed
        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).editLabels(jToggleButton4.isSelected());
    }//GEN-LAST:event_jToggleButton4ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).refreshDisplay();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed

        // Retrieve all the labels
        ArrayList<Label> labelData = myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).getAllLabels();
        StringBuilder labels = new StringBuilder();
        labels.append("#####################").append(Utility.LINE_BREAK);
        labels.append("# Label / TimeStamp(sec)").append(Utility.LINE_BREAK);
        labels.append("######################").append(Utility.LINE_BREAK);
        for (Label l : labelData) {
            labels.append(l.getTitle()).append(" / ").append(myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).getLabelTimeStamp(l.getPosAsSample())).append(Utility.LINE_BREAK);
        }
        
        labels.append("#####################").append(Utility.LINE_BREAK);
        labels.append("# Labels exported @ ").append(Calendar.getInstance().getTime());
        labels.append("# by AudioUIDemo ");
        
        Utility.saveStringToFile(labels.toString(), Utility.UI.saveFile(myPanel));
        Utility.UI.showInfoMessage(myPanel, "Labels Exported!");
        

    }//GEN-LAST:event_jButton9ActionPerformed

    private void presActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_presActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_presActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).setTimePrecision(Integer.parseInt(pres.getText()));
        myAudio.getDisplay(AudioDisplay.TYPE.WAVEFORM).refreshDisplay();

    }//GEN-LAST:event_jButton10ActionPerformed

    private void currPosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currPosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_currPosActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        aboutFrame.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*  
         * Set the Nimbus look and feel
         */
        //        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /*
//         * If Nimbus (introduced in Java SE 6) is not available, stay with the
//         * default look and feel. For details see
//         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(AudioUIDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(AudioUIDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(AudioUIDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(AudioUIDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>

        Logger.getLogger("com.tckb").setLevel(Level.INFO);


        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AudioUIDemo().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFrame aboutFrame;
    private javax.swing.JProgressBar audSlider;
    private javax.swing.JTextField currPos;
    private javax.swing.JButton filechooserBut;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    private javax.swing.JToggleButton jToggleButton4;
    private javax.swing.JTextField label;
    private javax.swing.JScrollPane myPanel;
    private javax.swing.JButton playAudio;
    private javax.swing.JTextField pres;
    private javax.swing.JButton stopAudio;
    private javax.swing.JTextField time;
    private javax.swing.JTextField zoomStepUI;
    // End of variables declaration//GEN-END:variables
}
