package tambowskip.com.flashlight;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;


public class App {
    private static final String PREF_APP = "prefApp";
    private static final String KEY_SHOW_STATUS_BAR = "showStatusBar";
    private static final String KEY_SWITCH_SOUNDS = "switchSounds";
    private static final String KEY_TURN_ON_AT_STARTUP = "turnOnAtStartup";
    private static final String KEY_TURN_OFF_AT_EXIT = "turnOffAtExit";
    private static App ourInstance;
    private final SharedPreferences mPref;
    private static FlashLightInterface flashlight;

    private App(Context context) {
        mPref = context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
    }
    public static FlashLightInterface getFlashlightInstance(Context context){
        if(flashlight ==null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flashlight =new FlashLightAPI21(context);

            }else{
                flashlight =new FlashLight(context);

            }

        }
        return flashlight;
    }

    public static App getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new App(context);
        }
        return ourInstance;
    }


    public void saveSettings(Boolean showStatusBar, Boolean switchSound, Boolean turnOnAtStartup, Boolean turnOffAtExit) {
        mPref.edit().putString(KEY_SHOW_STATUS_BAR, String.valueOf(showStatusBar))
                .putString(KEY_SWITCH_SOUNDS, String.valueOf(switchSound))
                .putString(KEY_TURN_ON_AT_STARTUP, String.valueOf(turnOnAtStartup))
                .putString(KEY_TURN_OFF_AT_EXIT, String.valueOf(turnOffAtExit))
                .apply();
    }
    public boolean showStatusBar() {
        return Boolean.valueOf(mPref.getString(KEY_SHOW_STATUS_BAR, "true"));
    }
    public boolean switchSounds() {
        return Boolean.valueOf(mPref.getString(KEY_SWITCH_SOUNDS, "false"));
    }
    public boolean turnOnAtStartup() {
        return Boolean.valueOf(mPref.getString(KEY_TURN_ON_AT_STARTUP, "true"));
    }
    public boolean turnOffAtExit() {
        return Boolean.valueOf(mPref.getString(KEY_TURN_OFF_AT_EXIT, "false"));
    }

}

