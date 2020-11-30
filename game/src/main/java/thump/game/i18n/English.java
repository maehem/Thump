/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.i18n;

/**
 *
 * @author mark
 */
public class English {
    public static final String D_DEVSTR =	"Development mode ON.";
    public static final String D_CDROM =        "CD-ROM Version: default.cfg from c:\\doomdata";

//
//	M_Menu.C
//
    public static final String PRESSKEY =	"press a key.";
    public static final String PRESSYN =	"press y or n.";
    public static final String QUITMSG =	"are you sure you want to\nquit this great game?";
    public static final String LOADNET =	"you can't do load while in a net game!\n\n" +  PRESSKEY;
    public static final String QLOADNET	=       "you can't quickload during a netgame!\n\n" + PRESSKEY;
    public static final String QSAVESPOT =	"you haven't picked a quicksave slot yet!\n\n" + PRESSKEY;
    public static final String SAVEDEAD =       "you can't save if you aren't playing!\n\n" + PRESSKEY;
    public static final String QSPROMPT =	"quicksave over your game named\n\n'%s'?\n\n" + PRESSYN;
    public static final String QLPROMPT=	"do you want to quickload the game named\n\n'%s'?\n\n" + PRESSYN;

    public static final String NEWGAME =	
        "you can't start a new game\n" +
        "while in a network game.\n\n" + PRESSKEY;

    public static final String NIGHTMARE =	
        "are you sure? this skill level\n" +
        "isn't even remotely fair.\n\n" + PRESSYN;

    public static final String SWSTRING =	
        "this is the shareware version of doom.\n\n" +
        "you need to order the entire trilogy.\n\n" + PRESSKEY;

    public static final String MSGOFF =         "Messages OFF";
    public static final String MSGON =          "Messages ON";
    public static final String NETEND =         "you can't end a netgame!\n\n" + PRESSKEY;
    public static final String ENDGAME =	"are you sure you want to end the game?\n\n" + PRESSYN;

    public static final String DOSY =           "(press y to quit)";

    public static final String DETAILHI=	"High detail";
    public static final String DETAILLO	=       "Low detail";
    public static final String GAMMALVL0=	"Gamma correction OFF";
    public static final String GAMMALVL1=	"Gamma correction level 1";
    public static final String GAMMALVL2=	"Gamma correction level 2";
    public static final String GAMMALVL3=	"Gamma correction level 3";
    public static final String GAMMALVL4=	"Gamma correction level 4";
    public static final String EMPTYSTRING =    "empty slot";

//
//	P_inter.C
//
    public static final String GOTARMOR=	"Picked up the armor.";
    public static final String GOTMEGA=         "Picked up the MegaArmor!";
    public static final String GOTHTHBONUS=	"Picked up a health bonus.";
    public static final String GOTARMBONUS=	"Picked up an armor bonus.";
    public static final String GOTSTIM=         "Picked up a stimpack.";
    public static final String GOTMEDINEED=	"Picked up a medikit that you REALLY need!";
    public static final String GOTMEDIKIT=	"Picked up a medikit.";
    public static final String GOTSUPER=	"Supercharge!";

    public static final String GOTBLUECARD=	"Picked up a blue keycard.";
    public static final String GOTYELWCARD=	"Picked up a yellow keycard.";
    public static final String GOTREDCARD=	"Picked up a red keycard.";
    public static final String GOTBLUESKUL=	"Picked up a blue skull key.";
    public static final String GOTYELWSKUL=	"Picked up a yellow skull key.";
    public static final String GOTREDSKULL=	"Picked up a red skull key.";

    public static final String GOTINVUL=	"Invulnerability!";
    public static final String GOTBERSERK=	"Berserk!";
    public static final String GOTINVIS=	"Partial Invisibility";
    public static final String GOTSUIT=         "Radiation Shielding Suit";
    public static final String GOTMAP	=       "Computer Area Map";
    public static final String GOTVISOR=	"Light Amplification Visor";
    public static final String GOTMSPHERE=	"MegaSphere!";

