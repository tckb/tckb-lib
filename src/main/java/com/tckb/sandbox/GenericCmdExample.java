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
            // Create command
            GenericCmdv2 testPM2 = new GenericCmdv2("java-run");
            // Configure command
            testPM2.setCommand("java");
            testPM2.addFlag("version", "");
            // Create observers
            GenericCmdOutputObserver rawOutputObserver = new GenericCmdOutputObserver();
            GenericCmdLogObserver cmdLogObserver = new GenericCmdLogObserver();
            // Configure observer
            cmdLogObserver.attachMethodCallWithParams(GenericCmdExample.class.getName(), "sayHello");
            cmdLogObserver.attachMethodCallWithParams(GenericCmdExample.class.getName(), "eat", new Integer(10), new Double(5.55), "croissant");
            cmdLogObserver.attachMethodCall(GenericCmdExample.class.getName(), "sayGoodBye");
            cmdLogObserver.enableUINotification();

            // Attach observers
            testPM2.attachObserver(rawOutputObserver);
            testPM2.attachObserver(cmdLogObserver);

            // Run command
            testPM2.runCommand(false);

            // Get output
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
// Sample output
/*
Eating demon says Hello!
Eating demon ate a num : 10 a double: 5.55 and text: croissant
Eating demon says Bye bye!
Here is raw output: 

java version "1.6.0_65"
Java(TM) SE Runtime Environment (build 1.6.0_65-b14-462-11M4609)
Java HotSpot(TM) 64-Bit Server VM (build 20.65-b04-462, mixed mode)

Here is log output: 

[java-run-log]LogStarted: Fri Nov 15 14:14:14 CET 2013
[java-run-log]==============================================
[java-run-log]Executing command: [java, -version, ]
[java-run-log]Output:
[java-run-log]java version "1.6.0_65"
[java-run-log]Java(TM) SE Runtime Environment (build 1.6.0_65-b14-462-11M4609)
[java-run-log]Java HotSpot(TM) 64-Bit Server VM (build 20.65-b04-462, mixed mode)
[java-run-log]Command executed with return value: 0
[java-run-log]==============================================
*/
