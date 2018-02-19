package co.gladminds.bajajcvl.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import co.gladminds.bajajcvl.Common.Common;
import co.gladminds.bajajcvl.ORCUtil.CameraSource;
import co.gladminds.bajajcvl.ORCUtil.CameraSourcePreview;
import co.gladminds.bajajcvl.ORCUtil.GraphicOverlay;
import co.gladminds.bajajcvl.ORCUtil.OcrDetectorProcessor;
import co.gladminds.bajajcvl.ORCUtil.OcrGraphic;
import co.gladminds.bajajcvl.ORCUtil.OnUPCFound;
import co.gladminds.bajajcvl.R;
import co.gladminds.bajajcvl.adapter.UPCRecycleAdapter;
import co.gladminds.bajajcvl.interphace.OnResponseListener;
import co.gladminds.bajajcvl.models.UPC;
import co.gladminds.bajajcvl.util.OkHttpRequest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UPCScanActivity extends AppCompatActivity {

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private TextView tvUpc;
    private String text;
    private ArrayList<UPC> upcList;
    private String TAG = "UPCScanActivity";
    private RecyclerView recyclerView;
    private UPCRecycleAdapter adapter;
    private ProgressBar progressBar;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_upcscan);


        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        upcList = new ArrayList<>();

//        UPC upc = new UPC();
//        upc.setCode("nikhil");
//        upc.setStatus(100);
//        upcList.add(upc);
        adapter = new UPCRecycleAdapter(this, upcList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.SetOnClickListener(new UPCRecycleAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onUPCChange(int position, String Upc) {
                UPC upc = upcList.get(position);
                upc.setCode(Upc);
                upcList.set(position, upc);
//                ArrayList<String> list = new ArrayList<>();
//                list.add(Upc);
//                checkAndAddUpc(list);
            }

            @Override
            public void onVerify(int position) {
                UPC upc = upcList.get(position);
                upc.setShowingProgress(true);
                upcList.set(position, upc);
                ArrayList<String> list = new ArrayList<>();
                list.add(upc.getCode());
                checkAndAddUpc(list);
            }


            @Override
            public void onDeleteClick(int position) {
                deleteConfirmation(position);
            }
        });

        // read parameters from the intent used to launch the activity.
        boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, false);
        boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }


        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        Snackbar.make(mGraphicOverlay, "Tap to capture. Pinch/Stretch to zoom", Snackbar.LENGTH_LONG).show();


        (findViewById(R.id.tvSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder stringBuilder = new StringBuilder();
                boolean b = false;
                for (int i = 0; i < upcList.size(); i++) {
                    if (upcList.get(i).getStatus() == 100) {
                        b = true;
                        stringBuilder.append(upcList.get(i).getCode() + ",");
                    }
                }
                if (b) {
                    verifyUPC(stringBuilder.toString(), true);
                } else {
                    saveToCart();
                }
            }
        });
    }

    private void deleteConfirmation(final int position) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirmation");
            builder.setMessage("Do you want to delete this UPC : " + upcList.get(position).getCode() + " ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            upcList.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);
        boolean c = gestureDetector.onTouchEvent(e);
        return b || c || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     * <p>
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A text recognizer is created to find text.  An associated processor instance
        // is set to receive the text recognition results and display graphics for each text block
        // on screen.
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        final OcrDetectorProcessor processor = new OcrDetectorProcessor(mGraphicOverlay);
        textRecognizer.setProcessor(processor);
        processor.SetLisener(new OnUPCFound() {
            @Override
            public void onFound(final ArrayList<String> upcs) {
                UPCScanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("nikhl", "onFound  : " + upcs.toString());
                        Toast.makeText(getApplicationContext(), "UPC : " + upcs, Toast.LENGTH_SHORT).show();
                        checkAndAddUpc(upcs);
                        processor.clearUpc();

                    }
                });

            }
        });

        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        mCameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(2.0f)
