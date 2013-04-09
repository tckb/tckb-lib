/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.audio;

import com.tckb.audio.NonTrivialAudio.InvalidChannnelException;
import com.tckb.audio.part.Block;
import com.tckb.audio.part.Block.Reduction;
import com.tckb.audio.ui.display.AudioDisplay;
import com.tckb.audio.ui.display.wave.WaveDisplay;
import com.tckb.audio.ui.display.wave.WvParams;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

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
    private Block[] cachedData;
    private NonTrivialAudio audio;
    private WvParams wvParams;
    private int channel;
    private double globalMax;

    private AudProcessor(NonTrivialAudio audio, int ch) throws InvalidChannnelException {
        this.audio = audio;
        wvParams = new WvParams();
        channel = ch;
        cachedData = processAudio();

    }

    public static AudProcessor createProcessor(NonTrivialAudio audio, int ch) throws InvalidChannnelException {

        return new AudProcessor(audio, ch);

    }

    // TODO: Clean this mess up!
    // this assumes that the size of each sample is 16 bits
    public final Block[] processAudio() throws InvalidChannnelException {//, int displayWidth) {

        // Independent Variables: Constants
        double[] origDataSamples = audio.getAudioData_fast(channel); // get the first channel
        System.out.println("Data read!");
        DenseMatrix64F audioData = new DenseMatrix64F(1, origDataSamples.length);
        for (int j = 0; j < origDataSamples.length; j++) {
            audioData.set(0, j, origDataSamples[j]);
        }

//        globalMax = CommonOps.elementMax(audioData);

        globalMax = 1;

//        CommonOps.divide(CommonOps.elementMax(audioData), audioData);


        wvParams.SAMPLE_COUNT = origDataSamples.length;
        calWvParams();

        Block bList[] = new Block[wvParams.BLOCK_COUNT]; // 0 -> blockcount-1
//        Block emptyBlock;
        int bCnt = 0;
        Reduction cRed;

        for (int s = 0; bCnt < wvParams.BLOCK_COUNT; s += WvParams.BLOCK_16K_SAMPLE) {
            mylogger.log(Level.FINE, "Filling block:{0}", bCnt);

            int sampleCount = (bCnt < wvParams.BLOCK_COUNT - 1) ? WvParams.BLOCK_16K_SAMPLE : lastBlockRedCount * WvParams.RED_SIZE_SAMPLE;
            mylogger.log(Level.FINE, "Sample count: {0}", sampleCount);

//            emptyBlock = new Block(sampleCount);

            for (int k = 0; k < sampleCount; k += WvParams.RED_SIZE_SAMPLE) {
                //     mylogger.info(sampleCount%RED_SIZE_SAMPLE);

//                cRed = computeReduction(normAudData, s + k, WvParams.RED_SIZE_SAMPLE);
                cRed = computeReduction(audioData, s + k, WvParams.RED_SIZE_SAMPLE);
                wvParams.wavData.add(cRed);
//
//                if (!emptyBlock.put(cRed)) {
//                    mylogger.warning("Cann't add anymore!");
//                    break;
//                }
            }
//           bList[bCnt++] = emptyBlock;
            bCnt++;

        }
        origDataSamples = null;
        // Now, request for garbage collection
        System.gc();

        return bList;

    }
// TODO: Deprecated method delete

    private Reduction computeReduction(double[] origDataSamples, int pos, int rSize) {
        ArrayList<Double> data = new ArrayList<Double>();

        for (int j = 0; j < rSize; j++) {

            double value = (double) origDataSamples[pos + j];

            data.add(value);
        }

        double max = Collections.max(data);
        double min = Collections.min(data);

        return new Reduction(min, max);

    }

    private Reduction computeReduction(DenseMatrix64F rowData, int colS, int size) {

        DenseMatrix64F extract = CommonOps.extract(rowData, 0, 1, colS, colS + size);
        Double max = CommonOps.elementMax(extract) / globalMax;
        Double min = CommonOps.elementMin(extract) / globalMax;

        return new Reduction(min, max);

    }

    private void calWvParams() {

        srate = audio.getSampleRate();
        wvParams.SRATE = srate;

        lastBlockSampleCount = wvParams.SAMPLE_COUNT % WvParams.BLOCK_16K_SAMPLE;
        lastBlockRedCount = lastBlockSampleCount / WvParams.RED_SIZE_SAMPLE; // discard the remaining samples!
        fullBlocks = wvParams.SAMPLE_COUNT / WvParams.BLOCK_16K_SAMPLE;
        wvParams.BLOCK_COUNT = (lastBlockSampleCount != 0) ? (fullBlocks + 1) : fullBlocks;
        wvParams.ADJ_SAMPLE_COUNT = (fullBlocks * WvParams.BLOCK_16K_SAMPLE) + lastBlockRedCount * WvParams.RED_SIZE_SAMPLE; // adjusting the sample count
        wvParams.DUR_MS = audio.getDurationInMS();
        wvParams.DUR_SEC = audio.getDurationInSeconds();
        wvParams.RED_COUNT = fullBlocks * (WvParams.BLOCK_16K_SAMPLE / WvParams.RED_SIZE_SAMPLE) + lastBlockRedCount;




        mylogger.log(Level.FINE, "Sample count={0}", wvParams.SAMPLE_COUNT);
        mylogger.log(Level.FINE, "Last block sample count={0}", lastBlockSampleCount);
        mylogger.log(Level.FINE, "Number of full blocks={0}", fullBlocks);
        mylogger.log(Level.FINE, "Last block red count={0}", lastBlockRedCount);
        mylogger.log(Level.FINE, "No of blocks : {0}", wvParams.BLOCK_COUNT);
    }

    //TODO: Fix this!
    public AudioDisplay getWavePanel() {
        WaveDisplay wavePanel = new WaveDisplay(cachedData, wvParams);
        wavePanel.setZoomLevel(wavePanel.getMIN_ZOOM());
        return wavePanel;
    }
}
//        Dataset data2 = new DefaultDataset();
//        for (int j = 0; j < rSize; j++) {
//
//            double[] value = new double[]{(double) origDataSamples[pos + j]};
//            Instance instance1 = new DenseInstance(value);
//            data2.add(instance1);
//            // mylogger.info(data2);
//        }
//               Normalize the samples: Performance overhead?
//            NormalizeMidrange nmr = new NormalizeMidrange(0,4);
//            nmr.filter(data2);
//
//        Instance maxI = DatasetTools.maxAttributes(data2);
//        Instance minI = DatasetTools.minAttributes(data2);
//        Vector2d normRed = new Vector2d(min, max);
//        normRed.normalize();
//        // System.out.println(normRed.x+":"+normRed.y);
//        return new Reduction(normRed.x, normRed.y);
//        return new Reduction(minI.value(0), maxI.value(0));
