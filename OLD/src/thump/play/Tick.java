/*
 * Thinking for Player and Monsters
 */
package thump.play;

import thump.game.Game;
import thump.game.Thinker;
import static thump.global.Defines.MAXPLAYERS;
import thump.menu.MenuManager;

/**
 *
 * @author mark
 */
public class Tick {
    
    
    // Moved to Game.java
    
//    int	leveltime;
//
//    //
//    // THINKERS
//    // All thinkers should be allocated by Z_Malloc
//    // so they can be operated on uniformly.
//    // The actual structures will vary in size,
//    // but the first element must be thinker_t.
//    //
//
//
//
//    // Both the head and tail of the thinker list.
//    Thinker	thinkercap;


    //
    // P_InitThinkers
    //
    public static void P_InitThinkers () {
        Game game = Game.getInstance();
        game.thinkercap = new DummyThinker();
        game.thinkercap.setPrevThinker( game.thinkercap );
        game.thinkercap.setNextThinker( game.thinkercap );
    }



    //
    // P_AddThinker
    // Adds a new thinker at the end of the list.
    //
    public static void P_AddThinker (Thinker thinker) {
        Game game = Game.getInstance();
        game.thinkercap.getPrevThinker().setNextThinker(thinker);
        thinker.setNextThinker(game.thinkercap);
        thinker.setPrevThinker(game.thinkercap.getPrevThinker());
        game.thinkercap.setPrevThinker(thinker);
    }



    //
    // P_RemoveThinker
    // Deallocation is lazy -- it will not actually be freed
    // until its thinking turn comes up.
    //
    public static void P_RemoveThinker(Thinker thinker) {
        // FIXME: NOP.
        thinker.setFunction(null);
    }

    //
    // P_AllocateThinker
    // Allocates memory and adds a new thinker at the end of the list.
    //
    public static void P_AllocateThinker (Thinker thinker) {
        // LOLz
    }

    //
    // P_RunThinkers
    //
    public static void P_RunThinkers () {
        Thinker	currentthinker;

        currentthinker = Game.getInstance().thinkercap.getNextThinker();
        while (currentthinker != Game.getInstance().thinkercap)
        {
            if ( currentthinker.getFunction() == null )
            {
                // time to remove it
                currentthinker.getNextThinker().setPrevThinker(currentthinker.getPrevThinker());
                currentthinker.getPrevThinker().setNextThinker(currentthinker.getNextThinker());
                //Z_Free (currentthinker);
            } else {
                    currentthinker.getFunction().doAction(currentthinker);
            }
            currentthinker = currentthinker.getNextThinker();
        }
    }


    //
    // P_Ticker
    //
    public static void P_Ticker () {
        int i;

        // run the tic
        if (Game.getInstance().paused) {
            return;
        }

        // pause if in menu and at least one tic has been run
        if ( !Game.getInstance().netgame
             && MenuManager.getInstance().menuactive
             && !Game.getInstance().demoplayback
             && Game.getInstance().players[Game.getInstance().consoleplayer].viewz != 1)
        {
            return;
        }


        for (i=0 ; i<MAXPLAYERS ; i++) {
            if (Game.getInstance().playeringame[i]) {
                Game.getInstance().playerSetup.user.P_PlayerThink (Game.getInstance().players[i]);
            }
        }

        P_RunThinkers ();
        Game.getInstance().playerSetup.effects.P_UpdateSpecials ();
        Game.getInstance().movingObject.P_RespawnSpecials ();

        // for par times
        Game.getInstance().leveltime++;	
    }


}
