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
package com.tckb.audio;

/**
 *
 * @author tckb < Chandra [dot] Tungathurthi [at] rwth-aachen.de >
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tckb
 */
public final class MultiCoreAudioDataReader extends RecursiveTask<SortedMap<Integer, Double[]>> {

    private int CHUNK_THRESHOLD; // global constants 
    private final ByteBuffer buffer; // audio buffer
    private final int frameSize;
    private final int chToRead;
    private final int noOfCh;
    private int totalFramesInBuffer;
    private int startByte_frame, endByte_frame;
    private static final Logger mylogger = Logger.getLogger("com.tckb.audio");
    private final SortedMap<Integer, Double[]> chunkData;
    private final int srate;
    private final int byte_per_sample;
    private int midByte_frame;
    private double totalDurationOfbuffer;  // in secs

    /**
     * SortedMap must be synchronized externally! Use Collection framework
     *
     * @param buffer
     * @param outMap
     * @param frameSize
     * @param srate
     * @param bytes_per_sample
     * @param channel
     * @param nrChannel
     */
    public MultiCoreAudioDataReader(ByteBuffer buffer, SortedMap<Integer, Double[]> outMap, int frameSize, int srate, int bytes_per_sample, int channel, int nrChannel) {
        this.buffer = buffer;
        this.srate = srate;
        this.frameSize = frameSize;
        this.byte_per_sample = bytes_per_sample;
        this.chToRead = channel;
        this.noOfCh = nrChannel;
        this.chunkData = outMap;

    }

    public void initReader(int start, int end) {
        this.startByte_frame = start;
        this.endByte_frame = end;

        int midByte = (int) Math.ceil((startByte_frame + endByte_frame) / 2);
        midByte_frame = getFrameNrInBuffer(midByte) * frameSize; // last byte of the frame 
        this.totalFramesInBuffer = (int) (Math.ceil((endByte_frame - startByte_frame) + 1) / frameSize);
        this.totalDurationOfbuffer = (totalFramesInBuffer / srate);

        setCHUNK_THRESHOLD(3 * 60); // 4 mins

    }

    public int getFrameNrInBuffer(int byteNr) {
        return (byteNr % frameSize) != 0 ? (int) (byteNr / frameSize) + 1 : (byteNr / frameSize);
    }

    public int getCHUNK_THRESHOLD() {
        return CHUNK_THRESHOLD;
    }

    public void setCHUNK_THRESHOLD(int CHUNK_THRESHOLD) {
        mylogger.log(Level.FINE, "{0} Setting Threshold: {1}", new Object[]{Thread.currentThread().getName(), CHUNK_THRESHOLD});
        this.CHUNK_THRESHOLD = CHUNK_THRESHOLD;
    }

    private int getSixteenBitSample(int high, int low) {
        return (high << 8) + (low & 0x00ff);
    }

    @Override
    protected SortedMap<Integer, Double[]> compute() {

        if (this.totalFramesInBuffer > 1) {

            if (this.totalDurationOfbuffer <= CHUNK_THRESHOLD) {
                // do here

                buffer.position(startByte_frame);
                byte[] dataBytes = new byte[endByte_frame - startByte_frame];
                buffer.get(dataBytes);

                ArrayList<Double> chDataArray = new ArrayList<Double>();
                int channelByteStart = 0; // channel numbering starts from 1
                int channelByteSkip = 1 + (noOfCh - 1) * (frameSize / noOfCh);
                int singleChannel_bytes = frameSize / noOfCh;

                // only read the channels data that is requested
                for (int t = channelByteStart; t < dataBytes.length - (noOfCh * singleChannel_bytes);) {

                    int low = (int) dataBytes[t];
                    t++;
                    int high = (int) dataBytes[t];
                    t += channelByteSkip;
                    int sample_16bit = getSixteenBitSample(high, low);

                    //  2^15 as we negative values as well
                    double sampleNorm = sample_16bit / Math.pow(2, 15);                 // normalize it!
                    chDataArray.add(sampleNorm);

                }

                Double[] chData = new Double[chDataArray.size()];
                chData = chDataArray.toArray(chData);
                chDataArray.clear();
                chunkData.put(getFrameNrInBuffer(midByte_frame), chData);

            } else {
                MultiCoreAudioDataReader leftAudDataReader, rightAudDataReader;

                leftAudDataReader = new MultiCoreAudioDataReader(buffer.duplicate(), chunkData, frameSize, srate, byte_per_sample, chToRead, noOfCh);
                rightAudDataReader = new MultiCoreAudioDataReader(buffer.duplicate(), chunkData, frameSize, srate, byte_per_sample, chToRead, noOfCh);

                leftAudDataReader.initReader(startByte_frame, midByte_frame);
                rightAudDataReader.initReader(midByte_frame, endByte_frame);
                leftAudDataReader.fork();
                chunkData.putAll(rightAudDataReader.compute()); // order is important
                chunkData.putAll(leftAudDataReader.join());

            }
        }
        return chunkData;

    }

    private double getDurOfByte(int byteOfFrame) {

        return getFrameNrInBuffer(byteOfFrame) / srate;

    }

}
