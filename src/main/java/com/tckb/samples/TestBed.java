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

import com.tckb.borrowed.tarsos.fft.FFT;

/**
 *
 * @author tckb < Chandra [dot] Tungathurthi [at] rwth-aachen.de >
 */
public class TestBed {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        try {
//            try {
//                System.out.println(new NonTrivialAudio(Utility.UI.getFile(null)).getAudioNormData_multicore(1,10).length);
//                
//                
//                
//                
//                
//                
//                
//            } catch (IOException ex) {
//                Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (UnsupportedAudioFileException ex) {
//                Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (LineUnavailableException ex) {
//                Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } catch (NonTrivialAudio.InvalidChannnelException ex) {
//            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//        }

        
        float[] data = new float[]{1.f,1.f,1.f,1.f,1.f};
        FFT fft = new FFT(data.length);
        fft.forwardTransform(data);
      
        for(int i=0;i<data.length;i++){
            System.out.println(data[i]);
        }

        
        
        
        
    }

}
