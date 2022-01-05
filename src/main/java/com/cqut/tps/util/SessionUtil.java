package com.cqut.tps.util;

import javax.servlet.http.HttpSession;

/**
 * @author Augenstern
 * @date 2021/12/8
 */
public class SessionUtil {

    private static final ThreadLocal<HttpSession> sessionThreadLocal = new ThreadLocal<>();

    public static void set(HttpSession session) {
        //∞Î–° ±
        session.setMaxInactiveInterval(1800);
        sessionThreadLocal.set(session);
    }

    public static HttpSession get() {
        return sessionThreadLocal.get();
    }

    public static void remove() {
        sessionThreadLocal.remove();
    }
}