    public static final String GOTCLIP=         "Picked up a clip.";
    public static final String GOTCLIPBOX=	"Picked up a box of bullets.";
    public static final String GOTROCKET=	"Picked up a rocket.";
    public static final String GOTROCKBOX=	"Picked up a box of rockets.";
    public static final String GOTCELL=         "Picked up an energy cell.";
    public static final String GOTCELLBOX=	"Picked up an energy cell pack.";
    public static final String GOTSHELLS=	"Picked up 4 shotgun shells.";
    public static final String GOTSHELLBOX=	"Picked up a box of shotgun shells.";
    public static final String GOTBACKPACK=	"Picked up a backpack full of ammo!";

    public static final String GOTBFG9000=	"You got the BFG9000!  Oh, yes.";
    public static final String GOTCHAINGUN=	"You got the chaingun!";
    public static final String GOTCHAINSAW=	"A chainsaw!  Find some meat!";
    public static final String GOTLAUNCHER=	"You got the rocket launcher!";
    public static final String GOTPLASMA=	"You got the plasma gun!";
    public static final String GOTSHOTGUN=	"You got the shotgun!";
    public static final String GOTSHOTGUN2=	"You got the super shotgun!";

//
// P_Doors.C
//
    public static final String PD_BLUEO=	"You need a blue key to activate this object";
    public static final String PD_REDO=         "You need a red key to activate this object";
    public static final String PD_YELLOWO=	"You need a yellow key to activate this object";
    public static final String PD_BLUEK=	"You need a blue key to open this door";
    public static final String PD_REDK=         "You need a red key to open this door";
    public static final String PD_YELLOWK=	"You need a yellow key to open this door";

//
//	G_game.C
//
    public static final String GGSAVED=	"game saved.";

//
//	HU_stuff.C
//
    public static final String HUSTR_MSGU=	"[Message unsent]";

    public static final String HUSTR_E1M1=	"E1M1: Hangar";
    public static final String HUSTR_E1M2=	"E1M2: Nuclear Plant";
    public static final String HUSTR_E1M3=	"E1M3: Toxin Refinery";
    public static final String HUSTR_E1M4=	"E1M4: Command Control";
    public static final String HUSTR_E1M5=	"E1M5: Phobos Lab";
    public static final String HUSTR_E1M6=	"E1M6: Central Processing";
    public static final String HUSTR_E1M7=	"E1M7: Computer Station";
    public static final String HUSTR_E1M8=	"E1M8: Phobos Anomaly";
    public static final String HUSTR_E1M9=	"E1M9: Military Base";

    public static final String HUSTR_E2M1=	"E2M1: Deimos Anomaly";
    public static final String HUSTR_E2M2=	"E2M2: Containment Area";
    public static final String HUSTR_E2M3=	"E2M3: Refinery";
    public static final String HUSTR_E2M4=	"E2M4: Deimos Lab";
    public static final String HUSTR_E2M5=	"E2M5: Command Center";
    public static final String HUSTR_E2M6=	"E2M6: Halls of the Damned";
    public static final String HUSTR_E2M7=	"E2M7: Spawning Vats";
    public static final String HUSTR_E2M8=	"E2M8: Tower of Babel";
    public static final String HUSTR_E2M9=	"E2M9: Fortress of Mystery";

    public static final String HUSTR_E3M1=	"E3M1: Hell Keep";
    public static final String HUSTR_E3M2=	"E3M2: Slough of Despair";
    public static final String HUSTR_E3M3=	"E3M3: Pandemonium";
    public static final String HUSTR_E3M4=	"E3M4: House of Pain";
    public static final String HUSTR_E3M5	="E3M5: Unholy Cathedral";
    public static final String HUSTR_E3M6=	"E3M6: Mt. Erebus";
    public static final String HUSTR_E3M7=	"E3M7: Limbo";
    public static final String HUSTR_E3M8=	"E3M8: Dis";
    public static final String HUSTR_E3M9=	"E3M9: Warrens";

