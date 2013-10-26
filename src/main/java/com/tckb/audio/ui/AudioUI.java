/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.audio.ui;

import com.tckb.audio.AudProcessor;
import com.tckb.audio.NonTrivialAudio;
import com.tckb.audio.ui.display.AudioDisplay;
import com.tckb.audio.ui.display.AudioDisplay.TYPE;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.Timer;

/**
 *
 * @author tckb
 */
public class AudioUI extends Observable {

    private static final Logger mylogger = Logger.getLogger("com.tckb.audio.ui");
    private SourceObserver defaultObserver = null;
    private boolean manualSeek = false;
    private int targetChannel = 1;

    public static enum UIComponent {

        PLAY, PAUSE, STOP, SEEKER, CONTAINER;
    }

    public AudioUI() {
        defaultObserver = new SourceObserver();
        addObserver(defaultObserver);
    }

    public boolean setAudioFile(File src) {

        if (src.exists()) {
            setChanged();
            this.notifyObservers(src);
            return true;
        } else {
            mylogger.log(Level.SEVERE, "Error: Can not set set file; {0} does not exist! ", src.getAbsolutePath());
            return false;
        }

    }

    public void attachUIComponent(UIComponent typeUIComponent, JComponent component) {
        switch (typeUIComponent) {
            case PLAY:

                if (component instanceof JButton) {
                    setUIPlay((JButton) component);
                } else {
                    mylogger.severe("Invalid component! expecting JButton; setting PLAY button failed!");
                }

                break;
            case PAUSE:

                if (component instanceof JButton) {
                    setUIPause((JButton) component);
                } else {
                    mylogger.severe("Invalid component! expecting JButton; setting PAUSE button failed!");
                }
                break;
            case STOP:

                throw new UnsupportedOperationException("Not implemented!");

//                if (component instanceof JButton) {
//                    setUIStop((JButton) component);
//                } else {
//                    mylogger.severe("Invalid component! expecting JButton; setting STOP button failed!");
//                }
//                break;
            case SEEKER:

                if (component instanceof JSlider) {
                    setUISeeker((JSlider) component);
                }
                if (component instanceof JProgressBar) {
                    setUISeeker((JProgressBar) component);
                } else {
                    mylogger.severe("Invalid component! expecting JSlider / JProgressBar; setting SEEKER  failed!");
                }
                break;
            case CONTAINER:
                if (component instanceof JScrollPane) {

                    setDisplayContainer((JScrollPane) component);
                } else {
                    mylogger.severe("Invalid component! expecting JScrollPane; setting CONTAINER failed!");
                }
                break;

        }

    }

    // experimental! doesnt work
    private void setVolume(float level) {
        defaultObserver.setVolLevel(level);
    }

    public boolean toggleMute() {
        return defaultObserver.toggleSound();
    }

    public void setAudioMute(boolean state) {
       defaultObserver.setAudioMute(state);
    }

    public boolean isMuted(){
        return defaultObserver.getMuteStatus();
    }
    
    public void setAutoPlay(boolean b) {
        defaultObserver.setAutoPlay(b);
    }

    /**
     * @deprecated Use attachUIComponent
     *
     * @param aud_pause_but
     */
    public void setUIPause(JButton aud_pause_but) {

        defaultObserver.addUIPause(aud_pause_but);
    }

    /**
     * @param aud_play_but
     * @deprecated Use attachUIComponent
     */
    public void setUIPlay(JButton aud_play_but) {

        defaultObserver.addUIPlay(aud_play_but);
    }

    /**
     * @param aud_seeker_slid
     * @deprecated Use attachUIComponent
     */
    public void setUISeeker(JSlider aud_seeker_slid) {

        defaultObserver.addSeeker(aud_seeker_slid);
    }

    /**
     * @param aud_seeker_slid
     * @deprecated Use attachUIComponent
     */
    public void setUISeeker(JProgressBar aud_seeker_slid) {
        defaultObserver.addSeeker(aud_seeker_slid);
    }

    /**
     * @param aContainer
     * @deprecated Use attachUIComponent
     */
    public void setDisplayContainer(JScrollPane aContainer) {
        defaultObserver.addContainer(aContainer);
    }

