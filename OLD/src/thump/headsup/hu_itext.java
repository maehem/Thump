/*
 * Input Text Line Widget
 * (child of Text Line widget)
 */
package thump.headsup;

/**
 *
 * @author mark
 */
public class hu_itext {
    public hu_textline l=null;       // text line to input on
    public int         lm=0;      // left margin past which I am not to delete characters
    public Boolean     on=false;      // pointer to boolean stating whether to update window
    public boolean     laston=false;  // last value of *->on;   
}
