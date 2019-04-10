package ldemyanenko.com.flashlight;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.View;


import java.io.Serializable;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by 8760w on 07.03.2017.
 */

public class FlashLight implements FlashLightInterface {
    private final Context context;
    private boolean hasFlash;
    private Camera camera;
    private Camera.Parameters params;
    private boolean isFlashOn;
    private MediaPlayer mp;

    public FlashLight(Context context){
        this.context=context;
    }


    public void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error.", e.getMessage());
            }
        }

    }

    public void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            // play sound
            playSound();

            params = camera.getParameters();
            List<String> pList = camera.getParameters().getSupportedFlashModes();
            if (pList.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }else{
                params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

            }
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;

            // changing button/switch image
        }

    }



    public  void checkForFlash(final Activity activity){
        hasFlash = activity.getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {

            //device doesn't support flash
            //Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(activity)
                    .create();
            alert.setTitle(R.string.error);
            alert.setMessage(context.getString(R.string.dont_support_message));
            alert.setButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    activity.finish();
                }
            });
            alert.show();
            return;
        }
    }

    public void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            // play sound
            playSound();

            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;

            // changing button/switch image
        }
    }

    public void releaseCamera() {
        turnOffFlash();
        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    public boolean isFlashOn() {
        return isFlashOn;
    }

    private void playSound() {

        if(App.getInstance(context).switchSounds()) {

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
