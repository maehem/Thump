/*
 * Logging mamagement
 */
package thump.system;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import static thump.global.Defines.logger;

/**
 *
 * @author mark
 */
public class ThumpLogger {
    
    public static void init() {
        setLoggerLevel();
    }
    
    private static void setLoggerLevel() {
        logger.setLevel(Level.CONFIG);
        // Handler for console (reuse it if it already exists)
        Handler consoleHandler = null;
        //see if there is already a console handler
        for (Handler handler : logger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                //found the console handler
                consoleHandler = handler;
                break;
            }
        }

        if (consoleHandler == null) {
            //there was no console handler found, create a new one
            consoleHandler = new ConsoleHandler();
            logger.addHandler(consoleHandler);
        }
        
        consoleHandler.setFormatter(new LogOutputFormatter());
        //set the console handler to fine:
        consoleHandler.setLevel(java.util.logging.Level.CONFIG);
    }

}
