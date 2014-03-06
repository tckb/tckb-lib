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
import com.tckb.audio.ui.display.spect.SpectDisplay;
import com.tckb.audio.ui.display.spect.SpectParams;
import com.tckb.borrowed.tarsos.fft.window.GaussWindow;

/**
 *
 * @author tckb < Chandra [dot] Tungathurthi [at] rwth-aachen.de >
 */
public class SpectProcessor extends AudioProcessor {
    
    SpectParams params = new SpectParams();
    Double[][] spectData = new Double[srate][srate];
    

    public SpectProcessor(NonTrivialAudio audio, int ch) throws NonTrivialAudio.InvalidChannnelException {
        super(audio, ch);
        params.setFFTParams(new GaussWindow(), 1024, 512);
        
    }

    @Override
    public AudioDisplay getPanel() {
        return new SpectDisplay(params);
        
    }
    
    
    public void setParams(SpectParams params){
        this.params=params;
    }
    
    
    
    
    
    
    

}
