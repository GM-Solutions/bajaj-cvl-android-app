package co.gladminds.bajajcvl.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.gladminds.bajajcvl.Common.Common;
import co.gladminds.bajajcvl.ORCUtil.TessUtil;
import co.gladminds.bajajcvl.R;
import co.gladminds.bajajcvl.adapter.UPCRecycleAdapter;
import co.gladminds.bajajcvl.interphace.OnResponseListener;
import co.gladminds.bajajcvl.models.UPC;
import co.gladminds.bajajcvl.util.OkHttpRequest;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;


public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int CAMERA_REQUEST = 3;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;
    Button buttonTakePicture;
    private boolean safeToTakePicture = false;

    final int RESULT_SAVEIMAGE = 0;

    private ArrayList<UPC> upcList;
    private String TAG = "UPCScanActivity";
    private RecyclerView recyclerView;
    private UPCRecycleAdapter adapter;
    private ProgressBar progressBar;
    private Dialog dialogA;
    private ImageView imageView;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        imageView = (ImageView) findViewById(R.id.imageView);
        controlInflater = LayoutInflater.from(getBaseContext());
//        View viewControl = controlInflater.inflate(R.layout.control, null);
//        LayoutParams layoutParamsControl
//                = new LayoutParams(LayoutParams.FILL_PARENT,
//                LayoutParams.FILL_PARENT);
//        this.addContentView(viewControl, layoutParamsControl);

        buttonTakePicture = (Button) findViewById(R.id.takepicture);
        buttonTakePicture.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                camera.autoFocus(new AutoFocusCallback() {
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            try {
                                if (safeToTakePicture) {
                                    camera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);
                                    safeToTakePicture = false;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
//                camera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);
            }
        });

        RelativeLayout layoutBackground = (RelativeLayout) findViewById(R.id.background);
        layoutBackground.setOnClickListener(new LinearLayout.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                buttonTakePicture.setEnabled(true);
                camera.autoFocus(myAutoFocusCallback);
            }
        });


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
        buttonTakePicture.setEnabled(true);

        findViewById(R.id.tvSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToCart();
            }
        });

        findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImageFromCamera();
            }
        });

        showInfoAlertDialog();
//        initTessBaseAPI();
    }

//    private void initTessBaseAPI() {
//        datapath = getFilesDir() + "/tesseract/";
//        //initialize Tesseract API
//        TessUtil.checkFile(new File(datapath + "tessdata/"), this);
//        String lang = "eng";
//        mTess = new TessBaseAPI();
//        mTess.init(datapath, lang);
//
//    }


    AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            // TODO Auto-generated method stub
            buttonTakePicture.setEnabled(true);
        }
    };


    public void takeImageFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                inspectFromBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ShutterCallback myShutterCallback = new ShutterCallback() {
        @Override
        public void onShutter() {
        }
    };

    PictureCallback myPictureCallback_RAW = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
        }
    };

    PictureCallback myPictureCallback_JPG = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub
   /*Bitmap bitmapPicture
    = BitmapFactory.decodeByteArray(arg0, 0, arg0.length); */

