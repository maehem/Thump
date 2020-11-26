/*
 * System Interface   --   Doom C - i_system.c
 */
package thump.system;

import java.util.logging.Level;
import static thump.base.Defines.logger;
import thump.game.Defines;
import thump.game.Game;
import thump.game.TickCommand;

/**
 *
 * @author mark
 */
public class SystemInterface {

    private static SystemInterface instance = null;

    int mb_used = 6;
    TickCommand emptycmd = new TickCommand();

    private SystemInterface() {
    }

    public static SystemInterface getInstance() {
        if (instance == null) {
            instance = new SystemInterface();
        }

        return instance;
    }

    public void I_Tactile(int on, int off, int total) {
        // UNUSED.
        //on = off = total = 0;
    }

    public TickCommand I_BaseTiccmd() {
        return emptycmd;
    }

//    public int I_GetHeapSize() {
//        return mb_used * 1024 * 1024;
//    }

//    public byte[] I_ZoneBase(/*int size*/) {
//        //size = mb_used*1024*1024;
//        //return (byte *) malloc (*size);
//        return new byte[mb_used * 1024 * 1024];  // Calling routine can get the size from byte array size.
//    }

    private long basetime = 0;

    /**
     * I_GetTime
     *
     * @return time in 1/70th second tics
     */
    public long I_GetTime() {
//        struct timeval	tp;
//        struct timezone	tzp;
        long newtics = 0;

        long currentTimeMillis = System.currentTimeMillis();
//        gettimeofday(&tp, &tzp);

//        if (basetime == 0)
//            basetime = tp.tv_sec;
        if (basetime == 0) {
            basetime = currentTimeMillis;
        } else {
            newtics = (currentTimeMillis - basetime) / Defines.TICRATE;
        }

//        newtics = (tp.tv_sec-basetime)*TICRATE + tp.tv_usec*TICRATE/1000000;
        return newtics;
    }

    /**
     * I_Init
     */
    public void I_Init() {
        Game.getInstance().sound.soundInterface.I_InitSound();
        //  I_InitGraphics(); // Don't use!
    }

    //
    // I_Quit
    //
    public void I_Quit() {
        Game game = Game.getInstance();
        game.net.D_QuitNetGame();
        game.sound.soundInterface.I_ShutdownSound();
        game.sound.soundInterface.I_ShutdownMusic();
        game.mainMenu.saveDefaults();
        game.videoInterface.I_ShutdownGraphics();
        System.exit(0);
    }

    public void I_WaitVBL(int count) {
        try {
            Thread.sleep(count * 1000 / 70);
        } catch (Exception e) {
        }

        //usleep (count * (1000000/70) );                                
    }

//    private void I_BeginRead() {
//    }   // Not used.  Delete me in v1.1
//
//    private void I_EndRead() {
//    }     // Not used.  Delete me in v1.1

    // TODO Used by Video, but probably obsolete.
//    public static byte[] I_AllocLow(int length) {
//        return new byte[length];
//    }

    /**
     * I_Error
     *
     * @param error message to print
     * @param items Logger style data items
     */
//    void I_Error (char *error, ...) {
    public static void I_Error(String error, Object[] items) {  // Uses same args as Logger.log(String, Object[])
        logger.log(Level.SEVERE, error, items);

        // Shutdown. Here might be other errors.
        if (Game.getInstance().demorecording) {
            Game.getInstance().G_CheckDemoStatus();
        }

        Game.getInstance().net.D_QuitNetGame();
        Game.getInstance().videoInterface.I_ShutdownGraphics();

        // Tell DoomMain to shutdown?
        // exit(-1);
    }

    public static void I_Error(String error) {  // Uses same args as Logger.log(String, Object[])
        I_Error(error, new Object[]{});
    }
}
