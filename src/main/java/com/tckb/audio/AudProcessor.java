/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.audio;

import com.tckb.audio.NonTrivialAudio.InvalidChannnelException;
import com.tckb.audio.part.Block;
import com.tckb.audio.part.Block.Reduction;
import com.tckb.audio.ui.display.wave.WaveDisplay;
import com.tckb.audio.ui.display.wave.WvParams;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tckb
 */
public class AudProcessor {

    private static final Logger mylogger = Logger.getLogger("com.tckb.audio");
    private int srate;
    private int lastBlockSampleCount;
    private int lastBlockRedCount;
    private int fullBlocks;
    private final NonTrivialAudio audio;
    private final int channel;
    private static int renderFactor = 1;
    private WvParams wvParams;

    private AudProcessor(NonTrivialAudio audio, int ch) throws InvalidChannnelException {
        this.audio = audio;
        channel = ch;

    }

    public static AudProcessor createProcessor(NonTrivialAudio audio, int ch) throws InvalidChannnelException {

        double duration = audio.getDurationInMS();

// Adjust the rendering factor depending on the length of the audio
        if (duration < 15000) {
            setRenderFactor(2);

        }
        if (duration < 5000) {
            setRenderFactor(8);

        }
        if (duration < 2000) {
            setRenderFactor(16);

        }

        return new AudProcessor(audio, ch);
    }

    /**
     * @throws com.tckb.audio.NonTrivialAudio.InvalidChannnelException
     */
    public final void calcWvParams() throws InvalidChannnelException {

        Double[] origDataSamples;

        double dur_secs = audio.getDurationInSeconds();
        
        if (dur_secs >= (8 * 60)) {
             mylogger.info("Duration exceeds the limit; using mulitcore data reader; expect high CPU usage!");
            origDataSamples = audio.getAudioNormData_multicore(channel, (int) Math.round(dur_secs * 0.1));

        } else {
             mylogger.info("Duration in the limit; using single core data reader");
            origDataSamples = audio.getAudioNormData(channel); // get the first channel
        }

        mylogger.log(Level.INFO, "Audio Data read complete: Channel: {0}", channel);
        wvParams.SAMPLE_COUNT = origDataSamples.length;
        initWvParams();
        // TODO: remove usuage bList!
        Block bList[] = new Block[wvParams.BLOCK_COUNT];
        int bCnt = 0;

        for (int s = 0; bCnt < wvParams.BLOCK_COUNT; s += WvParams.BLOCK_16K_SAMPLE) {
            mylogger.log(Level.FINE, "Filling block:{0}", bCnt);

            int sampleCount = (bCnt < wvParams.BLOCK_COUNT - 1) ? WvParams.BLOCK_16K_SAMPLE : lastBlockRedCount * wvParams.RED_SIZE_SAMPLE;
            mylogger.log(Level.FINE, "Sample count: {0}", sampleCount);

            for (int k = 0; k < sampleCount; k += wvParams.RED_SIZE_SAMPLE) {

                Double[] rawReductionData = Arrays.copyOfRange(origDataSamples, s + k, (s + k) + wvParams.RED_SIZE_SAMPLE);

                if (rawReductionData.length <= 0) {
                    break;
                }

                Arrays.sort(rawReductionData);
                double min = rawReductionData[0];
                double max = rawReductionData[rawReductionData.length - 1];

                wvParams.wavData.add(new Reduction(min, max));

            }
            bCnt++;

        }

    }

    private void initWvParams() {
        srate = audio.getSampleRate();
        wvParams.SRATE = srate;
        lastBlockSampleCount = wvParams.SAMPLE_COUNT % WvParams.BLOCK_16K_SAMPLE;
        lastBlockRedCount = lastBlockSampleCount / wvParams.RED_SIZE_SAMPLE; // discard the remaining samples!
        fullBlocks = wvParams.SAMPLE_COUNT / WvParams.BLOCK_16K_SAMPLE;
        wvParams.BLOCK_COUNT = (lastBlockSampleCount != 0) ? (fullBlocks + 1) : fullBlocks;
        wvParams.ADJ_SAMPLE_COUNT = (fullBlocks * wvParams.BLOCK_16K_SAMPLE) + lastBlockRedCount * wvParams.RED_SIZE_SAMPLE; // adjusting the sample count
        wvParams.DUR_MS = audio.getDurationInMS();
        wvParams.DUR_SEC = audio.getDurationInSeconds();
        wvParams.RED_COUNT = fullBlocks * (WvParams.BLOCK_16K_SAMPLE / wvParams.RED_SIZE_SAMPLE) + lastBlockRedCount;

        mylogger.log(Level.FINE, "Sample count={0}", wvParams.SAMPLE_COUNT);
        mylogger.log(Level.FINE, "Last block sample count={0}", lastBlockSampleCount);
        mylogger.log(Level.FINE, "Number of full blocks={0}", fullBlocks);
        mylogger.log(Level.FINE, "Last block red count={0}", lastBlockRedCount);
        mylogger.log(Level.FINE, "No of blocks : {0}", wvParams.BLOCK_COUNT);

    }

    public WaveDisplay getWavePanel() throws InvalidChannnelException {
        this.wvParams = new WvParams();

        int redSize = (wvParams.RED_SIZE_SAMPLE) / renderFactor;

        if (redSize < WvParams.SAMPLE_SIZE) {
            redSize = WvParams.SAMPLE_SIZE;
        }
        wvParams.RED_SIZE_SAMPLE = redSize;
        calcWvParams();

        WaveDisplay wavePanel = new WaveDisplay(this.wvParams);
        wavePanel.setZoomLevel(wavePanel.getMinZoom());
        wavePanel.setDisplayInfo(audio.getFile().getName()+" / Channel: "+channel);
        return wavePanel;
    }

    protected static void setRenderFactor(int factor) {
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
// Experimental call for check
//        Double[] origDataSamples_fast = audio.getAudioData_fast2(channel); // get the first channel
//        System.out.println("faster data length: "+origDataSamples_fast.length);
//        Double[] origDataSamples_list = new Double[origDataSamples.length];
//        System.out.println("original data length: "+origDataSamples.length);
//
//        
//        boolean res = Arrays.deepEquals(origDataSamples_fast, Arrays.asList(origDataSamples).toArray(origDataSamples_list));
//        System.out.println("res: " + res);
// --END-
