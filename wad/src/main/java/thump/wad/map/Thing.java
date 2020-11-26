/*
    MapThing definition, position, orientation and type,
    plus skill/visibility options and attributes.
 */
package thump.wad.map;

/**
 *
 * @author mark
 */
public class Thing {

    public short x;
    public short y;
    public short angle;
    public short type;
    public short options;

    public Thing(short xPos, short yPos, short angle, short type, short flags ) {
        this.x = xPos;
        this.y = yPos;
        this.angle = angle;
        this.type = type;
        this.options = flags;
    }

    @Override
    public String toString() {
        return "thing: x:" + x + "  y:" + y + "  angle:" + angle + "  type:" + type + "  flags:" + String.format("0x%02X", options);
    }


    
}
