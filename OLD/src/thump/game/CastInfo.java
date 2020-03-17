/*
 * Cast Information
 */
package thump.game;

import thump.global.MobJInfo;

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
