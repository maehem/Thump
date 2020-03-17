/*
    COLORMAP

  This contains 34 sets of 256 bytes, which "map" the colors "down" in
brightness. Brightness varies from sector to sector. At very low
brightness, almost all the colors are mapped to black, the darkest gray,
etc. At the highest brightness levels, most colors are mapped to their
own values, i.e. they don't change.
  In each set of 256 bytes, byte 0 will have the number of the palette
color to which original color 0 gets mapped.
  The colormaps are numbered 0-33. Colormaps 0-31 are for the different
brightness levels, 0 being the brightest (light level 248-255), 31 being
the darkest (light level 0-7). Light level is the fifth field of each
SECTOR record, see [4-9].
  Colormap 32 is used for every pixel in the display window (but not
the status bar), regardless of sector brightness, when the player is
under the effect of the "Invulnerability" power-up. This colormap is
all whites and greys.
  Colormap 33 is all black for some reason.
  While the light-amplification goggles power-up is in effect, everything
in the display uses colormap 0, regardless of sector brightness.

*/
package thump.wad.lump;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 *
 * @author mark
 */
public class ColorMapLump extends Lump {
    public final ArrayList<byte[]> mapList = new ArrayList<>();
    
    public ColorMapLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);
        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(256);
        for (int p = 0; p < 34; p++) {
            byte map[] = new byte[256];
            bb.clear();
            fc.read(bb);
            bb.position(0);
            for (int i = 0; i < 256; i++) {
                map[i] = bb.get();
            }
            mapList.add(map);
        }
    }
    
}
