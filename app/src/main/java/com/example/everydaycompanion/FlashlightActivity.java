package com.example.everydaycompanion;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class FlashlightActivity extends AppCompatActivity {

    private Button toggleFlashlightBtn;
    private boolean isFlashOn = false;
    private CameraManager cameraManager;
    private String cameraId;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);

        toggleFlashlightBtn = findViewById(R.id.toggleFlashlightBtn);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "No flashlight available on this device", Toast.LENGTH_LONG).show();
            return;
        }

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        toggleFlashlightBtn.setOnClickListener(view -> {
            try {
                if (isFlashOn) {
                    cameraManager.setTorchMode(cameraId, false);
                    toggleFlashlightBtn.setText("Turn On");
                    isFlashOn = false;
                } else {
                    cameraManager.setTorchMode(cameraId, true);
                    toggleFlashlightBtn.setText("Turn Off");
                    isFlashOn = true;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        });
    }
}
