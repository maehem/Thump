/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.base;

import java.math.BigInteger;

/**
 *
 * @author mark
 */
public class Utilities {
    
    /**
     * Turn a String into it's ASCII hex values.  For debugging strings.
     * 
     * @param str
     * @return str as hex values
     */
    public static String toHex(String str) {
        return String.format("%040x", new BigInteger(1, str.getBytes()));
    }    
}
