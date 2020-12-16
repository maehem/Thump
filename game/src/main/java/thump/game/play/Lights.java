/*
 *  Handle Sector base lighting effects.
 *  Muzzle flash?
 */
package thump.game.play;

import thump.game.Game;
import thump.game.maplevel.MapSector;
import static thump.game.play.SpecialEffects.GLOWSPEED;
import static thump.game.play.SpecialEffects.SLOWDARK;
import static thump.game.play.SpecialEffects.STROBEBRIGHT;
import thump.game.thinkeraction.T_FireFlicker;
import thump.game.thinkeraction.T_Glow;
import thump.game.thinkeraction.T_LightFlash;
import thump.game.thinkeraction.T_StrobeFlash;
import thump.wad.map.Line;
import thump.wad.map.Sector;

/**
 *
 * @author mark
 */
public class Lights {

    //
    // T_FireFlicker
    //
    public static void T_FireFlicker(FireFlicker flick) {
        int amount;

        flick.count--;
        if (flick.count > 0) {
            return;
        }

        amount = (Random.getInstance().P_Random() & 3) * 16;

        if (flick.sector.lightlevel - amount < flick.minlight) {
            flick.sector.lightlevel = flick.minlight;
        } else {
            flick.sector.lightlevel = flick.maxlight - amount;
        }

        flick.count = 4;
    }



    //
    // P_SpawnFireFlicker
    //
    public static void P_SpawnFireFlicker(Sector sector) {
        FireFlicker	flick;

        // Note that we are resetting sector attributes.
        // Nothing special about it during gameplay.
        sector.special = 0; 

        //flick = Z_Malloc ( sizeof(*flick), PU_LEVSPEC, 0);
        flick = new FireFlicker();

        Tick.P_AddThinker (flick);

        flick.setFunction (new T_FireFlicker());
        flick.sector = sector;
        flick.maxlight = sector.lightlevel;
        flick.minlight = SpecialEffects.P_FindMinSurroundingLight(sector,sector.lightlevel)+16;
        flick.count = 4;
    }



    //
    // BROKEN LIGHT FLASHING
    //


    //
    // T_LightFlash
    // Do flashing lights.
    //
    public static void T_LightFlash(LightFlash flash) {
        flash.count--;
        if (flash.count > 0) {
            return;
        }

        if (flash.sector.lightlevel == flash.maxlight) {
            flash.sector.lightlevel = flash.minlight;
            flash.count = (Random.getInstance().P_Random() & flash.mintime) + 1;
        } else {
            flash.sector.lightlevel = flash.maxlight;
            flash.count = (Random.getInstance().P_Random() & flash.maxtime) + 1;
        }

    }


    //
    // P_SpawnLightFlash
    // After the map has been loaded, scan each sector
    // for specials that spawn thinkers
    //
    public static void P_SpawnLightFlash (Sector sector) {
        LightFlash	flash;

        // nothing special about it during gameplay
        sector.special = 0;	

        //flash = Z_Malloc ( sizeof(*flash), PU_LEVSPEC, 0);
        flash = new LightFlash();

        Tick.P_AddThinker (flash);

        flash.setFunction(new T_LightFlash());
        flash.sector = sector;
        flash.maxlight = sector.lightlevel;

        flash.minlight = SpecialEffects.P_FindMinSurroundingLight(sector,sector.lightlevel);
        flash.maxtime = 64;
        flash.mintime = 7;
        flash.count = (Random.getInstance().P_Random()&flash.maxtime)+1;
    }


    //
    // T_StrobeFlash
    //
    public static void T_StrobeFlash (Strobe flash) {
        flash.count--;
        if (flash.count>0) {
            return;
        }

        if (flash.sector.lightlevel == flash.minlight) {
            flash.sector.lightlevel = flash.maxlight;
            flash.count = flash.brighttime;
        } else {
            flash.sector.lightlevel = flash.minlight;
            flash.count =flash.darktime;
        }

    }



