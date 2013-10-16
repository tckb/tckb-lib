/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.sandbox;

import com.tckb.util.Utility;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.demos.DisplayWebcamVideo;
import com.xuggle.xuggler.ICodec;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author tckb
 */
public class XugglerScreenRecorder {

    private static final double FRAME_RATE = 23;
    private static final int SECONDS_TO_RUN_FOR = 2;
    private static final String outputFilename = Utility.USER_HME + "/out.mp4";
    private static Dimension screenBounds;
   
    public static void main(String[] args) {


        // let's make a IMediaWriter to write the file.

        final IMediaWriter writer = ToolFactory.makeWriter(outputFilename);
        final IMediaReader reader = ToolFactory.makeReader(writer.getContainer());

        screenBounds = Toolkit.getDefaultToolkit().getScreenSize();


        // We tell it we're going to add one video stream, with id 0,

        // at position 0, and that it will have a fixed frame rate of FRAME_RATE.

        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,
                screenBounds.width / 2, screenBounds.height / 2);


        long startTime = System.nanoTime();



        for (int index = 0; index < SECONDS_TO_RUN_FOR * FRAME_RATE; index++) {


            System.out.println(" recording: inx:" + index + "/" + SECONDS_TO_RUN_FOR * FRAME_RATE);


// take the screen shot


            BufferedImage screen = getDesktopScreenshot();



// convert to the right image type


            BufferedImage bgrScreen = convertToType(screen,
                    BufferedImage.TYPE_3BYTE_BGR);



// encode the image to stream #0


            writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime,
                    TimeUnit.NANOSECONDS);



// sleep for frame rate milliseconds


            try {


                Thread.sleep((long) (1000 / FRAME_RATE));


            } catch (InterruptedException e) {
                // ignore
            }




        }



        // tell the writer to close and write the trailer if  needed

        writer.close();
        
        ToolFactory.makeViewer();
        
        
        

    }

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {



        BufferedImage image;


        // if the source image is already the target type, return the source image

        if (sourceImage.getType() == targetType) {


            image = sourceImage;

        } // otherwise create a new image of the target type and draw the new image
        else {


            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);


            image.getGraphics().drawImage(sourceImage, 0, 0, null);

        }


        return image;


    }

    private static BufferedImage getDesktopScreenshot() {

        try {


            Robot robot = new Robot();


            Rectangle captureSize = new Rectangle(screenBounds);


            return robot.createScreenCapture(captureSize);

        } catch (AWTException e) {


            e.printStackTrace();


            return null;

        }


    }
}
