package com.example.bbbwithapi.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class PrefManager {
    SharedPreferences pref_global;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_GLOBAL = "mysharedreference";

    // Shared preferences variable name
    private static final String MYFIRSTTIMELAUNCH = "MYFIRSTTIMELAUNCH";

    private static final String MYUSERID = "MYUSERID";
    private static final String MYUSERNAME = "MYUSERNAME";
    private static final String MYNAME = "MYNAME";
    private static final String MYEMAIL = "MYEMAIL";
    private static final String MYMOBILEPHONE = "MYMOBILEPHONE";
    private static final String MYSEX = "MYSEX";
    private static final String MYDIVISION = "MYDIVISION";
    private static final String MYTEAM = "MYTEAM";
    private static final String MYUSERCLASS = "MYUSERCLASS";
    private static final String MYLEVEL = "MYLEVEL";

    public PrefManager(Context context) {
        this._context = context;
        pref_global = _context.getSharedPreferences(PREF_GLOBAL, PRIVATE_MODE);
        editor = pref_global.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(MYFIRSTTIMELAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref_global.getBoolean(MYFIRSTTIMELAUNCH, true);
    }

    public void setUserID(String userID) {
        editor.putString(MYUSERID, userID);
        editor.commit();
    }

    public String getUserID() {
        return pref_global.getString(MYUSERID, null);
    }

    public void setUserName(String userName) {
        editor.putString(MYUSERNAME, userName);
        editor.commit();
    }

    public String getUserName() {
        return pref_global.getString(MYUSERNAME, null);
    }

    public void setName(String name) {
        editor.putString(MYNAME, name);
        editor.commit();
    }

    public String getName() {
        return pref_global.getString(MYNAME, null);
    }

    public String getEmail() {
        return pref_global.getString(MYEMAIL, null);
    }

    public void setEmail(String email) {
        editor.putString(MYEMAIL, email);
        editor.commit();
    }

    public String getMobilePhone() {
        return pref_global.getString(MYMOBILEPHONE, null);
    }

    public void setMobilePhone(String mobilePhone) {
        editor.putString(MYMOBILEPHONE, mobilePhone);
        editor.commit();
    }

    public void setSex(String sex) {
        editor.putString(MYSEX, sex);
        editor.commit();
    }

    public String getSex() {
        return pref_global.getString(MYSEX, null);
    }

    public void setDivision(String division) {
        editor.putString(MYDIVISION, division);
        editor.commit();
    }

    public String getDivision() {
        return pref_global.getString(MYDIVISION, null);
    }

    public void setTeam(String team) {
        editor.putString(MYTEAM, team);
        editor.commit();
    }

    public String getTeam() {
        return pref_global.getString(MYTEAM, null);
    }

    public void setUserClass(String userClass) {
        editor.putString(MYUSERCLASS, userClass);
        editor.commit();
    }

    public String getUserClass() {
        return pref_global.getString(MYUSERCLASS, null);
    }

    public void setLevel(String level) {
        editor.putString(MYLEVEL, level);
        editor.commit();
    }

    public String getLevel() {
        return pref_global.getString(MYLEVEL, null);
    }

    public void removeAllPreference()
    {
        editor.clear();
        editor.commit();
    }

    public String getLocalIpAddress(Context myContext) {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            Toast.makeText(myContext, "Error IP: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
