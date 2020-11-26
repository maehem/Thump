/*
 * Canvas Screen to draw all graphics
 */
package thump.render;

import java.awt.Canvas;
import java.awt.image.BufferStrategy;

/**
 *
 * @author mark
 */
@SuppressWarnings("serial")
public class ViewCanvas extends Canvas {

    private BufferStrategy strategy=null;

    public ViewCanvas() {
        //setBounds(0,0,300,200);
        setIgnoreRepaint(true);
        //requestFocus();

    }
    
    public void initStrategy() {
	createBufferStrategy(2);
	this.strategy = getBufferStrategy();
       
    }
    
    public BufferStrategy getStrategy() {
        return strategy;
    }
}