    public static final String HUSTR_E4M1=	"E4M1: Hell Beneath";
    public static final String HUSTR_E4M2=	"E4M2: Perfect Hatred";
    public static final String HUSTR_E4M3=	"E4M3: Sever The Wicked";
    public static final String HUSTR_E4M4=	"E4M4: Unruly Evil";
    public static final String HUSTR_E4M5=	"E4M5: They Will Repent";
    public static final String HUSTR_E4M6=	"E4M6: Against Thee Wickedly";
    public static final String HUSTR_E4M7=	"E4M7: And Hell Followed";
    public static final String HUSTR_E4M8=	"E4M8: Unto The Cruel";
    public static final String HUSTR_E4M9=	"E4M9: Fear";

    public static final String HUSTR_1=         "level 1: entryway";
    public static final String HUSTR_2=         "level 2: underhalls";
    public static final String HUSTR_3=         "level 3: the gantlet";
    public static final String HUSTR_4=         "level 4: the focus";
    public static final String HUSTR_5=         "level 5: the waste tunnels";
    public static final String HUSTR_6=         "level 6: the crusher";
    public static final String HUSTR_7=         "level 7: dead simple";
    public static final String HUSTR_8=         "level 8: tricks and traps";
    public static final String HUSTR_9=         "level 9: the pit";
    public static final String HUSTR_10=	"level 10: refueling base";
    public static final String HUSTR_11=	"level 11: 'o' of destruction!";

    public static final String HUSTR_12=	"level 12: the factory";
    public static final String HUSTR_13=	"level 13: downtown";
    public static final String HUSTR_14=	"level 14: the inmost dens";
    public static final String HUSTR_15=	"level 15: industrial zone";
    public static final String HUSTR_16=	"level 16: suburbs";
    public static final String HUSTR_17=	"level 17: tenements";
    public static final String HUSTR_18=	"level 18: the courtyard";
    public static final String HUSTR_19=	"level 19: the citadel";
    public static final String HUSTR_20=	"level 20: gotcha!";

    public static final String HUSTR_21=	"level 21: nirvana";
    public static final String HUSTR_22=	"level 22: the catacombs";
    public static final String HUSTR_23=	"level 23: barrels o' fun";
    public static final String HUSTR_24=	"level 24: the chasm";
    public static final String HUSTR_25=	"level 25: bloodfalls";
    public static final String HUSTR_26=	"level 26: the abandoned mines";
    public static final String HUSTR_27=	"level 27: monster condo";
    public static final String HUSTR_28=	"level 28: the spirit world";
    public static final String HUSTR_29=	"level 29: the living end";
    public static final String HUSTR_30=	"level 30: icon of sin";

    public static final String HUSTR_31=	"level 31: wolfenstein";
    public static final String HUSTR_32=	"level 32: grosse";

    public static final String PHUSTR_1=	"level 1: congo";
    public static final String PHUSTR_2=	"level 2: well of souls";
    public static final String PHUSTR_3=	"level 3: aztec";
    public static final String PHUSTR_4=	"level 4: caged";
    public static final String PHUSTR_5=	"level 5: ghost town";
    public static final String PHUSTR_6=	"level 6: baron's lair";
    public static final String PHUSTR_7=	"level 7: caughtyard";
    public static final String PHUSTR_8=	"level 8: realm";
    public static final String PHUSTR_9=	"level 9: abattoire";
    public static final String PHUSTR_10=	"level 10: onslaught";
    public static final String PHUSTR_11=	"level 11: hunted";

    public static final String PHUSTR_12=	"level 12: speed";
    public static final String PHUSTR_13=	"level 13: the crypt";
    public static final String PHUSTR_14=	"level 14: genesis";
    public static final String PHUSTR_15=	"level 15: the twilight";
    public static final String PHUSTR_16=	"level 16: the omen";
    public static final String PHUSTR_17=	"level 17: compound";
    public static final String PHUSTR_18=	"level 18: neurosphere";
    public static final String PHUSTR_19=	"level 19: nme";
    public static final String PHUSTR_20=	"level 20: the death domain";

