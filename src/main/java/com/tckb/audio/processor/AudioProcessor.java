/*
 * Copyright (C) 2014 tckb < Chandra [dot] Tungathurthi [at] rwth-aachen.de >
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
package com.tckb.audio.processor;

import com.tckb.audio.core.NonTrivialAudio;
import com.tckb.audio.ui.display.AudioDisplay;
import com.tckb.audio.ui.display.DisplayParams;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tckb < Chandra [dot] Tungathurthi [at] rwth-aachen.de >
 */
public abstract class AudioProcessor {

    protected final Logger mylogger = Logger.getLogger("com.tckb.audio");
    protected NonTrivialAudio source;
    protected int srate;
    protected int channel;
    protected int renderFactor = 1;
    protected DisplayParams params;
    protected Double[] origDataSamples;

    public AudioProcessor(NonTrivialAudio audio, int ch) throws NonTrivialAudio.InvalidChannnelException {
        source = audio;
        channel = ch;
        srate = audio.getSampleRate();
        double dur_secs = audio.getDurationInSeconds();

        if (dur_secs >= (8 * 60)) {
            mylogger.info("Duration exceeds the limit; using mulitcore data reader; expect high CPU usage!");
            origDataSamples = audio.getAudioNormData_multicore(channel, (int) Math.round(dur_secs * 0.1));

        } else {
            mylogger.info("Duration in the limit; using single core data reader");
            origDataSamples = audio.getAudioNormData(channel); // get the first channel
        }

        mylogger.log(Level.INFO, "Audio Data read complete: Channel: {0}", channel);

    }

    public static AudioProcessor createProcessor(AudioDisplay.TYPE typeOfDisplay, NonTrivialAudio audio, int ch) throws NonTrivialAudio.InvalidChannnelException {
        switch (typeOfDisplay) {
            case WAVEFORM:
                return new WaveProcessor(audio, ch);
            case SPECTROGRAM:
                return new SpectProcessor(audio, ch);
            default:
                throw new IllegalArgumentException("Unknown Display type");
        }

    }

    public abstract AudioDisplay getPanel();

    public void setRenderFactor(int factor) {
        renderFactor = factor;
    }

    /**
     * Increase render quality at the cost of performance
     */
    public void rQualityIN() {
        renderFactor *= 2;

    }

    public void rQualityOUT() {
        renderFactor /= 2;

    }

}
