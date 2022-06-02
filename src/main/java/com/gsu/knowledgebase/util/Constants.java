package com.gsu.knowledgebase.util;

/**
 * Created by cnytync on 01/03/2018.
 */
public class Constants {
    public static int ROLE_ADMIN = 1;
    public static int ROLE_MODERATOR = 2;
    public static int ROLE_USER = 3;

    public static int USER_STATUS_AWAITING_CONFIRMATION = 1;
    public static int USER_STATUS_CONFIRMED = 2;
    public static int USER_STATUS_DELETED = 3;

    public static int USER_CONFIRMATION_STATUS_AWAITING = 1;
    public static int USER_CONFIRMATION_STATUS_CONFIRMED = 2;
    public static int USER_CONFIRMATION_STATUS_DISABLED = 3;

    public static String ROLE_NAME(int roleId) {
        switch (roleId) {
            case 1 : return "ADMIN";
            case 2 : return "MODERATOR";
            case 3 : return "USER";
            default: return "USER";
        }
    }
}