    public static final String PHUSTR_21=	"level 21: slayer";
    public static final String PHUSTR_22=	"level 22: impossible mission";
    public static final String PHUSTR_23=	"level 23: tombstone";
    public static final String PHUSTR_24=	"level 24: the final frontier";
    public static final String PHUSTR_25=	"level 25: the temple of darkness";
    public static final String PHUSTR_26=	"level 26: bunker";
    public static final String PHUSTR_27=	"level 27: anti-christ";
    public static final String PHUSTR_28=	"level 28: the sewers";
    public static final String PHUSTR_29=	"level 29: odyssey of noises";
    public static final String PHUSTR_30=	"level 30: the gateway of hell";

    public static final String PHUSTR_31=	"level 31: cyberden";
    public static final String PHUSTR_32=	"level 32: go 2 it";

    public static final String THUSTR_1=	"level 1: system control";
    public static final String THUSTR_2=	"level 2: human bbq";
    public static final String THUSTR_3=	"level 3: power control";
    public static final String THUSTR_4=	"level 4: wormhole";
    public static final String THUSTR_5=	"level 5: hanger";
    public static final String THUSTR_6=	"level 6: open season";
    public static final String THUSTR_7=	"level 7: prison";
    public static final String THUSTR_8=	"level 8: metal";
    public static final String THUSTR_9=	"level 9: stronghold";
    public static final String THUSTR_10=	"level 10: redemption";
    public static final String THUSTR_11=	"level 11: storage facility";

    public static final String THUSTR_12=	"level 12: crater";
    public static final String THUSTR_13=	"level 13: nukage processing";
    public static final String THUSTR_14=	"level 14: steel works";
    public static final String THUSTR_15=	"level 15: dead zone";
    public static final String THUSTR_16=	"level 16: deepest reaches";
    public static final String THUSTR_17=	"level 17: processing area";
    public static final String THUSTR_18=	"level 18: mill";
    public static final String THUSTR_19=	"level 19: shipping/respawning";
    public static final String THUSTR_20=	"level 20: central processing";

    public static final String THUSTR_21=	"level 21: administration center";
    public static final String THUSTR_22=	"level 22: habitat";
    public static final String THUSTR_23=	"level 23: lunar mining project";
    public static final String THUSTR_24=	"level 24: quarry";
    public static final String THUSTR_25=	"level 25: baron's den";
    public static final String THUSTR_26=	"level 26: ballistyx";
    public static final String THUSTR_27=	"level 27: mount pain";
    public static final String THUSTR_28=	"level 28: heck";
    public static final String THUSTR_29=	"level 29: river styx";
    public static final String THUSTR_30=	"level 30: last call";

    public static final String THUSTR_31=	"level 31: pharaoh";
    public static final String THUSTR_32=	"level 32: caribbean";

    public static final String HUSTR_CHATMACRO1=	"I'm ready to kick butt!";
    public static final String HUSTR_CHATMACRO2=	"I'm OK.";
    public static final String HUSTR_CHATMACRO3=	"I'm not looking too good!";
    public static final String HUSTR_CHATMACRO4=	"Help!";
    public static final String HUSTR_CHATMACRO5=	"You suck!";
    public static final String HUSTR_CHATMACRO6=	"Next time, scumbag...";
    public static final String HUSTR_CHATMACRO7=	"Come here!";
    public static final String HUSTR_CHATMACRO8=	"I'll take care of it.";
    public static final String HUSTR_CHATMACRO9=	"Yes";
    public static final String HUSTR_CHATMACRO0=	"No";

    public static final String HUSTR_TALKTOSELF1=	"You mumble to yourself";
    public static final String HUSTR_TALKTOSELF2=	"Who's there?";
    public static final String HUSTR_TALKTOSELF3=	"You scare yourself";
    public static final String HUSTR_TALKTOSELF4=	"You start to rave";
    public static final String HUSTR_TALKTOSELF5=	"You've lost it...";

