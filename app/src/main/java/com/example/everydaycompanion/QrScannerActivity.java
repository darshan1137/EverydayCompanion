package com.example.everydaycompanion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QrScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize QR scanner
        IntentIntegrator integrator = new IntentIntegrator(QrScannerActivity.this);
        integrator.setPrompt("Scan a QR Code");
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() != null) {
                String scannedData = result.getContents();
                Toast.makeText(this, "Scanned: " + scannedData, Toast.LENGTH_SHORT).show();

                // Open the scanned link in a browser
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannedData));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Not a valid URL", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
            finish(); // Return to previous activity
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
