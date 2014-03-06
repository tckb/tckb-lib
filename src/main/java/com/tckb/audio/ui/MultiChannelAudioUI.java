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
package com.tckb.audio.ui;

import com.tckb.audio.core.NonTrivialAudio;
import com.tckb.audio.ui.display.AudioDisplay;
import com.tckb.audio.ui.display.AudioDisplay.TYPE;
import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.JSlider;

/**
 *
 * @author tckb < Chandra [dot] Tungathurthi [at] rwth-aachen.de >
 */
public class MultiChannelAudioUI extends Observable {

    private final HashMap<AudioUI, TYPE> trackList = new HashMap<AudioUI, TYPE>();
    private static final Logger mylogger = Logger.getLogger("com.tckb.audio.ui");
    private JButton globalPlay, globalPause, globalStop;
    private JComponent globalSeeker;

    public MultiChannelAudioUI() {

        addObserver(new AudioSourceObserver());

    }

    public static enum UIComponent {

        PLAY, STOP, PAUSE, SEEKER;
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

    /**
     * Must be called after adding the UI Components
     *
     * @param track
     * @param type
     */
    public void addTrack(AudioUI track, AudioDisplay.TYPE type) {
        track.attachUIComponent(AudioUI.UIComponent.PLAY, globalPlay);
        track.attachUIComponent(AudioUI.UIComponent.PAUSE, globalPause);
        track.attachUIComponent(AudioUI.UIComponent.SEEKER, globalSeeker);
        track.setAutoPlay(false);
        trackList.put(track, type);

    }

    public void attachUIComponent(UIComponent typeUIComponent, JComponent component) {
        switch (typeUIComponent) {
            case PLAY:

                if (component instanceof JButton) {
                    globalPlay = (JButton) component;
                } else {
                    mylogger.severe("Invalid component! expecting JButton; setting PLAY button failed!");
                }

                break;
            case PAUSE:

                if (component instanceof JButton) {
                    globalPause = (JButton) component;
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
                    globalSeeker = (JSlider) component;

                }
                if (component instanceof JProgressBar) {
                    globalSeeker = (JProgressBar) component;
                } else {
                    mylogger.severe("Invalid component! expecting JSlider / JProgressBar; setting SEEKER  failed!");
                }
                break;

        }

    }

    private class AudioSourceObserver implements Observer {

        @Override
        public void update(Observable o, Object audioFile) {
            if (audioFile instanceof File) {
                for (Entry e : trackList.entrySet()) {
                    try {
                        ((AudioUI) e.getKey()).setAudioFile((File) audioFile);
                        ((AudioUI) e.getKey()).setContainerDisplay(((AudioDisplay.TYPE) e.getValue()));
                    } catch (NonTrivialAudio.InvalidChannnelException ex) {
                        mylogger.log(Level.SEVERE, "Invalid Channel!", ex);
                    }
                }
            }

        }

    }

}