    public static final String HUSTR_MESSAGESENT=	"[Message Sent]";

// The following should NOT be changed unless it seems
// just AWFULLY necessary

    public static final String HUSTR_PLRGREEN =	"Green: ";
    public static final String HUSTR_PLRINDIGO ="Indigo: ";
    public static final String HUSTR_PLRBROWN =	"Brown: ";
    public static final String HUSTR_PLRRED =   "Red: ";

    public static final char HUSTR_KEYGREEN=	'g';
    public static final char HUSTR_KEYINDIGO=	'i';
    public static final char HUSTR_KEYBROWN=	'b';
    public static final char HUSTR_KEYRED=      'r';

//
//	AM_map.C
//

    public static final String AMSTR_FOLLOWON=	"Follow Mode ON";
    public static final String AMSTR_FOLLOWOFF=	"Follow Mode OFF";

    public static final String AMSTR_GRIDON=	"Grid ON";
    public static final String AMSTR_GRIDOFF=	"Grid OFF";

    public static final String AMSTR_MARKEDSPOT=	"Marked Spot";
    public static final String AMSTR_MARKSCLEARED=	"All Marks Cleared";

//
//	ST_stuff.C
//

    public static final String STSTR_MUS=		"Music Change";
    public static final String STSTR_NOMUS=		"IMPOSSIBLE SELECTION";
    public static final String STSTR_DQDON=		"Degreelessness Mode On";
    public static final String STSTR_DQDOFF=	"Degreelessness Mode Off";

    public static final String STSTR_KFAADDED=	"Very Happy Ammo Added";
    public static final String STSTR_FAADDED=	"Ammo (no keys) Added";

    public static final String STSTR_NCON	=	"No Clipping Mode ON";
    public static final String STSTR_NCOFF=		"No Clipping Mode OFF";

    public static final String STSTR_BEHOLD=	"inVuln, Str, Inviso, Rad, Allmap, or Lite-amp";
    public static final String STSTR_BEHOLDX=	"Power-up Toggled";

    public static final String STSTR_CHOPPERS=	"... doesn't suck - GM";
    public static final String STSTR_CLEV=		"Changing Level...";

//
//	F_Finale.C
//
    public static final String E1TEXT= 
"Once you beat the big badasses and\n" +
"clean out the moon base you're supposed\n" +
"to win, aren't you? Aren't you? Where's\n" +
"your fat reward and ticket home? What\n" +
"the hell is this? It's not supposed to\n" +
"end this way!\n" +
"\n" +
"It stinks like rotten meat, but looks\n" +
"like the lost Deimos base.  Looks like\n" +
"you're stuck on The Shores of Hell.\n" +
"The only way out is through.\n" +
" \n" +
"To continue the DOOM experience, play\n" +
"The Shores of Hell and its amazing\n" +
"sequel, Inferno!\n";


    public static final String E2TEXT= 
"You've done it! The hideous cyber-\n" +
"demon lord that ruled the lost Deimos\n" +
"moon base has been slain and you\n" +
"are triumphant! But ... where are\n" +
"you? You clamber to the edge of the\n" +
"moon and look down to see the awful\n" +
"truth.\n" +
"\n" +
"Deimos floats above Hell itself!\n" +
"You've never heard of anyone escaping\n" +
"from Hell, but you'll make the bastards\n" +
"sorry they ever heard of you! Quickly,\n" +
"you rappel down to  the surface of\n" +
"Hell.\n" +
" \n" +
"Now, it's on to the final chapter of\n" +
"DOOM! -- Inferno.";


    public static final String E3TEXT= 
"The loathsome spiderdemon that\n" +
"masterminded the invasion of the moon\n" +
"bases and caused so much death has had\n" +
"its ass kicked for all time.\n" +
"\n" +
"A hidden doorway opens and you enter.\n" +
"You've proven too tough for Hell to\n" +
"contain, and now Hell at last plays\n" +
"fair -- for you emerge from the door\n" +
"to see the green fields of Earth!\n" +
"Home at last.\n" +
" \n" +
"You wonder what's been happening on\n" +
"Earth while you were battling evil\n" +
"unleashed. It's good that no Hell-\n" +
"spawn could have come through that\n" +
"door with you ...";


