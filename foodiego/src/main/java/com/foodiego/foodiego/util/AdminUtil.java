package com.foodiego.foodiego.util;

import jakarta.servlet.http.HttpSession;

public class AdminUtil {

    private AdminUtil() {
    }

    public static boolean isAdmin(HttpSession session) {

        return "ADMIN".equals(
                session.getAttribute("userRole"));
    }
}