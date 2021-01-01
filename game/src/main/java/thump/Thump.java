/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump;

import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import thump.game.DoomMain;
import static thump.base.Defines.logger;

/**
 *
 * @author mark
 */
public class Thump {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            logger.setLevel(Level.CONFIG);
            logger.addHandler(new Handler() {
                @Override
                public void publish(LogRecord record) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(
                    MessageFormat.format(record.getMessage(), record.getParameters())
                    );
                    System.out.println(sb.toString());
                }

                @Override
                public void flush() {}

                @Override
                public void close() throws SecurityException {}
            });
            logger.setUseParentHandlers(false);
            List<String> aargs = new ArrayList<>();
            aargs.addAll(Arrays.asList(args));
            
            DoomMain doomMain = new DoomMain(aargs);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Thump.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