//            Uri uriTarget = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, new ContentValues());

            File fileDir = CameraActivity.this.getCacheDir();
            OutputStream imageFileOS;
            try {
                File file = File.createTempFile("Image", ".jpg", fileDir);
                imageFileOS = new FileOutputStream(file);
                imageFileOS.write(arg0);
                imageFileOS.flush();
                imageFileOS.close();
                inspect(file);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            camera.setDisplayOrientation(90);
            camera.startPreview();
            safeToTakePicture = true;
        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
        if (previewing) {
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = getBestPreviewSize(width, height, parameters);
                parameters.setPreviewSize(size.width, size.height);
                parameters.setPictureSize(size.width, size.height);

                if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    parameters.set("orientation", "portrait");
                    camera.setDisplayOrientation(90);
                    parameters.setRotation(90);
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                } else {
                    // This is an undocumented although widely known feature
                    parameters.set("orientation", "landscape");
                    // For Android 2.2 and above
                    camera.setDisplayOrientation(0);
                    // Uncomment for Android 2.0 and above
                    parameters.setRotation(0);
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
                try {
//                    camera.setParameters(parameters);
//                    surfaceHolder.setFixedSize(300, 300);
                    camera.setPreviewDisplay(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();
                previewing = true;
                safeToTakePicture = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera = Camera.open();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }


    private void inspectFromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e("Bitmap", "  Bitmap : Null " + bitmap.toString());
        }
        Log.e("Bitmap", "  Bitmap : " + bitmap.toString());
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        try {

            if (!textRecognizer.isOperational()) {
                new AlertDialog.Builder(this).setMessage("Text recognizer could not be set up on your device").show();
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
            }

            Collections.sort(textBlocks, new Comparator<TextBlock>() {
                @Override
                public int compare(TextBlock o1, TextBlock o2) {
                    int diffOfTops = o1.getBoundingBox().top - o2.getBoundingBox().top;
                    int diffOfLefts = o1.getBoundingBox().left - o2.getBoundingBox().left;
                    if (diffOfTops != 0) {
                        return diffOfTops;
                    }
                    return diffOfLefts;
                }
            });

            StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock != null && textBlock.getValue() != null) {
                    detectedText.append(textBlock.getValue());
                    detectedText.append(" ");
                }
            }

//            String OCRresult = null;
//
//            mTess.setImage(bitmap);
//            OCRresult = mTess.getUTF8Text();
//            detectedText.append(" " + OCRresult);

            Toast.makeText(this, detectedText.toString(), Toast.LENGTH_LONG);
            String[] strings = detectedText.toString().replaceAll("\n", " ").split(" ");
            List<String> list = validate(Arrays.asList(strings));

            if (list.isEmpty()) {
                Toast.makeText(this, "No UPC found, Please capture clear image!", Toast.LENGTH_LONG).show();
            } else {
                checkAndAddUpc(list);
            }

        } finally {
            textRecognizer.release();
        }
    }


    private void inspect(File file) {
        FileInputStream is = null;
        Bitmap bitmap = null;
        long l = file.length();
        try {
            is = new FileInputStream(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            options.inSampleSize = 1;
//            options.inScreenDensity = DisplayMetrics.DENSITY_HIGH;
//            bitmap = BitmapFactory.decodeStream(is, null, options);
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int rotate = 90;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            Log.e("bitmap", "B---------- : " + b.getWidth() + "  h : " + b.getHeight());
            Log.e("bitmap", "B-ddd--------- : " + bitmap.getByteCount());
            Log.e("bitmap", "B-ddd--------- : " + b);
            inspectFromBitmap(b);
            imageView.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
            Log.w("Camera", "Failed to find the file: " + file, e);
        } finally {
            if (bitmap != null) {
//                bitmap.recycle();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.w("Camera", "Failed to close InputStream", e);
                }
            }
        }
    }


    private void checkAndAddUpc(List<String> str) {
        Log.e("nikhl", "sstr : " + str.toString());
        for (int i = 0; i < str.size(); i++) {
            if (!checkForExist(str.get(i).trim())) {
                UPC upc = new UPC();
                upc.setCode(str.get(i).trim());
                upc.setStatus(100);
                upcList.add(upc);
                verifyUPC(upc.getCode(), false);
            } else {
                Toast.makeText(this, str.get(i).trim() + " is already exist, Please Scan other UPC", Toast.LENGTH_LONG).show();
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
                            Common.Customtoast(CameraActivity.this, "Please try again");
                        }
                    } else {
                        Common.Customtoast(CameraActivity.this, "Please try again");
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
                        upc.setPoint(object.getInt("points"));
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
            showDailog();
        }

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

    public List<String> validate(List<String> str) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < str.size(); i++) {
            Log.e("receiveDetections", "validate : " + str.get(i));
            if (str.get(i).contains("-") && str.get(i).endsWith("pt")) {
                Log.e("receiveDetections", "validate = true: " + str.get(i));
                String[] vu = str.get(i).split("-");
                String up = vu[0];
                up = up.toUpperCase();
                if (up.length() == 8 && (!list.contains(up))) {
                    list.add(up);
                }
            }
        }
        return list;
    }


    public void showDailog() {

        SharedPreferences sharedpreferences = getSharedPreferences("bajaj_pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        Set<String> set = sharedpreferences.getStringSet("countbarcode", null);
        if (set == null) {
            set = new HashSet<>();
        }
        final List<String> list = new ArrayList<>();
        list.addAll(set);

        dialogA = new Dialog(this);
        dialogA.setCancelable(true);
        dialogA.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogA.setContentView(R.layout.barcode_layout);

        final LinearLayout linearLayout = (LinearLayout) dialogA.findViewById(R.id.barcodelinear);
        RelativeLayout send = (RelativeLayout) dialogA.findViewById(R.id.sendlayout);
        RelativeLayout close = (RelativeLayout) dialogA.findViewById(R.id.closelayout);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogA.dismiss();
            }
        });
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < list.size(); i++) {
            final View item = inflator.inflate(R.layout.barcode_view, null);
            String getvalue = "" + list.get(i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
            item.setLayoutParams(layoutParams);
            TextView barcodevalue = (TextView) item.findViewById(R.id.barcodetext);
            final TextView cross = (TextView) item.findViewById(R.id.barcodecross);
            cross.setTag(i);
            cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = 0;
                    if (cross.getTag() instanceof Integer) {
                        position = (Integer) cross.getTag();
                        if (position >= list.size()) {
                            position = list.size() - 1;
                        }
                    }

                    // Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();
                    try {
                        list.remove(position);
                        linearLayout.removeViewAt(position);
                    } catch (Throwable ex) {
                        //FirebaseCrash.report(new Exception(ex.toString()));
                    }
                    Set<String> setTestId = new HashSet<String>();
                    setTestId.addAll(list);
                    editor.putStringSet("countbarcode", setTestId);
                    editor.commit();


                }
            });

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendbarcode();

                }
            });
            barcodevalue.setText(getvalue);
            linearLayout.addView(item);
        }

        dialogA.show();
    }

    private void sendbarcode() {
        final List barcodelist = new ArrayList();
        SharedPreferences sharedpreferences = getSharedPreferences("bajaj_pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();

        Set<String> set = sharedpreferences.getStringSet("countbarcode", null);
        barcodelist.addAll(set);

        OkHttpRequest okHttpRequest = new OkHttpRequest();
        okHttpRequest.setOnResponseListener(new OnResponseListener() {
            @Override
            public void onResponse(String result) {
                // login.setVisibility(View.VISIBLE);
                // progressBar.setVisibility(View.GONE);
                Log.e("RESULT", result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    if (result != null) {
                        if (jsonObject.optBoolean("status") == true) {
                            if (dialogA != null && dialogA.isShowing())
                                dialogA.dismiss();
                            barcodelist.clear();
                            Set<String> setTestId = new HashSet<String>();
                            setTestId.addAll(barcodelist);
                            editor.putStringSet("countbarcode", setTestId);
                            editor.commit();
                            Common.Customtoast(CameraActivity.this, jsonObject.optString("message"));
                        } else {
                            Common.Customtoast(CameraActivity.this, "Please try again");
                        }
                    } else {

                        Common.Customtoast(CameraActivity.this, "Please try again");
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
        } catch (UnsupportedEncodingException e1) {
            FirebaseCrash.report(new Exception(e1.toString()));
            e1.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < barcodelist.size(); i++) {
            String getvalue = "" + barcodelist.get(i);
            sb.append(getvalue + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        String upccode = sb.toString();
        String userid = Common.getPreferences(getApplicationContext(), "mobile_no");
        String jsonstring = "{\"mobile_no\":\"" + userid + "\",\"upc\":\"" + upccode + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = RequestBody.create(JSON, jsonstring);
        Request request = new Request.Builder()
                .url(Common.mainurl + "transaction/pointcollect")
                .header("Authorization", "Basic " + Base64.encodeToString(data, Base64.NO_WRAP))
                .post(body)
                .build();
        okHttpRequest.httpPost(CameraActivity.this, request);
    }


    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return (result);
    }

    private void showInfoAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Please adjust UPC code inside the box and click on the scan button.")
                .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
        builder.create();
        builder.show();
    }


}
