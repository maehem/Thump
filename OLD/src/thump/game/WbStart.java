/*
 * WbStart
 */
package thump.game;

import thump.global.Defines;

/**
 *
 * @author mark
 */
public class WbStart {

    public int epsd;	// episode # (0-2)

    // if true, splash the secret level
    public boolean didsecret;

    // previous and next levels, origin 0
    public int last;
    public int next;

    public int maxkills;
    public int maxitems;
    public int maxsecret;
    public int maxfrags;

    // the par time
    public int partime;

    // index of this player in game
    public int pnum;

    public WbPlayer plyr[] = new WbPlayer[Defines.MAXPLAYERS];

    WbStart copy() {
        WbStart copy = new WbStart();
        copy.epsd = epsd;
        copy.didsecret = didsecret;
        copy.last = last;
        copy.next = next;
        copy.maxkills = maxkills;
        copy.maxitems = maxitems;
        copy.maxsecret = maxsecret;
        copy.maxfrags = maxfrags;
        copy.partime = partime;
        copy.pnum = pnum;
        
        for ( int i=0; i<plyr.length; i++ ) {
            copy.plyr[i] = plyr[i].copy();
        }
        
        return copy;
    }

}
