package tambowskip.com.flashlight;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;

/**
 * Created by ldemyanenko on 07.03.2017.
 */

public class SettingsActivity extends AppCompatActivity {
    private App appSettings;
    private CheckBox showStatucsBar;
    private CheckBox switchSound;
    private CheckBox turnOnAtStartup;
    private CheckBox turnOffAtExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSettings=App.getInstance(getApplicationContext());
        setContentView(R.layout.activity_settings);
        showStatucsBar=(CheckBox)findViewById(R.id.showStatusBar);
        switchSound=(CheckBox)findViewById(R.id.switchSound);
        turnOnAtStartup=(CheckBox)findViewById(R.id.turnOnAtStartup);
        turnOffAtExit=(CheckBox)findViewById(R.id.turnOffAtExit);
        setupSettings();
    }

    private void setupSettings() {
        showStatucsBar.setChecked(appSettings.showStatusBar());
        switchSound.setChecked(appSettings.switchSounds());
        turnOnAtStartup.setChecked(appSettings.turnOnAtStartup());
        turnOffAtExit.setChecked(appSettings.turnOffAtExit());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveSettings();
    }

    private void saveSettings() {
        appSettings.saveSettings(showStatucsBar.isChecked(),switchSound.isChecked(),turnOnAtStartup.isChecked(), turnOffAtExit.isChecked());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
