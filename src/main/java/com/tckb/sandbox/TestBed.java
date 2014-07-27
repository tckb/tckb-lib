/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.sandbox;

import com.tckb.audio.NonTrivialAudio.InvalidChannnelException;
import com.tckb.util.Utility;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 *
 * @author tckb
 */
public class TestBed {

    public static void main(String[] args) throws InvalidChannnelException {
//        edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D fft_double = new DoubleFFT_2D(1, 10);

//        try {
//            //        try {
//            //            NonTrivialAudio audio = new NonTrivialAudio(new File("/Users/tckb/Msc-MI/Thesis/audio@thesis/orig/audiotest_cut.wav"));
//            //
//            //            System.out.println("Audio Header information:"+audio.getHeader());
//            //                public void actionPerformed(ActionEvent ae) {
//            //                    System.out.println("I'm called");
//            //                }
//            //            });
//            //            t.setDelay(5000);
//            ////            t.setRepeats(false);
//            //        
//            //        
//
//
//            //        try {
//            //            while(true){
//            //                
//            //            }
//            //            
//            //        try {
//            //            NonTrivialAudio audio = new NonTrivialAudio(new File("/Users/tckb/Msc-MI/Thesis/audio@thesis/orig/audiotest_cut.wav"));
//            //
//            //            System.out.println("Audio Header information:"+audio.getHeader());
//            //            
//            //            
//            //            
//            //            
//            //            audio.start();
//            //            audio.pause();
//            //            audio.stop();
//            //            
//
//
//            //            File f = Utility.getFileFromUI(null);
//            //            final FileChannel channel = new FileInputStream(f).getChannel();
//            //            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
//            //            System.out.println("Buffer size: " + buffer.capacity());
//            //            System.out.println("header size: " + new WAVHeader(f.getAbsolutePath()));
//            //
//            //
//            //
//            //
//            //            // when finished
//            //            channel.close();
//            //
//
//
//
//
//            //        File someFile = Utility.getFileFromUI(null);
//            //        File dupFile = Utility.makeDuplicate(someFile);
//            //        System.out.println("File Contents: " + Utility.readFileAsString(dupFile));
//            //        System.out.println("File Contents: " + Utility.readFileAsString(dupFile));
//
//            //
//            //        XYSeries series = new XYSeries("Average Size");
//            //        series.add(20.0, 10.0);
//            //        series.add(40.0, 20.0);
//            //        series.add(70.0, 50.0);
//            //        XYDataset xyDataset = new XYSeriesCollection(series);
//            //
//            //        JFreeChart chart = ChartFactory.createXYAreaChart(null, null, null, xyDataset, PlotOrientation.HORIZONTAL, true, true, true);
//            //                
//
//
//
//
//
//            //        } catch (IOException ex) {
//            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//            //        } catch (UnsupportedAudioFileException ex) {
//            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//            //        } catch (LineUnavailableException ex) {
//            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//            //        }
//            //            //        
//            //        } catch (Exception ex) {
//            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//            //            //        }
//            //            //
//            //
////            File someFile = Utility.getFileFromUI(null);
//            //
//            //
//
//            Logger.getLogger("com.tckb.audio").setLevel(Level.INFO);
//
//            final NonTrivialAudio a = new NonTrivialAudio(Utility.getFileFromUI(null));
//
//
//
////            final NonTrivialAudio a = new NonTrivialAudio(new File("/Users/tckb/Projects/AvaTech/TestData/Ata_01_CE_1_13.wav"));
//
////
////            System.out.println("Reading fast2");
////            long start1 = Utility.tic();
////            ArrayList<Double[]> data1 = a.getAudioData_fast2(1);
////            double time1 = Utility.toc(start1);
////            System.out.println("Time taken: " + time1 + " sec");
////            System.out.println(data1.size());
//
//
//
//
//
//
//            System.out.println("Reading fast3");
//            long start2 = Utility.tic();
//            SortedMap<Integer, Double[]> data2 = a.getAudioData_fast3(1);
//            double time = Utility.toc(start2) * 1000;
//            System.out.println("Time taken: " + Utility.toFormatedTimeString((int) time) + " sec");
//            System.out.println(data2.keySet());
//
////            System.out.println(1+14>>1);
//
//
//
//
//
//            ////        
//            //            new Thread() {
//            //                @Override
//            //                public void run() {
//            //                    try {
//            //                        System.out.println("Reading fast");
//            //                        long start = System.nanoTime();
//            //                        ArrayList<Double[]> data = a.getAudioData_fast2(1);
//            //                        long end = System.nanoTime();
//            //                        System.out.println("Time taken: " + (end - start) / Math.pow(10, 9) + " sec");
//            //                    } catch (InvalidChannnelException ex) {
//            //                        Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//            //                    }
//            //                }
//            //            }.start();
//            //
//            //
//            //        } catch (IOException ex) {
//            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//            //        } catch (UnsupportedAudioFileException ex) {
//            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//            //        } catch (LineUnavailableException ex) {
//            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//            //        }
//
//
//
//
//            //        System.out.println(Utility.toFormatedTimeString(1000*60));
//        } catch (IOException ex) {
//            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedAudioFileException ex) {
//            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (LineUnavailableException ex) {
//            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
        BigDecimal bigNumber1 = new BigDecimal(3.333336, MathContext.DECIMAL64);
        BigDecimal bigNumber2 = new BigDecimal(1212121, MathContext.DECIMAL64);

        File tmp = Utility.createTmpFile();
        if (Utility.saveObjectToFile(tmp, bigNumber1, new TestClass())) {
            System.out.println(" Obhect from file: " + (Object) Utility.getObjectFromFile(tmp)[0]);
        }else{
            System.out.println("OOOOPPPPSSs");
        }
             

    }
}
