package co.gladminds.bajajcvl.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class Multi_photo extends AppCompatActivity {
    private ImageAdapter imageAdapter;
    private static final int REQUEST_FOR_STORAGE_PERMISSION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(co.gladminds.bajajcvl.R.layout.activity_multi_photo);
        Toolbar toolbar = (Toolbar) findViewById(co.gladminds.bajajcvl.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Productscan.class);
                finish();
                startActivity(intent);

            }
        });

        populateImagesFromGallery();
    }

    public void btnChoosePhotosClick(View v) {

        ArrayList<String> selectedItems = imageAdapter.getCheckedItems();

        if (selectedItems != null && selectedItems.size() > 0) {
            // Toast.makeText(Multi_photo.this, "Total photos selected: " + selectedItems.size(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Productscan.class);
            intent.putStringArrayListExtra("imagelist", selectedItems);
            finish();
            startActivity(intent);
            //Log.d(Multi_photo.class.getSimpleName(), "Selected Items: " + selectedItems.toString());
        }
    }

    private void populateImagesFromGallery() {
        if (!mayRequestGalleryImages()) {
            return;
        }

        ArrayList<String> imageUrls = loadPhotosFromNativeGallery();
        initializeRecyclerView(imageUrls);
    }

    private boolean mayRequestGalleryImages() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            //promptStoragePermission();
            showPermissionRationaleSnackBar();
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_FOR_STORAGE_PERMISSION);
        }

        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_FOR_STORAGE_PERMISSION: {

                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        populateImagesFromGallery();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                            showPermissionRationaleSnackBar();
                        } else {
                            Toast.makeText(this, "Go to settings and enable permission", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                break;
            }
        }
    }

    private ArrayList<String> loadPhotosFromNativeGallery() {
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imagecursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy + " DESC");

        ArrayList<String> imageUrls = new ArrayList<String>();

        for (int i = 0; i < imagecursor.getCount(); i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imageUrls.add(imagecursor.getString(dataColumnIndex));

            System.out.println("=====> Array path => " + imageUrls.get(i));
        }

        return imageUrls;
    }

    private void initializeRecyclerView(ArrayList<String> imageUrls) {
        imageAdapter = new ImageAdapter(this, imageUrls, Multi_photo.this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 4);
        RecyclerView recyclerView = (RecyclerView) findViewById(co.gladminds.bajajcvl.R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ItemOffsetDecoration(this, co.gladminds.bajajcvl.R.dimen.item_offset));
        recyclerView.setAdapter(imageAdapter);
    }

    private void showPermissionRationaleSnackBar() {
        Snackbar.make(findViewById(co.gladminds.bajajcvl.R.id.button1), getString(co.gladminds.bajajcvl.R.string.permission_rationale),
                Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Request the permission
                ActivityCompat.requestPermissions(Multi_photo.this,
                        new String[]{READ_EXTERNAL_STORAGE},
                        REQUEST_FOR_STORAGE_PERMISSION);
            }
        }).show();

    }
}