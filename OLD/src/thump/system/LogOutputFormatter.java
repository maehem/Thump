/*
 * Log Output Formatter
 */
package thump.system;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author mark
 */
public class LogOutputFormatter extends SimpleFormatter {

    @Override
    public synchronized String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
//            sb.append(record.getLoggerName());
        // Prepend the message with the class and method name if Level.FINEST
//        if (    record.getLevel().intValue() == Level.FINEST.intValue()
//                && record.getSourceClassName() != null) {
            sb.append("[");
            String className = record.getSourceClassName();
            sb.append(className.substring(className.lastIndexOf('.')+1));
//            if (record.getSourceMethodName() != null) {
//                sb.append(".");
//                sb.append(record.getSourceMethodName());
//            }
            sb.append("]");
            sb.append(": ");
//        }
//        sb.append(lineSeparator);
        String message = formatMessage(record);
        //sb.append(record.getLevel().getLocalizedName());
        //sb.append(": ");
        sb.append(message);
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
            }
        }
        return sb.toString();

    }
}
