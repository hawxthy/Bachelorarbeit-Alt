package ws1415.ps1415.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Diese Klasse wird genutzt um Einstellungen vom Benutzer persistent abzuspeichern.
 *
 * @author Martin Wrodarczyk
 */
public class PrefManager {
    private static final String SELECTED_USER_MAIL = "selectedUserMail";
    private PrefManager(){}

    private static SharedPreferences getSharedPreferences(Context context) {
        if (context != null) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            return null;
        }
    }

    public static boolean getGroupVisibility(Context context, String groupName){
        SharedPreferences sp = getSharedPreferences(context);
        if (sp != null) {
            return sp.getBoolean(groupName, false);
        } else {
            return false;
        }
    }

    public static void setGroupVisibility(Context context, String groupName, Boolean visibility){
        SharedPreferences sp = getSharedPreferences(context);
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(groupName, visibility);
            editor.commit();
        }
    }

    public static void deleteGroupVisibility(Context context, String groupName){
        SharedPreferences sp = getSharedPreferences(context);
        if (sp != null) {
            sp.edit().remove(groupName).commit();
        }
    }

    public static void setSelectedUserMail(Context context, String userMail){
        SharedPreferences sp = getSharedPreferences(context);
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(SELECTED_USER_MAIL, userMail);
            editor.commit();
        }
    }

    public static String getSelectedUserMail(Context context){
        SharedPreferences sp = getSharedPreferences(context);
        if (sp != null) {
            return sp.getString(SELECTED_USER_MAIL, "");
        } else {
            return "";
        }
    }
}