    public boolean getStatusOK() {
        return this.defaultObserver.getStatus();
    }

    public AudioDisplay getDisplay(TYPE type) {
        return defaultObserver.getDisplay(type);

    }

    public void setContainerDisplay(TYPE type) throws NonTrivialAudio.InvalidChannnelException {
        defaultObserver.setContainerDisplay(type);

    }

    public void setChannelToRead(int channel) {
        this.targetChannel = channel;
    }

    private class SourceObserver implements Observer {

        private JSlider seeker;
        private boolean autoPlay = false;
        private boolean statusOK;
        private JProgressBar seeker2;
        private NonTrivialAudio audio = null;
        private JScrollPane displayContainer = null;
        private AudioDisplay spectPanel = null;
        private ArrayList<Double> timeStamps = null;
        private boolean audioPlaying = false;
        private AudProcessor aProcesor = null;
        // parameters of audio
        private Double audLenMS = 0.0;
        private Timer playTimer;
        private boolean audioPanelLinked = true;
        private AudioDisplay wavPanel = null;

        @Override
        public void update(Observable o, Object audioFile) {

            mylogger.info("Sound source changed, Adjusting the controls");

            if (audioFile instanceof File) {
                try {
                    // stop any playing audio 
                    this.stopCurrentPlay();

                    audio = new NonTrivialAudio((File) audioFile);
                    audLenMS = audio.getDurationInMS();
                    aProcesor = AudProcessor.createProcessor(audio, targetChannel);

                    // Reset the seeker if, defined
                    resetSeekersMS(audLenMS);

                    mylogger.log(Level.INFO, "Sound Clip duration: {0} ", com.tckb.util.Utility.toFormatedTimeString(audLenMS.intValue()));

                    if (autoPlay) {
                        mylogger.fine("Playing audio");

                        startPlaying();
                    }

                    playTimer = new Timer(0, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            startThePlay();
                        }
                    });
                    playTimer.setDelay(1);
                    playTimer.start();
                    playTimer.setRepeats(true);

                    this.statusOK = true;
                } catch (Exception ex) {
                    mylogger.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }

//catch (IOException ex) {
////                    mylogger.log(Level.SEVERE, null, ex);
////                } catch (UnsupportedAudioFileException ex) {
////                    mylogger.log(Level.SEVERE, null, ex);
////                } catch (LineUnavailableException ex) {
////                    mylogger.log(Level.SEVERE, null, ex);
////                }
//
//            } else {
//                mylogger.warning("Source not an instance of file");
//            }
        }