//                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                        .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    private void checkAndAddUpc(List<String> str) {
        Log.e("nikhl", "sstr : " + str.toString());
        for (int i = 0; i < str.size(); i++) {
            if (!checkForExist(str.get(i))) {
                UPC upc = new UPC();
                upc.setCode(str.get(i));
                upc.setStatus(100);
                upcList.add(upc);
                verifyUPC(upc.getCode(), false);
            } else {
                verifyUPC(str.get(i), false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private boolean checkForExist(String s) {
        boolean valid = false;
        for (int i = 0; i < upcList.size(); i++) {
            if (upcList.get(i).getCode().equals(s)) {
                return true;
            }
        }
        return valid;

    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // Check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for gladminds,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For gladminds, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }

    /**
     * onTap is called to capture the first TextBlock under the tap location and return it to
     * the Initializing Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
//        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
//        TextBlock text = null;
//        if (graphic != null) {
//            text = graphic.getTextBlock();
//            if (text != null && text.getValue() != null) {
//                Intent data = new Intent();
//                data.putExtra(TextBlockObject, text.getValue());
//                setResult(CommonStatusCodes.SUCCESS, data);
//                finish();
//            } else {
//                Log.d(TAG, "text data is null");
//            }
//        } else {
//            Log.d(TAG, "no text detected");
//        }
//        return text != null;
        return false;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }


    public void verifyUPC(final String upccode, final boolean b) {
        if (b)
            progressBar.setVisibility(View.VISIBLE);
        OkHttpRequest okHttpRequest = new OkHttpRequest();
        okHttpRequest.setOnResponseListener(new OnResponseListener() {
            @Override
            public void onResponse(String result) {
                Log.e("RESULT", result);
                progressBar.setVisibility(View.GONE);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    if (result != null) {
                        if (jsonObject.optBoolean("status") == true) {
                            JSONArray jsonArray = jsonObject.getJSONArray("upc");
                            updateUPCStatus(jsonArray, b);
                        } else {
                            Common.Customtoast(UPCScanActivity.this, "Please try again");
                        }
                    } else {
                        Common.Customtoast(UPCScanActivity.this, "Please try again");
                    }
                } catch (JSONException e) {
                    FirebaseCrash.report(new Exception(e.toString()));
                    e.printStackTrace();
                }

            }
        });
        String userCredentials = "bajajcvl:gm1361";
        byte[] data = null;
        try {
            data = userCredentials.getBytes("UTF-8");
            Log.e("my data is", Base64.encodeToString(data, Base64.NO_WRAP));

        } catch (UnsupportedEncodingException e1) {
            FirebaseCrash.report(new Exception(e1.toString()));
            e1.printStackTrace();
        }
        String finalupccode = upccode.replace("\n", " ");
        Log.e("detect text is", finalupccode);
        String usertype = Common.getPreferences(getApplicationContext(), "user_type");
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("user_type", usertype);
            jsonObject.put("upc", upccode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(Common.mainurl + "transaction/verify_upc")
                .header("Authorization", "Basic " + Base64.encodeToString(data, Base64.NO_WRAP))
                .post(body)
                .build();
        okHttpRequest.httpPost(this, request, false);


    }

    private void updateUPCStatus(JSONArray jsonArray, boolean b) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                for (int j = 0; j < upcList.size(); j++) {
                    if (object.getString("upc").endsWith(upcList.get(j).getCode())) {
                        UPC upc = upcList.get(j);
                        upc.setStatus(object.getInt("status_used"));
                        upc.setMessage(object.getString("message"));
                        upc.setShowingProgress(false);
                        upcList.set(j, upc);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        adapter.notifyDataSetChanged();
        if (b)
            saveToCart();


    }

    private void saveToCart() {
        SharedPreferences sharedpreferences = getSharedPreferences("bajaj_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Set<String> set = sharedpreferences.getStringSet("countbarcode", null);
        if (set == null) {
            set = new HashSet<>();
        }
        List<String> list = new ArrayList<>();
        list.addAll(set);

        String msg = "";
        for (int i = 0; i < upcList.size(); i++) {
            if (upcList.get(i).getStatus() == 1) {
                msg = msg + upcList.get(i).getCode() + ",";
                if (!list.contains(upcList.get(i).getCode())) {
                    set.add(upcList.get(i).getCode());
                }
            }
        }

        editor.putStringSet("countbarcode", set);
        editor.commit();

        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(this, "Valid UPC Not Found..", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, msg + " UPC's added to your cart.", Toast.LENGTH_LONG).show();
            finish();
        }

    }


}