    public static final String E4TEXT= 
"the spider mastermind must have sent forth\n" +
"its legions of hellspawn before your\n" +
"final confrontation with that terrible\n" +
"beast from hell.  but you stepped forward\n" +
"and brought forth eternal damnation and\n" +
"suffering upon the horde as a true hero\n" +
"would in the face of something so evil.\n" +
" \n" +
"besides, someone was gonna pay for what\n" +
"happened to daisy, your pet rabbit.\n" +
" \n" +
"but now, you see spread before you more\n" +
"potential pain and gibbitude as a nation\n" +
"of demons run amok among our cities.\n" +
" \n" +
"next stop, hell on earth!";


// after level 6, put this:

    public static final String C1TEXT= 
"YOU HAVE ENTERED DEEPLY INTO THE INFESTED\n" +
"STARPORT. BUT SOMETHING IS WRONG. THE\n" +
"MONSTERS HAVE BROUGHT THEIR OWN REALITY\n" +
"WITH THEM, AND THE STARPORT'S TECHNOLOGY\n" +
"IS BEING SUBVERTED BY THEIR PRESENCE.\n" +
" \n" +
"AHEAD, YOU SEE AN OUTPOST OF HELL, A\n" +
"FORTIFIED ZONE. IF YOU CAN GET PAST IT,\n" +
"YOU CAN PENETRATE INTO THE HAUNTED HEART\n" +
"OF THE STARBASE AND FIND THE CONTROLLING\n" +
"SWITCH WHICH HOLDS EARTH'S POPULATION\n" +
"HOSTAGE.";

// After level 11, put this:

    public static final String C2TEXT= 
"YOU HAVE WON! YOUR VICTORY HAS ENABLED\n" +
"HUMANKIND TO EVACUATE EARTH AND ESCAPE\n" +
"THE NIGHTMARE.  NOW YOU ARE THE ONLY\n" +
"HUMAN LEFT ON THE FACE OF THE PLANET.\n" +
"CANNIBAL MUTATIONS, CARNIVOROUS ALIENS,\n" +
"AND EVIL SPIRITS ARE YOUR ONLY NEIGHBORS.\n" +
"YOU SIT BACK AND WAIT FOR DEATH, CONTENT\n" +
"THAT YOU HAVE SAVED YOUR SPECIES.\n" +
" \n" +
"BUT THEN, EARTH CONTROL BEAMS DOWN A\n" +
"MESSAGE FROM SPACE: \"SENSORS HAVE LOCATED\n" +
"THE SOURCE OF THE ALIEN INVASION. IF YOU\n" +
"GO THERE, YOU MAY BE ABLE TO BLOCK THEIR\n" +
"ENTRY.  THE ALIEN BASE IS IN THE HEART OF\n" +
"YOUR OWN HOME CITY, NOT FAR FROM THE\n" +
"STARPORT.\" SLOWLY AND PAINFULLY YOU GET\n" +
"UP AND RETURN TO THE FRAY.";


// After level 20, put this:

    public static final String C3TEXT= 
"YOU ARE AT THE CORRUPT HEART OF THE CITY,\n" +
"SURROUNDED BY THE CORPSES OF YOUR ENEMIES.\n" +
"YOU SEE NO WAY TO DESTROY THE CREATURES'\n" +
"ENTRYWAY ON THIS SIDE, SO YOU CLENCH YOUR\n" +
"TEETH AND PLUNGE THROUGH IT.\n" +
" \n" +
"THERE MUST BE A WAY TO CLOSE IT ON THE\n" +
"OTHER SIDE. WHAT DO YOU CARE IF YOU'VE\n" +
"GOT TO GO THROUGH HELL TO GET TO IT?";


// After level 29, put this:

