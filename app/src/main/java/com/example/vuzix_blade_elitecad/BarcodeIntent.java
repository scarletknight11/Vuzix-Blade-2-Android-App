package com.example.vuzix_blade_elitecad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.vuzix.sdk.barcode.BarcodeType2;
import com.vuzix.sdk.barcode.ScannerFragment;
import com.vuzix.sdk.barcode.ScannerIntent;
import com.vuzix.sdk.barcode.ScanResult2;


public class BarcodeIntent extends AppCompatActivity {
    private static final int REQUEST_CODE_SCAN = 90001;  // Must be unique within this Activity
    private final static String TAG = "barcodeSample";
    private boolean cameraToggle = false;

    private TextView mTextEntryField;

    // Limiting the barcode formats to those you expect to encounter improves the speed of scanning
    // and increases the likelihood of properly detecting a barcode.
    private final String[] requestedBarcodeTypes = {
            BarcodeType2.QR_CODE.name(),
            BarcodeType2.UPC_A.name(),
            BarcodeType2.CODE_128.name()
    };

    /**
     * Sets up the User Interface
     *
     * @param savedInstanceState - unused and passed unchanged to the superclass
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextEntryField = (TextView) findViewById(R.id.scannedTextResult);

        Button buttonScan = (Button) findViewById(R.id.btn_scan_barcode);
        buttonScan.requestFocusFromTouch();
        buttonScan.setOnClickListener(view -> OnScanClick());
    }

    /**
     * Handler for the button press. Activates the scan.
     */
    private void OnScanClick() {
        Intent scannerIntent = new Intent(ScannerIntent.ACTION);
        scannerIntent.putExtra(ScannerIntent.EXTRA_BARCODE2_TYPES, requestedBarcodeTypes);
        if (cameraToggle) {
            scannerIntent.putExtra(ScannerIntent.EXTRA_CAMERA_ID, 0);
        }
        else {
            scannerIntent.putExtra(ScannerIntent.EXTRA_CAMERA_ID, 1);
        }
        cameraToggle = !cameraToggle;
        try {
            // The Vuzix smart glasses have a built-in Barcode Scanner app that is registered for this intent.
            startActivityForResult(scannerIntent, REQUEST_CODE_SCAN);
        } catch (ActivityNotFoundException activityNotFound) {
            Toast.makeText(this, R.string.only_on_mseries, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * The  M-Series Barcode Scanner App will scan a barcode and return
     *
     * @param requestCode int identifier you provided in startActivityForResult
     * @param resultCode int result of the scan operation
     * @param data Intent containing a ScanResult whenever the resultCode indicates RESULT_OK
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                ScanResult2 scanResult = data.getParcelableExtra(ScannerIntent.RESULT_EXTRA_SCAN_RESULT2);
                if (scanResult != null) {
                    Log.d(TAG, "Got result: " + scanResult.getText());
                    mTextEntryField.setText(scanResult.getText());
                } else {
                    Log.d(TAG, "No data");
                    mTextEntryField.setText(R.string.no_data);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
