/*
 * To change this template, choose Tools | Templates
 * and open the template rawDataStream the editor.
 */
package com.tckb.audio;

import com.tckb.borrowed.elan.WAVHeader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Based on Hack from
 * http://codeidol.com/java/swing/Audio/Play-Non-Trivial-Audio Implementation of
 * javax.sound.sampled.Clip can't handle large audio file
 *
 * @author tckb
 */
public class NonTrivialAudio implements Runnable {

    private static final Logger mylogger = Logger.getLogger("com.tckb.audio");
    private File audSrc;
    private AudioInputStream rawDataStream;
    private SourceDataLine audioLine;
    private WAVHeader header;
    private int frameSize;
    private Thread playThread;
    private boolean playing;
    private boolean notYetEOF;
    private int totalFrames = -1;
    private boolean playSafe = false; // for maintaining synchronisation
    private long bytesToSkip = -1;
    private int totalBytesread = -1; // no of bytes read at while running 
    private double noOfRuns;
    private long bytesLeft;
    private byte[] empty32kBuffer;
    private byte[] empty64kBuffer; // 64k is arbitrary
    private byte[] empty128kBuffer; // 128k is arbitrary

    public static enum CHUNK_SIZE {
        // size32k was optimal from empherical tests

        SIZE_128K(128 * 1024), SIZE_64K(64 * 1024), SIZE_32K(32 * 1024), SIZE_16K(16 * 1024), SIZE_8K(8 * 1024);
        private int size;

        CHUNK_SIZE(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    };

    public NonTrivialAudio(File f)
            throws IOException,
            UnsupportedAudioFileException,
            LineUnavailableException {
        audSrc = f;
        playing = false;
        header = new WAVHeader(f.getAbsolutePath());

        empty32kBuffer = new byte[16 * 1024];

        empty64kBuffer = new byte[CHUNK_SIZE.SIZE_64K.getSize()];

        empty128kBuffer = new byte[CHUNK_SIZE.SIZE_128K.getSize()];
        resetStream();


    }

