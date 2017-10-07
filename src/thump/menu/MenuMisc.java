/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.menu;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Properties;
import thump.game.Game;
import static thump.global.Defines.SCREENWIDTH;
import static thump.headsup.Stuff.HU_FONTSIZE;
import static thump.headsup.Stuff.HU_FONTSTART;
import thump.render.Patch;

/**
 *
 * @author mark
 */
public class MenuMisc {
    
    public final static String PROPK_MOUSE_SENS = "mouse_sensitivity";
    public final static String PROPK_SFX_VOL = "sfx_volume";
    public final static String PROPK_MUS_VOL = "music_volume";
    public final static String PROPK_SHOW_MSG = "show_messages";
    public final static String PROPK_SCREENBLK = "screenblocks";
    public final static String PROPK_DETAIL = "detaillevel";
    //public final static String PROPK_SND_CHNLS = "snd_channels";
    public final static String PROPK_USEGAMMA = "usegamma";
    
    public final Properties properties = new Properties();

    public MenuMisc() {    
    
        properties.put( PROPK_MOUSE_SENS,/*&mouseSensitivity,*/ "5");
        properties.put( PROPK_SFX_VOL,/*&snd_SfxVolume,*/"8");
        properties.put( PROPK_MUS_VOL,/*&snd_MusicVolume,*/"8");
        properties.put( PROPK_SHOW_MSG,/*&showMessages,*/"1");
        properties.put( PROPK_SCREENBLK,/*&screenblocks,*/ "9");
        properties.put( PROPK_DETAIL,/*&detailLevel,*/ "0");
        //properties.put( PROPK_SND_CHNLS,/*&numChannels,*/ "3");
        properties.put( PROPK_USEGAMMA,/*&usegamma,*/ "0");


    //#ifdef NORMALUNIX
        properties.put( "key_right",/*&key_right,*/KeyEvent.VK_RIGHT);
        properties.put( "key_left",/*&key_left,*/KeyEvent.VK_LEFT);
        properties.put( "key_up",/*&key_up,*/KeyEvent.VK_UP);
        properties.put( "key_down",/*&key_down,*/KeyEvent.VK_DOWN);
        properties.put( "key_strafeleft",/*&key_strafeleft,*/KeyEvent.VK_COMMA);
        properties.put( "key_straferight",/*&key_straferight,*/KeyEvent.VK_PERIOD);

        properties.put( "key_fire",/*&key_fire,*/KeyEvent.VK_CONTROL);
        properties.put( "key_use",/*&key_use,*/' ');
        properties.put( "key_strafe",/*&key_strafe,*/KeyEvent.VK_ALT);
        properties.put( "key_speed",/*&key_speed,*/KeyEvent.VK_SHIFT);

    // UNIX hack,*/to be removed. 
    //#ifdef SNDSERV
    //    properties.put( "sndserver",/*(int *) &sndserver_filename,*/(int) "sndserver");
    //    properties.put( "mb_used",/*&mb_used,*/2);
    //#endif

    //#endif

    //#ifdef LINUX
    //    properties.put( "mousedev",/*(int*)&mousedev,*/(int)"/dev/ttyS0");
    //    properties.put( "mousetype",/*(int*)&mousetype,*/(int)"microsoft");
    //#endif

        properties.put( "use_mouse",/*&usemouse,*/1);
        properties.put( "mouseb_fire",/*&mousebfire,*/ 0);
        properties.put( "mouseb_strafe",/*&mousebstrafe,*/ 1);
        properties.put( "mouseb_forward",/*&mousebforward,*/ 2);

        properties.put( "use_joystick",/*&usejoystick,*/ 0);
        properties.put( "joyb_fire",/*&joybfire,*/ 0);
        properties.put( "joyb_strafe",/*&joybstrafe,*/ 1);
        properties.put( "joyb_use",/*&joybuse,*/ 3);
        properties.put( "joyb_speed",/*&joybspeed,*/ 2);


        //TODO FIXME
    //    properties.put( "chatmacro0",/* (int *) &chat_macros[0],*/ (int) HUSTR_CHATMACRO0 );
    //    properties.put( "chatmacro1",/* (int *) &chat_macros[1],*/ (int) HUSTR_CHATMACRO1 );
    //    properties.put( "chatmacro2",/* (int *) &chat_macros[2],*/ (int) HUSTR_CHATMACRO2 );
    //    properties.put( "chatmacro3",/* (int *) &chat_macros[3],*/ (int) HUSTR_CHATMACRO3 );
    //    properties.put( "chatmacro4",/* (int *) &chat_macros[4],*/ (int) HUSTR_CHATMACRO4 );
    //    properties.put( "chatmacro5",/* (int *) &chat_macros[5],*/ (int) HUSTR_CHATMACRO5 );
    //    properties.put( "chatmacro6",/* (int *) &chat_macros[6],*/ (int) HUSTR_CHATMACRO6 );
    //    properties.put( "chatmacro7",/* (int *) &chat_macros[7],*/ (int) HUSTR_CHATMACRO7 );
    //    properties.put( "chatmacro8",/* (int *) &chat_macros[8],*/ (int) HUSTR_CHATMACRO8 );
    //    properties.put( "chatmacro9",/* (int *) &chat_macros[9],*/ (int) HUSTR_CHATMACRO9 );

    }
    
    
    public int drawText( int x, int y, boolean	direct, String string ) {
        int xPos = x;
    int 	c;
    int		w;

    int sn = 0;
    Patch [] font = Game.getInstance().headUp.hu_font;
    while (sn<string.length()){
	c = Character.toUpperCase(string.charAt(sn)) - HU_FONTSTART;
	sn++;
	if (c < 0 || c> HU_FONTSIZE) {
	    xPos += 4;
	    continue;
	}
		
	w = font[c].width;
	if (xPos+w > SCREENWIDTH) {
            break;
        }
	if (direct) {
            Game.getInstance().video.drawPatchDirect(xPos, y, 0, font[c]);
        } else {
            Game.getInstance().video.drawPatch(xPos, y, 0, font[c]);
        }
	xPos+=w;
    }

        return xPos;
    }
    
