package ldemyanenko.com.flashlight;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by ldemyanenko on 07.03.2017.
 */

public class FlashLightAPI21 implements FlashLightInterface {
    private static final int NOTIFICATION_ID = 10000;
    private final Context context;
    private boolean hasFlash;

    private boolean isFlashOn;
    private MediaPlayer mp;
    private View view;
    private CameraManager mCameraManager;
    private String mCameraId;

    public FlashLightAPI21(Context context){
        this.context=context;
        //this.context=activity.getApplicationContext();
        //this.activity=activity;
    }


    @TargetApi(21)
    public void getCamera() {
            mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            try {
                mCameraId = mCameraManager.getCameraIdList()[0];
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
    }



    public void turnOnFlash() {
        if (!isFlashOn) {
        try {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mCameraManager.setTorchMode(mCameraId, true);
                    playSound();
                    isFlashOn = true;
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    public void turnOffFlash() {
        if (isFlashOn) {

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mCameraManager.setTorchMode(mCameraId, false);
                    playSound();
                    isFlashOn = false;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





    public void checkForFlash(final Activity activity){
        hasFlash = activity.getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {

             //device doesn't support flash
             //Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(activity)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    activity.finish();
                }
            });
            alert.show();
            return;
        }
    }


    public void releaseCamera() {
        turnOffFlash();

    }

    public boolean isFlashOn() {
        return isFlashOn;
    }

    private void playSound() {

        if(App.getInstance(context).switchSounds()) {
            //   mainActivity.view.playSoundEffect(android.view.SoundEffectConstants.CLICK);

            mp = MediaPlayer.create(context, R.raw.flash_sound);

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.release();
                }
            });
            mp.start();
        }

    }

    public void workWithFlash() {
        if(isFlashOn()) {
            turnOffFlash();
        }else{

            turnOnFlash();
        }
    }


    public void setFlashOn(boolean flashOn) {
        this.isFlashOn = flashOn;
    }

}
