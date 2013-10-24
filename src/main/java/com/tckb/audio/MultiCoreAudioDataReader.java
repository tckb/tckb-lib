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
    private ByteBuffer buffer; // audio buffer
    private int sampleSize;
    private final int frameSize;
    private int chunk_size;
    private int chToRead;
    private final int noOfCh;
    private int availableChunks;
    private int totalFramesInBuffer;
    private int startByte_frame, endByte_frame;
    private static final Logger mylogger = Logger.getLogger("com.tckb.audio");
    private SortedMap<Integer, Double[]> chunkData;
    private final int srate;
    private final int byte_per_sample;
    private int midByte_frame;
    private double totalDurationOfbuffer;  // in secs

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

        setCHUNK_THRESHOLD(60); // 4 mins

    }

    public int getFrameNrInBuffer(int byteNr) {
        return (byteNr % frameSize) != 0 ? (int) (byteNr / frameSize) + 1 : (byteNr / frameSize);
    }

    public void setReaderToAudioChannel(int channel) {
        this.chToRead = channel;
    }

    public int getCHUNK_THRESHOLD() {
        return CHUNK_THRESHOLD;
    }

    public void setCHUNK_THRESHOLD(int CHUNK_THRESHOLD) {
        mylogger.log(Level.FINE, "{0} Setting Threshold: {1}", new Object[]{Thread.currentThread().getName(), CHUNK_THRESHOLD});
        this.CHUNK_THRESHOLD = CHUNK_THRESHOLD;
    }

    /**
     * Reads a chunk of frames of size 'chunk_size'
     *
     * @return data
     */
    private int getSixteenBitSample(int high, int low) {
        return (high << 8) + (low & 0x00ff);
    }

    @Override
    public String toString() {
        return super.toString();
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

    public int getFrameNr(int buf_pos) {
        return (buf_pos / frameSize) + 1;
    }

    public int getFrameNr_chunk(int chunkNr) {
        return (chunkNr - 1) * chunk_size;
    }

    public int getChunkNr(int frameNr) {
        return (frameNr / chunk_size) + 1;
    }

    public int getChunkNr_buf(int buf_pos) {
        return (getFrameNr(buf_pos) / chunk_size) + 1;
    }

    public int getBufPos(int frameNr) {
        return (frameNr - 1) * frameSize;
    }

    public int getBufPos_chunk(int chunkNr) {
        return (getFrameNr(chunkNr) - 1) * frameSize;
    }

    public int getEffectiveBufferCapacity(ByteBuffer buffer) {
        return (buffer.capacity() - buffer.position());
    }

    public int getNoOfFramesInBuffer(ByteBuffer buffer) {
        return (int) Math.ceil((double) getEffectiveBufferCapacity(buffer) / frameSize);

    }

    public int getNoOfChunksInBuffer(ByteBuffer buffer) {
        return (int) Math.ceil((double) getNoOfFramesInBuffer(buffer) / chunk_size);
    }

    private double getDurOfByte(int byteOfFrame) {

        return getFrameNrInBuffer(byteOfFrame) / srate;

    }

}/*
 protected SortedMap<Integer, Double[]> Oldcompute() {

 // Check if the available chunks exceeds threshold
 if (availableChunks <= CHUNK_THRESHOLD) {  // less-than case should not happen in theory

 mylogger.log(Level.INFO, "{0}  availableChunks <= CHUNK_THRESHOLD ; COMPUTING...", Thread.currentThread().getName());

 for (int f = startByte_frame; f < (startByte_frame + totalFramesInBuffer); f += chunk_size) {
 //                System.out.println("Reading chunks");
 //                System.out.println(Thread.currentThread().getName() + "Frame nr position: " + buffer.position());
 int pos = f * frameSize;
 buffer.position(pos);
 // read in chunks
 Double[] data = readAudioChunk(chunk_size);
 Integer chunkNr = getChunkNr_buf(pos);
 chunkData.put(chunkNr, data);

 System.out.println("processing...");

 }

 } else {

 //            System.out.println(Thread.currentThread().getName() + " dividing");
 MultiCoreAudioDataReader leftAudDataReader, rightAudDataReader;
 // Applying Divide - and - conquer rule
 // 'fork' half of the chunks to left 
 // clone the existing buffer
 ByteBuffer copyLeftOfBuffer = buffer.duplicate();
 ByteBuffer copyRightOfBuffer = buffer.duplicate();

 int leftStart_frame, leftEnd_frame;
 int rightStart_frame, rightEnd_frame;

 leftStart_frame = startByte_frame;
 leftEnd_frame = center_frame;

 rightStart_frame = center_frame + 1;
 rightEnd_frame = endByte_frame;

 // setup the position
 copyLeftOfBuffer.position(buffer.position());
 copyLeftOfBuffer.limit(getBufPos(leftEnd_frame)); // in-theory, endChunkLtFrameNr should be less than its size

 copyLeftOfBuffer = copyLeftOfBuffer.slice();
 // create a 'fork'

 leftAudDataReader = new MultiCoreAudioDataReader(copyLeftOfBuffer, frameSize, sampleSize, chunk_size, noOfCh, chunkData);
 //            System.out.println(Thread.currentThread().getName() + " left has:" + leftAudDataReader.availableChunks);

 leftAudDataReader.setReaderToAudioChannel(chToRead);

 leftAudDataReader.fork();

 copyRightOfBuffer.position(getBufPos(rightStart_frame));
 copyRightOfBuffer.limit(buffer.capacity());

 rightAudDataReader = new MultiCoreAudioDataReader(copyRightOfBuffer, frameSize, sampleSize, chunk_size, noOfCh, chunkData);
 rightAudDataReader.setReaderToAudioChannel(chToRead);

 mylogger.log(Level.INFO, "{2} Splitting left:  C: {3} [  {0} - {1} ] ", new Object[]{leftStart_frame, leftEnd_frame, Thread.currentThread().getName(), getNoOfChunksInBuffer(copyLeftOfBuffer)});
 mylogger.log(Level.INFO, "{2} Splitting right: C: {3}  [  {0} - {1} ] ", new Object[]{rightStart_frame, rightEnd_frame, Thread.currentThread().getName(), getNoOfChunksInBuffer(copyRightOfBuffer)});

 SortedMap<Integer, Double[]> map1 = rightAudDataReader.compute();
 SortedMap<Integer, Double[]> map2 = leftAudDataReader.join();
 //rightAudDataReader.fork();

 chunkData.putAll(map1);

 chunkData.putAll(map2);

 // join the result
 // return the result
 // discard the exisiting buffer
 buffer.clear();

 //            invokeAll(leftAudDataReader, rightAudDataReader);
 //            System.out.println(Thread.currentThread().getName() + " finished");
 }
 System.out.println(Thread.currentThread().getName() + " :" + chunkData.keySet() + ":" + chunkData.size());

 return chunkData;
 }

 public MultiCoreAudioDataReader(ByteBuffer buffer, int frameSize, int sampleSize, int chunk_size, int noOfChannels, SortedMap<Integer, Double[]> output) {
 this.buffer = buffer;
 this.sampleSize = sampleSize;
 this.frameSize = frameSize;
 this.noOfCh = noOfChannels;
 this.chunk_size = chunk_size;
 this.chunkData = output;
 this.chToRead = 1; // by default read the first channel
 this.startByte_frame = getFrameNr(buffer.position());
 this.endByte_frame = getFrameNr(buffer.capacity());
 this.center_frame = (startByte_frame + endByte_frame) >> 1; // equal to Math.floor
 this.totalFramesInBuffer = getNoOfFramesInBuffer(buffer);
 this.availableChunks = getNoOfChunksInBuffer(buffer);
 setCHUNK_THRESHOLD(50);

 }
 private Double[] readAudioChunk(int framesToRead) {

 int framesAvailable = getNoOfFramesInBuffer(buffer);
 int actualFramesToRead = (framesToRead <= framesAvailable) ? framesToRead : framesAvailable;
 //        System.out.println(Thread.currentThread().getName() + " Total frames avilable: " + framesAvailable + " Frames requested: " + framesToRead + " frames read: " + actualFramesToRead);
 int start = 0 + (chToRead - 1) * sampleSize;
 int skip = ((noOfCh - chToRead) * sampleSize);
 //        System.out.println("Start byte: " + start + " Skipping bytes: " + skip);
 Double[] data = new Double[actualFramesToRead];
 int i = 0;
 // works for only 2-bytes-sample
 for (int t = start;
 t < buffer.capacity();
 t += skip) {

 int low = buffer.get(t);
 int high = buffer.get(t + 1);
 int sample16bit = getSixteenBitSample(high, low);

 double sample = sample16bit / Math.pow(2, 15);

 data[i++] = sample; // check if works?

 if (i >= actualFramesToRead) {
 break;
 }

 }
 return data;
 }

 */
