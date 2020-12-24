/*
 * BSP Clip Range
 */
package thump.render;

import java.text.MessageFormat;

/**
 *
 * @author mark
 */
public class ClipRange {
    public int first = 0;
    public int last = 0;

    @Override
    public String toString() {
        MessageFormat messageFormat = new MessageFormat(" first: {0}  last:{1}");
        return messageFormat.format(new Object[]{first, last});
    }
    
    
}
