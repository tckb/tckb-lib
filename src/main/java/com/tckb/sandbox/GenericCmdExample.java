/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.sandbox;

import com.tckb.util.cmd.GenericCmdLogObserver;
import com.tckb.util.cmd.GenericCmdOutputObserver;
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
         
            GenericCmdv2 testPM2 = new GenericCmdv2("java-run");
            testPM2.setCommand("java");
            testPM2.addFlag("version", "");
            GenericCmdOutputObserver rawOutputObserver = new GenericCmdOutputObserver();
            GenericCmdLogObserver cmdLogObserver = new GenericCmdLogObserver();
            cmdLogObserver.attachMethodCallWithParams(GenericCmdExample.class.getName(), "sayHello");
            cmdLogObserver.attachMethodCallWithParams(GenericCmdExample.class.getName(), "eat", new Integer(10), new Double(5.55), "croissant");
            cmdLogObserver.attachMethodCall(GenericCmdExample.class.getName(), "sayGoodBye");
            cmdLogObserver.enableUINotification();

            testPM2.attachObserver(rawOutputObserver);
            testPM2.attachObserver(cmdLogObserver);
            testPM2.runCommand(false);

            String logOut = cmdLogObserver.waitForOutput();
            String rawOut = rawOutputObserver.waitForOutput();

            System.out.println("Here is raw output: ");
            System.out.println(rawOut);

            System.out.println("Here is log output: ");
            System.out.println(logOut);

        } catch (InterruptedException ex) {
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
