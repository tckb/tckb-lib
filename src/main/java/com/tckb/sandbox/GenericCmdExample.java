/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.sandbox;

import com.tckb.util.cmd.GenericCmdLogObserver;
import com.tckb.util.cmd.GenericCmdv2;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ctungathur
 */
public class GenericCmdExample extends Observable {

    public static void main(String[] args) {
        try {

//            GenericCmd testPM = new GenericCmd("notepad");
//            testPM.enableDefaultObserver();
//
//            //testPM.attachLogObserver(testPMObserver); 
//            // Same as testPM.getLogger().addObserver(testPMObserver)
//            testPM.setCommand("notepad");
//            testPM.runCommand();
//            

            GenericCmdv2 testPM2 = new GenericCmdv2("java-run");

            GenericCmdLogObserver defaultObserver = testPM2.enableDefaultObserver();
            defaultObserver.attachMethodCallWithParams(GenericCmdExample.class.getName(), "sayHello");
            defaultObserver.attachMethodCallWithParams(GenericCmdExample.class.getName(), "eat", new Integer(10), new Double(5.55), "croissant");
            defaultObserver.attachMethodCall(GenericCmdExample.class.getName(), "sayGoodBye");
            // defaultObserver.attachMethodCallWithParams(main.class.getName(), "test");
            testPM2.setCommand("java");
            testPM2.addFlag("jar", "/Users/tckb/Desktop/Junk/Text2XCAS.jar");
            testPM2.runCommand(false);


        } catch (Exception ex) {
            Logger.getLogger(GenericCmdExample.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void eat(Integer num, Double b, String text) {
        System.out.println("Eating demon ate a " + "num : " + num + " a double: " + b + " and text: " + text);
    }

    public void sayHello() {
        System.out.println("Eating demon says Hello!");
    }

    public void sayGoodBye() {
        System.out.println("Eating demon says Bye bye!");
    }
}