    public static final String C4TEXT= 
"THE HORRENDOUS VISAGE OF THE BIGGEST\n" +
"DEMON YOU'VE EVER SEEN CRUMBLES BEFORE\n" +
"YOU, AFTER YOU PUMP YOUR ROCKETS INTO\n" +
"HIS EXPOSED BRAIN. THE MONSTER SHRIVELS\n" +
"UP AND DIES, ITS THRASHING LIMBS\n" +
"DEVASTATING UNTOLD MILES OF HELL'S\n" +
"SURFACE.\n" +
" \n" +
"YOU'VE DONE IT. THE INVASION IS OVER.\n" +
"EARTH IS SAVED. HELL IS A WRECK. YOU\n" +
"WONDER WHERE BAD FOLKS WILL GO WHEN THEY\n" +
"DIE, NOW. WIPING THE SWEAT FROM YOUR\n" +
"FOREHEAD YOU BEGIN THE LONG TREK BACK\n" +
"HOME. REBUILDING EARTH OUGHT TO BE A\n" +
"LOT MORE FUN THAN RUINING IT WAS.\n";



// Before level 31, put this:

    public static final String C5TEXT= 
"CONGRATULATIONS, YOU'VE FOUND THE SECRET\n" +
"LEVEL! LOOKS LIKE IT'S BEEN BUILT BY\n" +
"HUMANS, RATHER THAN DEMONS. YOU WONDER\n" +
"WHO THE INMATES OF THIS CORNER OF HELL\n" +
"WILL BE.";


// Before level 32, put this:

    public static final String C6TEXT= 
"CONGRATULATIONS, YOU'VE FOUND THE\n" +
"SUPER SECRET LEVEL!  YOU'D BETTER\n" +
"BLAZE THROUGH THIS ONE!\n";


// after map 06	

    public static final String P1TEXT=  
"You gloat over the steaming carcass of the\n" +
"Guardian.  With its death, you've wrested\n" +
"the Accelerator from the stinking claws\n" +
"of Hell.  You relax and glance around the\n" +
"room.  Damn!  There was supposed to be at\n" +
"least one working prototype, but you can't\n" +
"see it. The demons must have taken it.\n" +
"\n" +
"You must find the prototype, or all your\n" +
"struggles will have been wasted. Keep\n" +
"moving, keep fighting, keep killing.\n" +
"Oh yes, keep living, too.";


// after map 11

    public static final String P2TEXT= 
"Even the deadly Arch-Vile labyrinth could\n" +
"not stop you, and you've gotten to the\n" +
"prototype Accelerator which is soon\n" +
"efficiently and permanently deactivated.\n" +
"\n" +
"You're good at that kind of thing.";


// after map 20

    public static final String P3TEXT= 
"You've bashed and battered your way into\n" +
"the heart of the devil-hive.  Time for a\n" +
"Search-and-Destroy mission, aimed at the\n" +
"Gatekeeper, whose foul offspring is\n" +
"cascading to Earth.  Yeah, he's bad. But\n" +
"you know who's worse!\n" +
" \n" +
"Grinning evilly, you check your gear, and\n" +
"get ready to give the bastard a little Hell\n" +
"of your own making!";

// after map 30

    public static final String P4TEXT= 
"The Gatekeeper's evil face is splattered\n" +
"all over the place.  As its tattered corpse\n" +
"collapses, an inverted Gate forms and\n" +
"sucks down the shards of the last\n" +
"prototype Accelerator, not to mention the\n" +
"few remaining demons.  You're done. Hell\n" +
"has gone back to pounding bad dead folks \n" +
"instead of good live ones.  Remember to\n" +
"tell your grandkids to put a rocket\n" +
"launcher in your coffin. If you go to Hell\n" +
"when you die, you'll need it for some\n" +
"final cleaning-up ...";

// before map 31

    public static final String P5TEXT= 
"You've found the second-hardest level we\n" +
"got. Hope you have a saved game a level or\n" +
"two previous.  If not, be prepared to die\n" +
"aplenty. For master marines only.";

// before map 32