    @Override
    public void run() {
        totalBytesread = 0;

        synchronized (this) {
            mylogger.fine("Play locked!");
            int readPoint = 0;
            int bytesRead = 0;

            try {
                mylogger.info("--Start of Stream--");
                mylogger.log(Level.INFO, "Bytes available: {0}", rawDataStream.available());
                while (notYetEOF) {
                    if (playing) {




                        bytesRead = rawDataStream.read(empty32kBuffer, readPoint, empty32kBuffer.length - readPoint);
                        if (bytesRead == -1) {
                            notYetEOF = false;
                            break;
                        }
                        // how many frames did we get,
                        // and how many are left over?
                        int frames = bytesRead / frameSize;
                        int leftover = bytesRead % frameSize;


                        totalBytesread += bytesRead;


                        mylogger.log(Level.FINER, "Readpoint: {2} frames read: {0} leftover frames: {1} current read: {3} totalbytes read: {4}", new Object[]{frames, leftover, readPoint, bytesRead, totalBytesread});



//                        if (cnt == noOfRuns) {
//                            mylogger.info(" Seek point reached!");
//                            skipbytes = bytesLeft;
//                        }


                        // send to audioLine
                        audioLine.write(empty32kBuffer, readPoint, bytesRead - leftover); // this one here causes sound to produce

                        // save the leftover bytes
                        System.arraycopy(empty32kBuffer, bytesRead, empty32kBuffer, 0, leftover);

                        readPoint = leftover;



                    } //if playing
                    else {
                        // if not playing                   
                        // Thread.yield(); 
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ie) {
                            mylogger.log(Level.SEVERE, "Play interrupted:{0}", ie);

                        }
                    }

                }// while notYetEOF

                mylogger.info("--End of Stream--");
                audioLine.drain();
                audioLine.stop();
                try {
                    int bytesVailable = rawDataStream.available();

                    if (bytesVailable != 0) {
                        // what, there are still bytes left ? - manually stopped?!
                        mylogger.log(Level.INFO, "Manual overrride! audio bytes still available: {0}", bytesVailable);

                        // release the lock!
                        playSafe = true;
                        rawDataStream.close();

                        notifyAll();
                        mylogger.fine("Lock released!");

                    }
                } catch (Exception ex) {
                    Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    } // run

    public void start() {

        if (playThread != null) {

            if (!playing) {  // this means that the audio has beeen just paused
                mylogger.fine("Not Playing, paused!");

                playing = true;         // so, turn play to true
                audioLine.start();

            } else {                      // this means that the audio has finsihed playing and user would like to play it again

                mylogger.fine("Other case   ");
                resetStream();                      // so, start a new  instance
                startThread();
            }
        } else {
            // this means that its a fresh start
            // stream is already initialised!  

            startThread();      // so, start a new instance
            mylogger.fine("Thread new");

        }
    }

    public void pause() {
        playing = false;
        audioLine.stop();


    }

    public void stop() {

        // manually set the eof to end playing
        this.notYetEOF = false;

    }

    public SourceDataLine getLine() {

        return audioLine;
    }

    public File getFile() {
        return audSrc;
    }

    public double getCurrentSecond() {
        double seconds = 0.0;
        seconds = audioLine.getFramePosition() / audioLine.getFormat().getFrameRate();
        return seconds;
    }

    public int getCurrentFrame() {
        return audioLine.getFramePosition();
    }

    public double getCurrentMS() {

        return getCurrentSecond() * 1000;
    }

    public double getDurationInSeconds() {
        try {


            return getDurationInFrames() / audioLine.getFormat().getFrameRate();

        } catch (Exception ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getDurationInMS() {

        return getDurationInSeconds() * 1000;

    }

    public boolean isRunning() {
        return notYetEOF;
    }

    public int getDurationInFrames() {
        //Check if its already available
        if (totalFrames == -1) {
            AudioInputStream audioData = null;
            int noOfFrames = 0;
            boolean notYetEOF = true;
            try {

                audioData = AudioSystem.getAudioInputStream(audSrc);

                try {

                    int readPoint = 0;
                    int bytesRead = 0;


                    while (notYetEOF) {

                        bytesRead = audioData.read(empty64kBuffer,
                                readPoint,
                                empty64kBuffer.length - readPoint);
                        if (bytesRead == -1) {
                            notYetEOF = false;
                            break;
                        }

                        // how many frames did we get,
                        // and how many are left over?
                        int frames = bytesRead / frameSize;
                        noOfFrames += frames;

                    }


                    mylogger.log(Level.INFO, "No of frames:{0}", noOfFrames);
                    //reset

                } catch (Exception ex) {
                    Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    audioData.close();




                } catch (IOException ex) {
                    Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
                }
            }



            this.totalFrames = noOfFrames;
        }

        return totalFrames;

    }

    public boolean seekSecond(double second) {

        double length = this.getDurationInSeconds();
        mylogger.log(Level.INFO, "Seeking to second: {0} / {1}", new Object[]{second, length});

        if (second > length) {
            return false;
        } else {
            double framesToBeRead = second * audioLine.getFormat().getFrameRate();
            return seek((int) framesToBeRead);
        }


    }

    public boolean seekMS(double ms) {

        double length = this.getDurationInMS();
        mylogger.log(Level.INFO, "Seeking to ms: {0} / {1}", new Object[]{ms, length});

        if (ms > length) {
            return false;
        } else {
            double framesToBeRead = ms * audioLine.getFormat().getFrameRate();
            return seek((int) (framesToBeRead / 1000));
        }


    }

    public boolean seek(int frames) {

        mylogger.log(Level.INFO, "Seeking to frame: {0} / {1} ", new Object[]{frames, this.getDurationInFrames()});
        mylogger.log(Level.FINE, "Frame difference {0}", (frames - this.getCurrentFrame()));





// Stop the current play if playing
        if (isRunning()) {
            // resets the stream when manually stopped
            mylogger.fine("Stopping current play...");
            this.stop();
        }

// Reset the stream

        // try acquiring the lock!
        synchronized (this) {

            while (!playSafe) {
                try {
                    // hold your horses! :P
                    wait();
                } catch (InterruptedException ex) {
                    mylogger.log(Level.SEVERE, null, ex);
                    return false;
                }
            }


        }
//            Thread curTh = Thread.currentThread();
//            mylogger.info("Current Thread: " + curTh.getName());

        //  frames * framesize = bytesread  
        //  second * framerate  = frames 
        bytesToSkip = (long) (frames * frameSize); // set the bytes to skip to reach that frame


        // reading 32k bytes

        noOfRuns = Math.floor(bytesToSkip / (32 * 1024));
        bytesLeft = (bytesToSkip % (32 * 1024));

        mylogger.log(Level.INFO, "Bytes to skip: {2} no.of times to read: {0} bytes remaining:{1} ", new Object[]{noOfRuns, bytesLeft, bytesToSkip});


        // Now start playing!
        this.start();                              // start skips if there are any bytes available
        return true;


    }

    private void resetStream() {
        mylogger.fine("Resetting audio stream");
        try {
            rawDataStream = AudioSystem.getAudioInputStream(audSrc);
            boolean bytesSkip = false;


            // check if there are any bytes to skip, if yes then skip!

            while (bytesToSkip > 0) {
                try {
                    mylogger.log(Level.INFO, "Bytes to skip: {0}/{1}", new Object[]{bytesToSkip, rawDataStream.available()});

                    long bytesRead = rawDataStream.skip(bytesToSkip);
                    bytesToSkip -= bytesRead;

                } catch (IOException ex) {
                    Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
                }

                bytesSkip = true; // lazy !

            }



            AudioFormat format = rawDataStream.getFormat();
            AudioFormat.Encoding formatEncoding = format.getEncoding();
            if (!(formatEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)
                    || formatEncoding.equals(AudioFormat.Encoding.PCM_UNSIGNED))) {
                throw new UnsupportedAudioFileException(
                        audSrc.getName() + " is not PCM audio");
            }
            mylogger.fine("Audio format:PCM");

            frameSize = format.getFrameSize();
            notYetEOF = true;




            if (!bytesSkip) {
                DataLine.Info info =
                        new DataLine.Info(SourceDataLine.class, format);



                audioLine = (SourceDataLine) AudioSystem.getLine(info);
                mylogger.fine("Opening audio line");
                audioLine.open();

                mylogger.fine("Audio line opened");
            } else {
                audioLine.drain();
                audioLine.stop();
            }


            mylogger.log(Level.INFO, "Samples:  {0} Samples / sec @ {1} bits per sample; Frame size: {2}; Big endian : {3} ", new Object[]{audioLine.getFormat().getSampleRate(), audioLine.getFormat().getSampleSizeInBits(), audioLine.getFormat().getFrameSize(), audioLine.getFormat().isBigEndian()});

        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void startThread() {
        this.playing = true;
        playThread = new Thread(this);
        playThread.setName("audio_" + audSrc.getName() + "_play");
        audioLine.start();
        playThread.start();
    }

    public boolean isAlive() {
        if (rawDataStream == null) {
            return false;
        } else {
            try {
                if (rawDataStream.available() <= 0) {
                    return false;
                } else {
                    return true;


                }
            } catch (IOException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);


                return false;
            }
        }
    }

    /**
     * Channel numbering starts with 1 ... getNoChannels()
     *
     * @param CurrChannel
     * @return
     */
    public double[] getAudioData_fast(int CurrChannel) throws InvalidChannnelException {
        CurrChannel = CurrChannel - 1;
        System.out.println("Reading file...");
        try {
            FileChannel fchannel = new FileInputStream(audSrc).getChannel();
            MappedByteBuffer buffer = fchannel.map(FileChannel.MapMode.READ_ONLY, 0, fchannel.size());
//               buffer = buffer.load();

            int headerSize = getHeader().getHeaderSize();

            int sampleIndex = 0;
            int numChannels = getHeader().getNumberOfChannels();


            if (CurrChannel < 0 || CurrChannel >= numChannels) {
                throw new InvalidChannnelException("Channel not found");
            }

            int frameLength = getHeader().getDataLength();
            // length of stream in-terms of frames
//            int frmSze = (int) myStream.getFormat().getFrameSize(); // 2 , 4 ... bytes per frame
            System.out.println("allocating");
//            IntBuffer channelBuffer = IntBuffer.allocate(frameLength);
//            DoubleBuffer channeBuffer = DoubleBuffer.allocate(frameLength);
            //int[][] toReturn = new int[numChannels][frameLength];
            //int[] bytesToReturn = new int[frameLength];
//            byte[] bytes = new byte[frameLength * frmSze];

            //  myStream.read(bytes);

            System.out.println("done");
            double[] channeBuffer = new double[frameLength];
            int k = 0;
            for (int t = headerSize; t < buffer.capacity();) {
                for (int channel = 0; channel < numChannels; channel++) {
                    // only read the channels data that is requested

                    int low = (int) buffer.get(t);
                    t++;
                    int high = (int) buffer.get(t);
                    t++;
                    int sample_16bit = getSixteenBitSample(high, low);

                    // normalize it!
                    //  2^15 as we negative values as well
                    double sample = sample_16bit / Math.pow(2, 15);

//                    System.out.println(sample);

                    if (channel == CurrChannel) {
                        //  bytesToReturn[sampleIndex] = sample;
//                        channelBuffer.position(t);
                        channeBuffer[k++] = (sample);
                    }
                    // toReturn[channel][sampleIndex] = sample;

                }

                sampleIndex++;
            }
            buffer.clear();
            return channeBuffer;

        } catch (Exception ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

// channelToRead starts with 1 ... noOfChannels
// assumes that buffer contains all channel data
    private Double[] readAudioChunk(int framesToRead, MappedByteBuffer buffer, int channelToRead, int noOfChannels) {

        int availableBuffer = (buffer.capacity() - buffer.position()); // total capacity of buffer
        int frameSize = (noOfChannels * getHeader().getSampleSize());
        int framesAvailable = availableBuffer / frameSize;

        int actualFramesToRead = (framesToRead <= framesAvailable) ? framesToRead : framesAvailable;

//        System.out.println("Total frames avilable: " + framesAvailable + " Frames requested: " + framesToRead + " frames read: " + actualFramesToRead);


        int start = 0 + (channelToRead - 1) * getHeader().getSampleSize();
        int skip = ((noOfChannels - channelToRead) * getHeader().getSampleSize());


//        System.out.println("Start byte: " + start + " Skipping bytes: " + skip);

        Double[] data = new Double[actualFramesToRead];
        int i = 0;
// works for only 2-bytes-sample
        for (int t = start; t < buffer.capacity(); t += skip) {

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

    public ArrayList<Double[]> getAudioData_fast2(int CurrChannel) throws InvalidChannnelException {
        CurrChannel = CurrChannel - 1;
        ArrayList<Double[]> channelData = null;
        FileChannel fchannel = null;
        MappedByteBuffer buffer = null;
        System.out.println("Reading file...");
        try {
            fchannel = new FileInputStream(audSrc).getChannel();
            buffer = fchannel.map(FileChannel.MapMode.READ_ONLY, 0, fchannel.size());

            buffer.force(); // performance gain??


            int headerSize = getHeader().getHeaderSize();

            int numChannels = getHeader().getNumberOfChannels();


            if (CurrChannel < 0 || CurrChannel >= numChannels) {
                throw new InvalidChannnelException("Channel not found");
            }


            int frameSize = numChannels * getHeader().getSampleSize();
            int totalFramesInBuffer = buffer.capacity() / frameSize;
            int totalLengthOfChannel = getHeader().getDataLength();


//            System.out.println("frameSize: " + frameSize + " totalFramesInBuffer: " + totalFramesInBuffer);

            // chuncks calculated based on frames

            int chunk_size = totalFramesInBuffer;
            // determine the chunk size

            for (CHUNK_SIZE csize : CHUNK_SIZE.values()) {

                if (chunk_size > csize.getSize() && (((int) (chunk_size / csize.getSize())) > 0)) {
                    chunk_size = csize.getSize(); // got the size 
                    break;
                }


            }
//            System.out.println("Chunk size determined as: " + chunk_size);

            int nofChunks = new Double(Math.ceil((double) totalFramesInBuffer / chunk_size)).intValue();

//            System.out.println("No of Chunks: " + nofChunks);
            channelData = new ArrayList<Double[]>();


            // split the entire channel into chunks

            for (int f = 0; f <= totalFramesInBuffer; f += chunk_size) {
//                System.out.println("Reading chunks");
//                System.out.println("Buffer position: " + buffer.position());
                int pos = f * frameSize;
                buffer.position(pos);


                Double[] data = readAudioChunk(chunk_size, buffer, CurrChannel + 1, numChannels);
                channelData.add(data);
                data = null;

            }



        } catch (Exception ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
//                System.gc();
                buffer.clear();
                fchannel.close();
            } catch (IOException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return channelData;

    }

    public SortedMap<Integer, Double[]> getAudioData_fast3(int CurrChannel) throws InvalidChannnelException {
        CurrChannel = CurrChannel - 1;
        ArrayList<Double[]> channelData = null;
        FileChannel fchannel = null;
        MappedByteBuffer buffer = null;
        SortedMap<Integer, Double[]> output = new TreeMap<Integer, Double[]>();
        SortedMap outMap = Collections.synchronizedSortedMap(output);
        System.out.println("Reading file...");
        try {
            fchannel = new FileInputStream(audSrc).getChannel();
            buffer = fchannel.map(FileChannel.MapMode.READ_ONLY, 0, fchannel.size());

            buffer.force(); // performance gain??


            int headerSize = getHeader().getHeaderSize();

            int numChannels = getHeader().getNumberOfChannels();


            if (CurrChannel < 0 || CurrChannel >= numChannels) {
                throw new InvalidChannnelException("Channel not found");
            }


            int frameSize = numChannels * getHeader().getSampleSize();
            int totalFramesInBuffer = buffer.capacity() / frameSize;
            int totalLengthOfChannel = getHeader().getDataLength();


//            System.out.println("frameSize: " + frameSize + " totalFramesInBuffer: " + totalFramesInBuffer);

            // chuncks calculated based on frames

            int chunk_size = totalFramesInBuffer;
            // determine the chunk size

            for (CHUNK_SIZE csize : CHUNK_SIZE.values()) {

                if (chunk_size > csize.getSize() && (((int) (chunk_size / csize.getSize())) > 0)) {
                    chunk_size = csize.getSize(); // got the size 
                    break;
                }


            }

            int processors = Runtime.getRuntime().availableProcessors();
            int parallelism = processors;


            System.out.println("Parallelism: " + parallelism);




            NaiveMutlicoreAudDataReader dataReader = new NaiveMutlicoreAudDataReader(buffer, frameSize, getHeader().getSampleSize(), chunk_size, numChannels, outMap);

            dataReader.setReaderToAudioChannel(CurrChannel + 1);

            ForkJoinPool pool = new ForkJoinPool(parallelism);
            pool.invoke(dataReader);





////            System.out.println("Chunk size determined as: " + chunk_size);
//
//            int nofChunks = new Double(Math.ceil((double) totalFramesInBuffer / chunk_size)).intValue();
//
////            System.out.println("No of Chunks: " + nofChunks);
//            channelData = new ArrayList<Double[]>();
//
//
//            // split the entire channel into chunks
//
//            for (int f = 0; f <= totalFramesInBuffer; f += chunk_size) {
////                System.out.println("Reading chunks");
////                System.out.println("Buffer position: " + buffer.position());
//                int pos = f * frameSize;
//                buffer.position(pos);
//
//
//                Double[] data = readAudioChunk(chunk_size, buffer, CurrChannel + 1, numChannels);
//                channelData.add(data);
//                data = null;
//
//            }



        } catch (Exception ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
//                System.gc();
                buffer.clear();
                fchannel.close();
            } catch (IOException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return outMap;

    }

    /**
     * Channel numbering starts with 1 ... getNoChannels()
     *
     * @param CurrChannel
     * @return
     */
    public int[] getRawAudioData(int CurrChannel) throws InvalidChannnelException {
        CurrChannel = CurrChannel - 1;

        AudioInputStream myStream = null;
        try {
            myStream = AudioSystem.getAudioInputStream(audSrc);
            try {
                int sampleIndex = 0;
                int numChannels = myStream.getFormat().getChannels();

                if (CurrChannel < 0 || CurrChannel >= numChannels) {
                    throw new InvalidChannnelException("Channel not found");
                }

                int frameLength = (int) myStream.getFrameLength();  // length of stream in-terms of frames

                int frmSze = (int) myStream.getFormat().getFrameSize(); // 2 , 4 ... bytes per frame


                //int[][] toReturn = new int[numChannels][frameLength];
                int[] bytesToReturn = new int[frameLength];
                byte[] bytes = new byte[frameLength * frmSze];

                myStream.read(bytes);


                for (int t = 0; t < bytes.length;) {
                    for (int channel = 0; channel < numChannels; channel++) {
                        // only read the channels data that is requested

                        int low = (int) bytes[t];
                        t++;
                        int high = (int) bytes[t];
                        t++;
                        int sample = getSixteenBitSample(high, low);



                        if (channel == CurrChannel) {
                            bytesToReturn[sampleIndex] = sample;
                        }
                        // toReturn[channel][sampleIndex] = sample;


                    }

                    sampleIndex++;
                }
                bytes = null;
                System.gc();
                return bytesToReturn;
            } catch (IOException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                myStream.close();
            } catch (IOException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public double[] getAudioData(int CurrChannel) throws InvalidChannnelException {
        CurrChannel = CurrChannel - 1;

        AudioInputStream myStream = null;
        try {
            myStream = AudioSystem.getAudioInputStream(audSrc);
            try {
                int sampleIndex = 0;
                int numChannels = myStream.getFormat().getChannels();


                if (CurrChannel < 0 || CurrChannel >= numChannels) {
                    throw new InvalidChannnelException("Channel not found");
                }


                int frameLength = (int) myStream.getFrameLength();  // length of stream in-terms of frames

                int frmSze = (int) myStream.getFormat().getFrameSize(); // 2 , 4 ... bytes per frame


                //int[][] toReturn = new int[numChannels][frameLength];
                double[] bytesToReturn = new double[frameLength];
                byte[] bytes = new byte[frameLength * frmSze];

                myStream.read(bytes);


                for (int t = 0; t < bytes.length;) {
                    for (int channel = 0; channel < numChannels; channel++) {
                        // only read the channels data that is requested


                        int low = (int) bytes[t];
                        t++;
                        int high = (int) bytes[t];
                        t++;
                        int sample_16bit = getSixteenBitSample(high, low);

                        // normalize it!
                        //  2^15 as we negative values as well
                        // ... -1 0 +1 ... 
                        double sample = sample_16bit / Math.pow(2, 15);

                        if (channel == CurrChannel) {
                            bytesToReturn[sampleIndex] = sample;
                        }
                        // toReturn[channel][sampleIndex] = sample;


                    }

                    sampleIndex++;
                }
                bytes = null;
                System.gc();
                return bytesToReturn;
            } catch (IOException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                myStream.close();
            } catch (IOException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private int getSixteenBitSample(int high, int low) {
//        if (rawDataStream.getFormat().isBigEndian()) {
//            return (high << 8) + (low & 0x00ff);
//        } else {
//            return low + high;
//        }
        return (high << 8) + (low & 0x00ff);
    }

    public int getNoChannels() {
        return audioLine.getFormat().getChannels();
    }

    public int getSampleRate() {
        return (int) rawDataStream.getFormat().getSampleRate();
    }

    public WAVHeader getHeader() {
        return header;
    }

    @Override
    public String toString() {
        return header.toString();

    }

    public class InvalidChannnelException extends Exception {

        private final String message;

        public InvalidChannnelException() {
            message = "";
        }

        public InvalidChannnelException(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return "Invalid Channel: " + message;
        }
    }

    public class UnsupportedAudioFile extends Exception {

        @Override
        public String getMessage() {
            return "Only 16-bit-per-sample supported ";
        }
    }
}
// -- DEAD CODE --
// 
// empty34kBuffer.length - bytes = readpoint
//                        if (skipbytes > 0) {
//                            readPoint = (int) (empty34kBuffer.length - skipbytes);
//                            skipbytes = 0;
//                        }
// -------------------------------------------

