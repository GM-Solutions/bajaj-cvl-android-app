package co.gladminds.bajajcvl.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.gladminds.bajajcvl.BuildConfig;
import co.gladminds.bajajcvl.Common.Common;
import co.gladminds.bajajcvl.adapter.DashboardAdapter;
import co.gladminds.bajajcvl.interphace.OnResponseListener;
import co.gladminds.bajajcvl.models.AppVersion;
import co.gladminds.bajajcvl.util.OkHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Animation right, left, top, bottom, bounce, rotate, bouncing;
    RelativeLayout accumulation, accumulationone, redeem, reddemone, checkbalance, checkbalanceone, tplayout,
            support, supportone, history, historyone, gift, giftone;
    private TextView totalpoint, tperrow, usercode;
    private ImageView countiamge;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;
    private List barcodelist;
    private StringBuilder sb;
    private int i;
    private ViewFlipper viewFlipper;
    private GridView gridView;
    private List categorylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(co.gladminds.bajajcvl.R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(co.gladminds.bajajcvl.R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.DKGRAY);
        // toolbar.getNavigationIcon().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

        right = AnimationUtils.loadAnimation(getApplicationContext(), co.gladminds.bajajcvl.R.anim.right_anim);
        left = AnimationUtils.loadAnimation(getApplicationContext(), co.gladminds.bajajcvl.R.anim.left_animation);
        top = AnimationUtils.loadAnimation(getApplicationContext(), co.gladminds.bajajcvl.R.anim.top_anim);
        bottom = AnimationUtils.loadAnimation(getApplicationContext(), co.gladminds.bajajcvl.R.anim.bottom_anim);
        bounce = AnimationUtils.loadAnimation(getApplicationContext(), co.gladminds.bajajcvl.R.anim.bounce);
        rotate = AnimationUtils.loadAnimation(getApplicationContext(), co.gladminds.bajajcvl.R.anim.rotate);
        bouncing = AnimationUtils.loadAnimation(getApplicationContext(), co.gladminds.bajajcvl.R.anim.bouncing);

        totalpoint = (TextView) findViewById(co.gladminds.bajajcvl.R.id.marque_scrolling_text);
        gridView = (GridView) findViewById(co.gladminds.bajajcvl.R.id.dashboardgridview);
        tplayout = (RelativeLayout) findViewById(co.gladminds.bajajcvl.R.id.totaltplayout);
        viewFlipper = (ViewFlipper) findViewById(co.gladminds.bajajcvl.R.id.viewflipper);
        categorylist = new ArrayList();
        categorylist.clear();
        categorylist.add("Accumulation");
        categorylist.add("Redeem");
        categorylist.add("Check Balance");
        categorylist.add("Support");
        categorylist.add("History");
        categorylist.add("My Gifts");
        DashboardAdapter dashboardAdapter = new DashboardAdapter(getApplicationContext(), categorylist, gridView);
        gridView.setAdapter(dashboardAdapter);

        viewFlipper.startFlipping();
        Animation animationFlipIn = AnimationUtils.loadAnimation(this, co.gladminds.bajajcvl.R.anim.flipin);
        Animation animationFlipOut = AnimationUtils.loadAnimation(this, co.gladminds.bajajcvl.R.anim.flipout);
        final Animation rotate = AnimationUtils.loadAnimation(this, co.gladminds.bajajcvl.R.anim.rotate);
        viewFlipper.setInAnimation(animationFlipIn);
        viewFlipper.setOutAnimation(animationFlipOut);
        viewFlipper.setFlipInterval(2000);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Intent intent = new Intent(getApplicationContext(), Productscan.class);
                    startActivity(intent);
                    overridePendingTransition(co.gladminds.bajajcvl.R.anim.enter, co.gladminds.bajajcvl.R.anim.exit);
                } else if (i == 1) {
                    Intent intent = new Intent(getApplicationContext(), ProductCatalogActivity.class);
                    startActivity(intent);
                    overridePendingTransition(co.gladminds.bajajcvl.R.anim.enter, co.gladminds.bajajcvl.R.anim.exit);
                } else if (i == 2) {
                    checkbalance();
                } else if (i == 3) {
                    Intent intent = new Intent(getApplicationContext(), Contact_Us.class);
                    startActivity(intent);
                    overridePendingTransition(co.gladminds.bajajcvl.R.anim.enter, co.gladminds.bajajcvl.R.anim.exit);

                } else if (i == 4) {
                    Intent intent = new Intent(getApplicationContext(), History.class);
                    startActivity(intent);
                    overridePendingTransition(co.gladminds.bajajcvl.R.anim.enter, co.gladminds.bajajcvl.R.anim.exit);
                } else if (i == 5) {
                    Intent intent = new Intent(getApplicationContext(), Mygifts.class);
                    startActivity(intent);
                    overridePendingTransition(co.gladminds.bajajcvl.R.anim.enter, co.gladminds.bajajcvl.R.anim.exit);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(co.gladminds.bajajcvl.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, co.gladminds.bajajcvl.R.string.navigation_drawer_open, co.gladminds.bajajcvl.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(co.gladminds.bajajcvl.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        usercode = (TextView) hView.findViewById(co.gladminds.bajajcvl.R.id.usercode);
        TextView username = (TextView) hView.findViewById(co.gladminds.bajajcvl.R.id.userName);
        if (Common.getPreferences(getApplicationContext(), "user_type").equalsIgnoreCase("1")) {
            username.setText("Retailer Id:");
        } else {
            username.setText("Member Id:");
        }
        String getvalue = Common.getPreferences(getApplicationContext(), "user_code");
        usercode.setText(getvalue);
        checkAndRequestPermissions();

        checkAppUpdate();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(co.gladminds.bajajcvl.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == co.gladminds.bajajcvl.R.id.accumulationinfo) {


            Intent i = new Intent(MainActivity.this, Productscan.class);
            startActivity(i);
            overridePendingTransition(co.gladminds.bajajcvl.R.anim.enter, co.gladminds.bajajcvl.R.anim.exit);

        } else if (id == co.gladminds.bajajcvl.R.id.redeeminfo) {

            Intent i = new Intent(MainActivity.this, ProductCatalogActivity.class);
            startActivity(i);
            overridePendingTransition(co.gladminds.bajajcvl.R.anim.enter, co.gladminds.bajajcvl.R.anim.exit);

        } else if (id == co.gladminds.bajajcvl.R.id.logout) {

            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
            finish();
            Common.SetPreferences(getApplicationContext(), "login", "false");
            overridePendingTransition(co.gladminds.bajajcvl.R.anim.enter, co.gladminds.bajajcvl.R.anim.exit);

        } else if (id == co.gladminds.bajajcvl.R.id.contactusinfo) {

            Intent i = new Intent(MainActivity.this, Contact_Us.class);
            startActivity(i);
            overridePendingTransition(co.gladminds.bajajcvl.R.anim.enter, co.gladminds.bajajcvl.R.anim.exit);

        } else if (id == co.gladminds.bajajcvl.R.id.historyinfo) {

            Intent i = new Intent(MainActivity.this, History.class);
            startActivity(i);
            overridePendingTransition(co.gladminds.bajajcvl.R.anim.enter, co.gladminds.bajajcvl.R.anim.exit);

        } else if (id == co.gladminds.bajajcvl.R.id.giftinfo) {

            Intent i = new Intent(MainActivity.this, Mygifts.class);
            startActivity(i);
            overridePendingTransition(co.gladminds.bajajcvl.R.anim.enter, co.gladminds.bajajcvl.R.anim.exit);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(co.gladminds.bajajcvl.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkbalance() {

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
                        boolean status = jsonObject.optBoolean("status");
                        String totalp = jsonObject.optString("totalpoints");
                        //tperrow.setVisibility(View.VISIBLE);
                        tplayout.setVisibility(View.VISIBLE);
                        totalpoint.setText("Total Point: " + totalp);
                    } else {

                        Common.Customtoast(MainActivity.this, "Please try again");
                    }
                } catch (JSONException e) {
                    //FirebaseCrash.report(new Exception(e.toString()));
                    e.printStackTrace();
                }

            }
        });
        String userCredentials = "bajajcvl:gm1361";
        byte[] data = null;
        try {
            data = userCredentials.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        String userid = Common.getPreferences(getApplicationContext(), "user_id");
        String usertype = Common.getPreferences(getApplicationContext(), "user_type");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(Common.mainurl + "transaction/checkbalance/" + userid + "/" + usertype)
                .header("Authorization", "Basic " + Base64.encodeToString(data, Base64.NO_WRAP))
                .build();

        okHttpRequest.httpPost(MainActivity.this, request);
    }

    // protected void onPostCreate(Bundle savedInstanceState) {super.onPostCreate(savedInstanceState);mDrawerToggle.syncState();}

    private boolean checkAndRequestPermissions() {

        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        countiamge = (ImageView) findViewById(co.gladminds.bajajcvl.R.id.scancount);
        sharedpreferences = getSharedPreferences("bajaj_pref", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        barcodelist = new ArrayList();
        barcodelist.clear();
        try {
            Set<String> set = sharedpreferences.getStringSet("countbarcode", null);
            barcodelist.addAll(set);
        } catch (Throwable ex) {
        }
        if (barcodelist.size() == 1) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartone);
        } else if (barcodelist.size() == 2) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.carttwo);
        } else if (barcodelist.size() == 3) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartthree);
        } else if (barcodelist.size() == 4) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartfour);
        } else if (barcodelist.size() == 5) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartfive);
        } else if (barcodelist.size() == 6) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartsix);
        } else if (barcodelist.size() == 7) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartseven);
        } else if (barcodelist.size() == 8) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.carteight);
        } else if (barcodelist.size() == 9) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartnine);
        } else if (barcodelist.size() == 10) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartten);
        } else {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cart);
        }
        countiamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (barcodelist.size() > 0) {
                    showDailog();
                }
            }
        });
        checkbalance();

    }

    Dialog dialogA;

    public void showDailog() {
        dialogA = new Dialog(this);
        dialogA.setCancelable(true);
        dialogA.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogA.setContentView(co.gladminds.bajajcvl.R.layout.barcode_layout);

        final LinearLayout linearLayout = (LinearLayout) dialogA.findViewById(co.gladminds.bajajcvl.R.id.barcodelinear);
        RelativeLayout send = (RelativeLayout) dialogA.findViewById(co.gladminds.bajajcvl.R.id.sendlayout);
        RelativeLayout close = (RelativeLayout) dialogA.findViewById(co.gladminds.bajajcvl.R.id.closelayout);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogA.dismiss();
            }
        });
        LayoutInflater inflator = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (i = 0; i < barcodelist.size(); i++) {
            final View item = inflator.inflate(co.gladminds.bajajcvl.R.layout.barcode_view, null);
            String getvalue = "" + barcodelist.get(i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
            item.setLayoutParams(layoutParams);
            TextView barcodevalue = (TextView) item.findViewById(co.gladminds.bajajcvl.R.id.barcodetext);
            final TextView cross = (TextView) item.findViewById(co.gladminds.bajajcvl.R.id.barcodecross);
            cross.setTag(i);
            cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = 0;
                    if (cross.getTag() instanceof Integer) {
                        position = (Integer) cross.getTag();
                        if (position >= barcodelist.size()) {
                            position = barcodelist.size() - 1;
                        }
                    }
                    //Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();
                    try {
                        barcodelist.remove(position);
                        linearLayout.removeViewAt(position);
                    } catch (Throwable ex) {
                    }
                    Set<String> setTestId = new HashSet<String>();
                    setTestId.addAll(barcodelist);
                    editor.putStringSet("countbarcode", setTestId);
                    editor.commit();

                    if (barcodelist.size() == 1) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartone);
                    } else if (barcodelist.size() == 2) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.carttwo);
                    } else if (barcodelist.size() == 3) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartthree);
                    } else if (barcodelist.size() == 4) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartfour);
                    } else if (barcodelist.size() == 5) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartfive);
                    } else if (barcodelist.size() == 6) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartsix);
                    } else if (barcodelist.size() == 7) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartseven);
                    } else if (barcodelist.size() == 8) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.carteight);
                    } else if (barcodelist.size() == 9) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartnine);
                    } else if (barcodelist.size() == 10) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartten);
                    } else {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cart);
                        dialogA.dismiss();
                    }
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
                            dialogA.dismiss();
                            barcodelist.clear();
                            Set<String> setTestId = new HashSet<String>();
                            setTestId.addAll(barcodelist);
                            editor.putStringSet("countbarcode", setTestId);
                            editor.commit();
                            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cart);
                            Common.Customtoast(MainActivity.this, jsonObject.optString("message"));
                        } else {
                            Common.Customtoast(MainActivity.this, "Please try again");
                        }
                    } else {

                        Common.Customtoast(MainActivity.this, "Please try again");
                    }
                } catch (JSONException e) {
                    //FirebaseCrash.report(new Exception(e.toString()));
                    e.printStackTrace();
                }

            }
        });
        String userCredentials = "bajajcvl:gm1361";
        byte[] data = null;
        try {
            data = userCredentials.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        sb = new StringBuilder();
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
        okHttpRequest.httpPost(MainActivity.this, request);
    }


    private void checkAppUpdate() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("app_version");

        AppVersion appVersion = new AppVersion();
        appVersion.setVersionCode(2);
        appVersion.setVersionMessage("New Version is available ");
        appVersion.setVersionName("1.0.2");

        myRef.setValue(appVersion);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AppVersion appVer = dataSnapshot.getValue(AppVersion.class);
                if (appVer.getVersionCode() > BuildConfig.VERSION_CODE)
                    updateAppFromPlayStore(appVer.getVersionMessage(), MainActivity.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("FirebaseDatabase", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public static void updateAppFromPlayStore(String message, final Context context) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            // Setting Dialog Title
            alertDialog.setTitle("Update Notification");

            String msg = "New App is available on playstore,</br> Please update your App. New Version having following update :  <br></br>" + message;
            // Setting Dialog Message

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                alertDialog.setMessage(Html.fromHtml(msg, Html.FROM_HTML_MODE_LEGACY));
            } else {
                alertDialog.setMessage(Html.fromHtml(msg));
            }
            // On pressing Settings button
            alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
