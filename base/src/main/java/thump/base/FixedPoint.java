/*
 * From m_fixed.c
 */
package thump.base;

import static java.lang.Math.abs;
import static thump.base.Defines.logger;

/**
 *
 * @author mark
 */
public class FixedPoint {
    //public static final Logger logger = Logger.getGlobal();
//
// Fixed point, 32bit as 16.16.
//

    public static final int FRACBITS = 16;

    public static final int FRACUNIT = (1 << FRACBITS);

    // Fixme. __USE_C_FIXED__ or something.
    public static int mul(int a, int b) {
        return (int) ((a * (long) b) >> FRACBITS);
    }

    //
    // FixedDiv, C version.
    //
    public static int div(int a, int b) {
        if ((abs(a) >> 14) >= abs(b)) {
            return (a ^ b) < 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }
        return div2(a, b);
    }

    public static int div2(int a, int b) {
        double c;

        c = ((double) a) / ((double) b) * FRACUNIT;

        if (c >= 2147483648.0 || c < -2147483648.0) {
            logger.severe("div2(): divide by zero!");
            //I_Error("FixedDiv: divide by zero");
        }
        return (int) c;
    }

}
