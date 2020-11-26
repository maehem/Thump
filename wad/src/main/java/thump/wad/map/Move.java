/*
        (2) The player-move data is recorded in 4-byte chunks. Every 1/35 of a
        second is a gametic, and for every gametic, there is one 4-byte chunk
        per player. So the time duration of a demo (in seconds) is approximately
        equal to its length in bytes divided by (140 * number_of_players).

          The four bytes recording each player's actions are:

          (a) Forward/Backward Movement.
          (b) Strafe Right/Left Movement.
          (c) Turn Left/Right.
          (d) other actions - use/activate, fire, change weapons.

          The first three are signed bytes (i.e. of type <char>).

          (a) Ranges from -127 to 127, negative numbers are backward movement,
              positive numbers are forward movement. Without the -turbo option
              above 100, values outside -50..50 cannot be achieved. With a
              keyboard or joystick, these are the regular values:

              Move forward:   25 (0x19)   with Speed on:  50 (0x32)
              Move backward: -25 (0xE7)   with Speed on: -50 (0xCE)

              Fancy mouse use can achieve any number in the range.

          (b) Ranges from -127 to 127, negative numbers are left-strafe movement,
              positive numbers are right-strafe movement. The keyboard values are:

              Strafe right: 24  (0x18)    with Speed on:  50 (0x32)
              Strafe left: -24  (0xE8)    with Speed on: -50 (0xCE)

          (c) Ranges from -127 to 127, negative numbers are right turns, positive
              numbers are left turns. The keyboard values vary from version to
              version, but are all in the range -5..5, and that's with Speed on.

              Using the mouse can achieve much higher numbers here. I doubt if
              the maximums of 127 and -127 can actually be achieved in play,
              though.

          (d) the bits of this byte indicate what actions the player is engaged in:

              bit 0     Fire current weapon
              bit 1     Use (a switch, open a door, etc.)
              bit 2     Change weapon to the one indicated in bits 3-5:

              bits 5-3 = 000 Fist or Chainsaw
                         001 Pistol
                         010 Shotgun
                         011 Chaingun
                         100 Rocket Launcher
                         101 Plasma Rifle
                         110 BFG 9000
                         111 Super Shotgun (DOOM 2 only)

              bit 6     unused
              bit 7     indicates a special action which alters the meanings
                          of the other bits:

                        bits 1-0 = 01 pause or unpause
                                 = 10 save game in slot # recorded in bits 4 to 2
                                        (slot number can thus be 0 to 7 but
                                         should NOT be 6 or 7 or else!)

          There might be other special actions. The save game action happens
        during replay of the demo, so be careful when playing demos if you
        have important savegames! One or more of them could conceivably get
        overwritten.
 */
package thump.wad.map;

/**
 *
 * @author mark
 */
public class Move {
    public final byte fwdBack;
    public final byte strafe;    
    public final byte turn;
    public final byte actions;
    
    public Move( int mov ) {
        fwdBack = (byte)( mov & 0x000000FF);
        strafe  = (byte)((mov & 0x0000FF00)>>8);
        turn    = (byte)((mov & 0x00FF0000)>>16);
        actions = (byte)((mov & 0xFF000000)>>24);
    }

    @Override
    public String toString() {
        return  String.format("%02X", fwdBack & 0xFF) + 
                String.format("%02X", strafe & 0xFF) +
                String.format("%02X", turn & 0xFF) +
                String.format("%02X", actions & 0xFF)     ;

        //return "    fb:" + fwdBack + "   strafe:" + strafe + "    turn:" + turn + "    actions:" + actions;
    }
    
    
}