    public void loadDefaults() {
        int	i;
        int	len;
        File	f;
        String	def;
        String	strparm;
        String	newstring;
        int	parm;
        boolean	isString;
//    
//    // set everything to base values
//    numdefaults = sizeof(defaults)/sizeof(defaults[0]);
//    for (i=0 ; i<numdefaults ; i++)
//	*defaults[i].location = defaults[i].defaultvalue;
//    
//    // check for a custom default file
//    i = M_CheckParm ("-config");
//    if (i && i<myargc-1)
//    {
//	defaultfile = myargv[i+1];
//	printf ("	default file: %s\n",defaultfile);
//    }
//    else
//	defaultfile = basedefault;
//    
//    // read the file in, overriding any set defaults
//    f = fopen (defaultfile, "r");
//    if (f)
//    {
//	while (!feof(f))
//	{
//	    isstring = false;
//	    if (fscanf (f, "%79s %[^\n]\n", def, strparm) == 2)
//	    {
//		if (strparm[0] == '"')
//		{
//		    // get a string default
//		    isstring = true;
//		    len = strlen(strparm);
//		    newstring = (char *) malloc(len);
//		    strparm[len-1] = 0;
//		    strcpy(newstring, strparm+1);
//		}
//		else if (strparm[0] == '0' && strparm[1] == 'x')
//		    sscanf(strparm+2, "%x", &parm);
//		else
//		    sscanf(strparm, "%i", &parm);
//		for (i=0 ; i<numdefaults ; i++)
//		    if (!strcmp(def, defaults[i].name))
//		    {
//			if (!isstring)
//			    *defaults[i].location = parm;
//			else
//			    *defaults[i].location =
//				(int) newstring;
//			break;
//		    }
//	    }
//	}
//		
//	fclose (f);
//    }
    }
    
    public void readFile(/* char const*	name,  byte**	buffer */) {
//    int	handle, count, length;
//    struct stat	fileinfo;
//    byte		*buf;
//	
//    handle = open (name, O_RDONLY | O_BINARY, 0666);
//    if (handle == -1)
//	I_Error ("Couldn't read file %s", name);
//    if (fstat (handle,/*&fileinfo) == -1)
//	I_Error ("Couldn't read file %s", name);
//    length = fileinfo.st_size;
//    buf = Z_Malloc (length, PU_STATIC, NULL);
//    count = read (handle, buf, length);
//    close (handle);
//	
//    if (count < length)
//	I_Error ("Couldn't read file %s", name);
//		
//    *buffer = buf;
//    return length;
    }
    
    public void saveDefaults() {
//    int		i;
//    int		v;
//    FILE*	f;
//	
//    f = fopen (defaultfile, "w");
//    if (!f)
//	return; // can't write the file, but don't complain
//		
//    for (i=0 ; i<numdefaults ; i++)
//    {
//	if (defaults[i].defaultvalue > -0xfff
//	    && defaults[i].defaultvalue < 0xfff)
//	{
//	    v = *defaults[i].location;
//	    fprintf (f,"%s\t\t%i\n",defaults[i].name,v);
//	} else {
//	    fprintf (f,"%s\t\t\"%s\"\n",defaults[i].name,
//		     * (char **) (defaults[i].location));
//	}
//    }
//	
//    fclose (f);
//        
    }
    
    public static void screenShot() {
//        int		i;
//         byte*	linear;
//         char	lbmname[12];
//
//         // munge planar buffer to linear
//         linear = screens[2];
//         I_ReadScreen (linear);
//
//         // find a file name to save it to
//         strcpy(lbmname,"DOOM00.pcx");
//
//         for (i=0 ; i<=99 ; i++)
//         {
//             lbmname[4] = i/10 + '0';
//             lbmname[5] = i%10 + '0';
//             if (access(lbmname,0) == -1)
//                 break;	// file doesn't exist
//         }
//         if (i==100)
//             I_Error ("M_ScreenShot: Couldn't create a PCX");
//
//         // save the pcx file
//         WritePCXfile (lbmname, linear,
//                       SCREENWIDTH, SCREENHEIGHT,
//                       W_CacheLumpName ("PLAYPAL",PU_CACHE));
//
//         players[consoleplayer].message = "screen shot";
    }
    
    public void writeFile(
        /*  char const*	name,
            void*	source,
            int		length  */
    ) {
//    int		handle;
//    int		count;
//	
//    handle = open ( name, O_WRONLY | O_CREAT | O_TRUNC | O_BINARY, 0666);
//
//    if (handle == -1)
//	return false;
//
//    count = write (handle, source, length);
//    close (handle);
//	
//    if (count < length)
//	return false;
//		
//    return true;
    }
    
}
