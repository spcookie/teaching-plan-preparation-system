package com.cqut.tps.util;

/**
 * @author Augenstern
 * @date 2021/12/31
 */
public class IdUtil {
    public static String generateID(int index) {
        if (index < 10) {
            return "C0" + index;
        }
        return "C" + index;
    }

    public static int generateIndex(String id) {
        return Integer.parseInt(id.substring(1));
    }
}