        private void addUIPlay(JButton aud_play_but) {
            aud_play_but.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {

                    if (statusOK) {
                        startPlaying();
                    }
                }
            });

        }

        private void addUIPause(JButton aud_pause_but) {

            aud_pause_but.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    pauseCurrentPlay();
                }
            });

        }

        private void addSeeker(JSlider aud_seeker_slid) {
            aud_seeker_slid.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // when mouse pres you know his intensions is to seekSecond so, enable manual seekSecond!
                    manualSeek = true; // this will pause the seekSecond updater!
                    // pauseCurrentPlay();

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    int currPos = ((JSlider) e.getSource()).getValue();
                    mylogger.log(Level.FINE, "Mouse released: Current Position: {0}", currPos);
                    seekPlay(currPos);
                    manualSeek = false;

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });

            this.seeker = aud_seeker_slid;
        }

        private void addSeeker(JProgressBar aud_seeker_slid) {
            this.seeker2 = aud_seeker_slid;

        }

        private void addContainer(JScrollPane aContainer) {
            this.displayContainer = aContainer;
        }

        private void pauseCurrentPlay() {

            if (audio != null) {
                audioPlaying = false;
                audio.pause();
            }

        }

        private void stopCurrentPlay() {

            if (audio != null) {
                audio.stop();
                audioPlaying = false;
            }

        }

        private void seekPlay(double currPos) {
            if (audio != null) {
                audio.seekMS(currPos); // audio seek automatically adjusts the play
            }

        }

        private void startPlaying() {

            audioPlaying = true;
            // play the audio
            audio.start();

            new Thread() {
                int tsCnt = -1;
                //TODO: This assumes that the timestamps start from 0.0!
                double currTS = 0.0;

                @Override
                public void run() {
                    // Empty the container first
                    //trContainer.setText("");

                    while (audio != null && audioPlaying) {
                        try {
                            // update only when there is no manaul override
                            if (!manualSeek) {
                                // System.out.println("update thread: ..good to go!");
                                updateSeekerMS(audio.getCurrentMS());

                            } else {
                                // System.out.println("update thread: I hate manual interactions! pausing...");
                            }
                            //TODO: CPU usuage!!
                            Thread.sleep(1);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AudioUI.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }
            }.start();

        }

        private void startThePlay() {
//            System.out.println("Start the play");
            if (audio != null && audioPlaying) {
                updateSeekerMS(audio.getCurrentMS());
            }

        }

        private void setAutoPlay(boolean b) {
            this.autoPlay = b;
        }

        private boolean getStatus() {
            return this.statusOK;
        }

        private void updateSeekerMS(double val) {
            // System.out.println("am updating");
            if (seeker != null) {
                seeker.setValue((int) val);

            }
            if (seeker2 != null) {
                seeker2.setValue((int) val);
                seeker2.setString(new Integer(seeker2.getValue()).toString() + " / " + seeker2.getMaximum() + " ms");
                seeker2.setToolTipText(null);
                seeker2.setBorderPainted(true);
                seeker2.setStringPainted(true);
            }
            // link for wavPanel
            if (audioPanelLinked) {
                if (audioPlaying) {
                    wavPanel.updateCrosshairPosition(val / 1000);
                } else {
                    wavPanel.updateCrosshairPosition(0);

                }
            }

            // link for specpanel
            // link to pther panel
        }

        private void resetSeekersMS(double duration) {
            // Adjust the seeker
            if (seeker != null) {
                seeker.setValue(0);
                seeker.setMinimum(0);
                seeker.setMinorTickSpacing(10 * 1000);  // 10 secs
                seeker.setMajorTickSpacing(60 * 1000); // 60 secs
                seeker.setPaintTicks(true);
                seeker.setMaximum((int) duration);
            }

            if (seeker2 != null) {
                seeker2.setValue(0);
                seeker2.updateUI();
                seeker2.setMinimum(0);
                seeker2.setMaximum((int) duration);
                seeker2.setString((int) duration + " ms");
                seeker2.setStringPainted(true);
            }
        }

        private AudioDisplay getDisplay(AudioDisplay.TYPE type) {
            switch (type) {
                case WAVEFORM:
                    if (wavPanel != null) {
                        return wavPanel;
                    } else {
                        mylogger.severe("WavPanel not initialized! container empty ");
                        return null;
                    }
                case SPECTROGRAM:
                    throw new UnsupportedOperationException("Spectrogram display not yet supported");
//                    return spectPanel;
                default:
                    throw new IllegalAccessError("Display type not available");
            }

        }

        /**
         * creates the TYPE panel
         *
         * @param type
         * @throws com.tckb.audio.NonTrivialAudio.InvalidChannnelException
         */
        private void setContainerDisplay(TYPE type) throws NonTrivialAudio.InvalidChannnelException {
            switch (type) {
                case WAVEFORM:
                    wavPanel = aProcesor.getWavePanel();
                    displayContainer.setViewportView(wavPanel);
                    break;
                case SPECTROGRAM:
                    throw new UnsupportedOperationException("Spectrogram display not yet supported");
//                    displayContainer.setViewportView(specPanel);
                default:
                    throw new IllegalAccessError("Display type not available");

            }
        }

        private boolean toggleSound() {
            if (audio.isMute()) {
                audio.setMute(false);
                return false;
            } else {
                audio.setMute(true);
                return true;
            }

        }

        private void setAudioMute(boolean state) {
            audio.setMute(state);

        }

        private void setVolLevel(float level) {
            audio.setVolumeLevel(level);
        }

        private boolean getMuteStatus() {
            return audio.isMute();
        }
    }
}