    //
    // P_SpawnStrobeFlash
    // After the map has been loaded, scan each sector
    // for specials that spawn thinkers
    //
    public static void P_SpawnStrobeFlash(Sector sector, int fastOrSlow, int inSync) {
        Strobe flash;

        //flash = Z_Malloc ( sizeof(*flash), PU_LEVSPEC, 0);
        flash = new Strobe();

        Tick.P_AddThinker(flash);

        flash.sector = sector;
        flash.darktime = fastOrSlow;
        flash.brighttime = STROBEBRIGHT;
        flash.setFunction(new T_StrobeFlash());
        flash.maxlight = sector.lightlevel;
        flash.minlight = SpecialEffects.P_FindMinSurroundingLight(sector, sector.lightlevel);

        if (flash.minlight == flash.maxlight) {
            flash.minlight = 0;
        }

        // nothing special about it during gameplay
        sector.special = 0;

        if (0 == inSync) {
            flash.count = (Random.getInstance().P_Random() & 7) + 1;
        } else {
            flash.count = 1;
        }
    }


    //
    // Start strobing lights (usually from a trigger)
    //
    public static void EV_StartLightStrobing(Line line) {
        //MapSector	sec;
        Sector	sec;

        int secnum = -1;
        while ((secnum = SpecialEffects.P_FindSectorFromLineTag(line,secnum)) >= 0) {
            //sec = Game.getInstance().playerSetup.sectors[secnum];
            sec = Game.getInstance().playerSetup.sectors.get(secnum).sector;
            if (sec.specialdata!=null) {
                continue;
            }

            P_SpawnStrobeFlash (sec,SLOWDARK, 0);
        }
    }


    //
    // TURN LINE'S TAG LIGHTS OFF
    //
    public static void EV_TurnTagLightsOff(Line line) {
        int min;
        //Sector		sector;
        Sector tsec;
        Line templine;

        //sector = sectors;
        //for (j = 0;j < numsectors; j++, sector++) {
        for (MapSector ms : Game.getInstance().playerSetup.sectors) {
            Sector sector = ms.sector;
            
            if (sector.tag == line.tag) {
                min = sector.lightlevel;
                for (int i = 0; i < sector.linecount; i++) {
                    templine = sector.lines[i];
                    tsec = SpecialEffects.getNextSector(templine, sector);
                    if (null == tsec) {
                        continue;
                    }
                    if (tsec.lightlevel < min) {
                        min = tsec.lightlevel;
                    }
                }
                sector.lightlevel = min;
            }
        }
    }


    //
    // TURN LINE'S TAG LIGHTS ON
    //
    public static void EV_LightTurnOn(Line line, int _bright) {
        int bright = _bright;

        //Sector sector;
        Sector temp;
        Line templine;

        //sector = Game.getInstance().playerSetup.sectors;

        //for (i = 0; i < numsectors; i++, sector++) {
        for ( MapSector ms: Game.getInstance().playerSetup.sectors) {
            Sector sector = ms.sector;
            
            if (sector.tag == line.tag) {
                // bright = 0 means to search
                // for highest light level
                // surrounding sector
                if (0==bright) {
                    for (int j = 0; j < sector.linecount; j++) {
                        templine = sector.lines[j];
                        temp = SpecialEffects.getNextSector(templine, sector);

                        if (null==temp) {
                            continue;
                        }

                        if (temp.lightlevel > bright) {
                            bright = temp.lightlevel;
                        }
                    }
                }
                sector.lightlevel = bright;
            }
        }
    }


    //
    // Spawn glowing light
    //
    // TODO Move to Glow.java as doGlow() method.
    public static void T_Glow(Glow g) {
        switch (g.direction) {
            case -1:
                // DOWN
                g.sector.lightlevel -= GLOWSPEED;
                if (g.sector.lightlevel <= g.minlight) {
                    g.sector.lightlevel += GLOWSPEED;
                    g.direction = 1;
                }
                break;

            case 1:
                // UP
                g.sector.lightlevel += GLOWSPEED;
                if (g.sector.lightlevel >= g.maxlight) {
                    g.sector.lightlevel -= GLOWSPEED;
                    g.direction = -1;
                }
                break;
        }
    }


    public static void P_SpawnGlowingLight(Sector sector) {
        Glow	g;

        //g = Z_Malloc( sizeof(*g), PU_LEVSPEC, 0);
        g = new Glow();

        Tick.P_AddThinker(g);

        g.sector = sector;
        g.minlight = SpecialEffects.P_FindMinSurroundingLight(sector,sector.lightlevel);
        g.maxlight = sector.lightlevel;
        g.setFunction( new T_Glow() );
        g.direction = -1;

        sector.special = 0;
    }


  
}
