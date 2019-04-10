package ldemyanenko.com.flashlight;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {
    private static final String KEY_FLASHLIGHT_ON = "KEY_FLASHLIGHT_ON";
    private static final String BROADCAST_ACTION = "BROADCAST_ACTION";
    private static final String KEY_FLASH_BUTTON_CLICK = "KEY_FLASH_BUTTON_CLICK";

    private App appSettings;
    private FlashLightInterface flashlight;
    private View view;
    private ImageView mFlashlight;
    private boolean useFlash=true;
    boolean blackScreen =false;
    private View mMainLayout;
    private ImageView mScreen;
    private boolean notificationOn;
    private boolean usedWhiteScreenFromNotification=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if(NotificationUtils.debugModeTwoDaysDateCheck("18.03.2017")){
//            finish();
//        }
        appSettings=App.getInstance(getApplicationContext());
        flashlight=App.getFlashlightInstance(getApplicationContext());

        flashlight.getCamera();
        if(savedInstanceState!=null){
            flashlight.setFlashOn(savedInstanceState.getBoolean(KEY_FLASHLIGHT_ON));
        }else {
            flashlight.checkForFlash(this);
        }

        mMainLayout=findViewById(R.id.mainLayout);
        mFlashlight=(ImageView) findViewById(R.id.flashlight);
        mFlashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                useFlash=true;
                setupFlashlightTypeIcons();
                blackScreen =true;//
                setBlackWhiteScreen();
                toggleButtonImage();

            }
        });
        mScreen=(ImageView) findViewById(R.id.screen);
        mScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                useFlash=false;
                setupFlashlightTypeIcons();
                toggleButtonImage();
                if(flashlight.isFlashOn()){
                    flashlight.turnOffFlash();
                }



            }
        });
        view=findViewById(R.id.main_button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (useFlash) {
                    flashlight.workWithFlash();
                } else{
                    setBlackWhiteScreen();
                }
                setupFlashlightTypeIcons();
                toggleButtonImage();



            }
        });


        checkSettingsAndTurnOnFlashlightOnStart();
        setupFlashlightTypeIcons();
        registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_ACTION));
        Log.d("broadcastReceiver","register");




    }

    private void checkSettingsAndTurnOnFlashlightOnStart() {
        if(appSettings.turnOnAtStartup()&&!flashlight.isFlashOn() && usedWhiteScreenFromNotification==false){
            flashlight.turnOnFlash();
            useFlash=true;
            toggleButtonImage();
        }else{
            toggleButtonImage();

        }
        usedWhiteScreenFromNotification=false;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BroadcastReceiver","Start");
            //Toast.makeText(getApplicationContext(),"Change UI",Toast.LENGTH_LONG);
            updateUI(intent);
            Log.d("BroadcastReceiver","End");
        }
    };

    private void updateUI(Intent intent) {
        if(intent.getBooleanExtra(KEY_FLASH_BUTTON_CLICK,false)) {
            useFlash = true;
            blackScreen = true;//
        }else{
            useFlash = false;
            blackScreen = false;
            if (flashlight.isFlashOn()){
                flashlight.turnOffFlash();
            }
            usedWhiteScreenFromNotification=true;
            getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        }
        setBlackWhiteScreen();
        setupFlashlightTypeIcons();
        toggleButtonImage();
    }

    private void setupFlashlightTypeIcons() {

        if(useFlash){
        mFlashlight.setBackgroundResource(R.drawable.ic_flashlight_white);
        mScreen.setBackgroundResource(R.drawable.ic_screen);
        }else{
            mFlashlight.setBackgroundResource(R.drawable.ic_flashlight);
            mScreen.setBackgroundResource(R.drawable.ic_screen_white);
        }

    }

    private void setBlackWhiteScreen() {
        if (blackScreen){
        mMainLayout.setBackgroundColor(getResources().getColor(R.color.black));
        }else{
        mMainLayout.setBackgroundColor(getResources().getColor(R.color.white));
        }
        blackScreen =!blackScreen;


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                openSettingsActivity();
                return true;
            default:
                return true;
        }
    }

    private void openSettingsActivity() {
        startActivity(new Intent(this,SettingsActivity.class));
       // Toast.makeText(getApplicationContext(),"Settings menu", Toast.LENGTH_LONG);
    }


    private void toggleButtonImage(){
        if((flashlight.isFlashOn()&& useFlash)||(blackScreen &&!useFlash)){
            if (useFlash) {
                findViewById(R.id.main_button).setBackgroundResource(R.drawable.cirlce_for_button_off);
            }else{
                findViewById(R.id.main_button).setBackgroundResource(R.drawable.cirlce_for_button_white);
                mFlashlight.setBackgroundResource(R.drawable.ic_flashlight_white);
            }
            findViewById(R.id.main_button_image).setBackgroundResource(R.drawable.ic_power_settings_new_black);
        }else{
            findViewById(R.id.main_button).setBackgroundResource(R.drawable.cirlce_for_button);
            findViewById(R.id.main_button_image).setBackgroundResource(R.drawable.ic_power_settings_new_white);
            if(!useFlash) {
                mFlashlight.setBackgroundResource(R.drawable.ic_flashlight);
            }


        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        // on starting the app get the camera params
        flashlight.getCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleButtonImage();
        if(!notificationOn && appSettings.showStatusBar()){
            NotificationUtils.createNotification(this);
            notificationOn=true;

        }else {
            if (!appSettings.showStatusBar()) {
                NotificationUtils.removeNotification(this);
                notificationOn=false;

            }
        }
        checkSettingsAndTurnOnFlashlightOnStart();
        //registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_ACTION));
       // Log.d("broadcastReceiver","register");

    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(broadcastReceiver);
        //Log.d("broadcastReceiver","UNregister");


    }

    @Override
    protected void onStop() {
        super.onStop();
        if(appSettings.turnOffAtExit()){
            flashlight.releaseCamera();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        boolean flashOn = flashlight.isFlashOn();
        outState.putBoolean(KEY_FLASHLIGHT_ON, flashOn);

        super.onSaveInstanceState(outState);

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    public static class FlashlightActionListener extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            FlashLightInterface flashLight=App.getFlashlightInstance(context);
            flashLight.getCamera();
            flashLight.workWithFlash();
            intent = new Intent(BROADCAST_ACTION);
            intent.putExtra(KEY_FLASH_BUTTON_CLICK,true);
            context.sendBroadcast(intent);
        }

    }

    public static class WhiteScreeActionListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("White..ActionListener","Click");

            Intent broadcastIntent = new Intent(BROADCAST_ACTION);
            broadcastIntent.putExtra(KEY_FLASH_BUTTON_CLICK,false);
            context.sendBroadcast(broadcastIntent);
            PendingIntent pIntent = PendingIntent.getActivity(
                    context,
                    0,
                    new Intent(context,MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                pIntent.send();
                Log.d("White..ActionListener","Send");
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
                Log.d("White..ActionListener","Error");
                Log.d("White..ActionListener",e.getMessage());
                //Toast.makeText(context,"Error",Toast.LENGTH_LONG);
            }
            Log.d("White..ActionListener","End");


        }



    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
