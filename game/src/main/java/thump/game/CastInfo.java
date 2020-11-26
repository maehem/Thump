/*
 * Cast Information
 */
package thump.game;


/**
 *
 * @author mark
 */
public class CastInfo {
        public String           name;
        public MobJInfo.Type	type;    

    CastInfo(String name, MobJInfo.Type type) {
        this.name = name;
        this.type = type;
    }
}