    public static final String P6TEXT= 
"Betcha wondered just what WAS the hardest\n" +
"level we had ready for ya?  Now you know.\n" +
"No one gets out alive.";


    public static final String T1TEXT= 
"You've fought your way out of the infested\n" +
"experimental labs.   It seems that UAC has\n" +
"once again gulped it down.  With their\n" +
"high turnover, it must be hard for poor\n" +
"old UAC to buy corporate health insurance\n" +
"nowadays..\n" +
" +n" +
"Ahead lies the military complex, now\n" +
"swarming with diseased horrors hot to get\n" +
"their teeth into you. With luck, the\n" +
"complex still has some warlike ordnance\n" +
"laying around.";


    public static final String T2TEXT= 
"You hear the grinding of heavy machinery\n" +
"ahead.  You sure hope they're not stamping\n" +
"out new hellspawn, but you're ready to\n" +
"ream out a whole herd if you have to.\n" +
"They might be planning a blood feast, but\n" +
"you feel about as mean as two thousand\n" +
"maniacs packed into one mad killer.\n" +
" \n" +
"You don't plan to go down easy.";


    public static final String T3TEXT= 
"The vista opening ahead looks real damn\n" +
"familiar. Smells familiar, too -- like\n" +
"fried excrement. You didn't like this\n" +
"place before, and you sure as hell ain't\n" +
"planning to like it now. The more you\n" +
"brood on it, the madder you get.\n" +
"Hefting your gun, an evil grin trickles\n" +
"onto your face. Time to take some names.";

    public static final String T4TEXT= 
"Suddenly, all is silent, from one horizon\n" +
"to the other. The agonizing echo of Hell\n" +
"fades away, the nightmare sky turns to\n" +
"blue, the heaps of monster corpses start \n" +
"to evaporate along with the evil stench \n" +
"that filled the air. Jeeze, maybe you've\n" +
"done it. Have you really won?\n" +
" \n" +
"Something rumbles in the distance.\n" +
"A blue light begins to glow inside the\n" +
"ruined skull of the demon-spitter.";


    public static final String T5TEXT= 
"What now? Looks totally different. Kind\n" +
"of like King Tut's condo. Well,\n" +
"whatever's here can't be any worse\n" +
"than usual. Can it?  Or maybe it's best\n" +
"to let sleeping gods lie..";


    public static final String T6TEXT= 
"Time for a vacation. You've burst the\n" +
"bowels of hell and by golly you're ready\n" +
"for a break. You mutter to yourself,\n" +
"Maybe someone else can kick Hell's ass\n" +
"next time around. Ahead lies a quiet town,\n" +
"with peaceful flowing water, quaint\n" +
"buildings, and presumably no Hellspawn.\n" +
" \n" +
"As you step off the transport, you hear\n" +
"the stomp of a cyberdemon's iron shoe.";

    
  


//
// Character cast strings F_FINALE.C
//
    public static final String CC_ZOMBIE=	"ZOMBIEMAN";
    public static final String CC_SHOTGUN=	"SHOTGUN GUY";
    public static final String CC_HEAVY	="HEAVY WEAPON DUDE";
    public static final String CC_IMP=	"IMP";
    public static final String CC_DEMON=	"DEMON";
    public static final String CC_LOST=	"LOST SOUL";
    public static final String CC_CACO=	"CACODEMON";
    public static final String CC_HELL=	"HELL KNIGHT";
    public static final String CC_BARON=	"BARON OF HELL";
    public static final String CC_ARACH=	"ARACHNOTRON";
    public static final String CC_PAIN	="PAIN ELEMENTAL";
    public static final String CC_REVEN=	"REVENANT";
    public static final String CC_MANCU=	"MANCUBUS";
    public static final String CC_ARCH=	"ARCH-VILE";
    public static final String CC_SPIDER=	"THE SPIDER MASTERMIND";
    public static final String CC_CYBER=	"THE CYBERDEMON";
    public static final String CC_HERO=	"OUR HERO";
}
