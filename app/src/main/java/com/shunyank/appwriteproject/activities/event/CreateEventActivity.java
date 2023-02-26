package com.shunyank.appwriteproject.activities.event;

import static android.provider.CallLog.Locations.LATITUDE;
import static android.provider.CallLog.Locations.LONGITUDE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.adevinta.leku.LocationPickerActivity;
import com.google.gson.Gson;
import com.mohamedabulgasem.datetimepicker.DateTimePicker;
import com.shunyank.appwriteproject.R;
import com.shunyank.appwriteproject.databinding.ActivityCreateEventBinding;
import com.shunyank.appwriteproject.enums.EventStatus;
import com.shunyank.appwriteproject.network.AppWriteHelper;
import com.shunyank.appwriteproject.network.callbacks.DocumentCreateListener;
import com.shunyank.appwriteproject.network.utils.DatabaseUtils;
import com.shunyank.appwriteproject.network.utils.storage.StorageUtils;
import com.shunyank.appwriteproject.utils.ActivityUtils;
import com.shunyank.appwriteproject.utils.Pref;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import io.appwrite.Permission;
import io.appwrite.Role;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import io.appwrite.models.File;
import io.appwrite.models.InputFile;
import io.appwrite.models.UploadProgress;
import io.appwrite.services.Storage;
import kotlin.Result;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function5;


public class CreateEventActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1001;
    ActivityCreateEventBinding binding;
    double geoLat=0 , getLong=0;
    private String address;
    private String postalcode;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private String fileLink="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityUtils.setDarkStatus(this);


        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEventActivity.super.onBackPressed();
            }
        });

        ActivityResultLauncher<Intent> lekuActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Log.d("RESULT****", "OK");
                            Intent data = result.getData();
                            if (data != null) {
                                Log.e("data", new Gson().toJson(data));
                                geoLat = data.getDoubleExtra(LATITUDE, 0.0);
                                Log.e("LATITUDE****", String.valueOf(geoLat));
                                getLong = data.getDoubleExtra(LONGITUDE, 0.0);
                                Log.e("LONGITUDE****", String.valueOf(getLong));
                                address = data.getStringExtra("location_address");
                                Log.e("ADDRESS****", address);
                                postalcode = data.getStringExtra("zipcode");
                                Log.e("POSTALCODE****", postalcode);
                                binding.edtLocation.setText(address);
                            }
                        } else {
                            Log.d("RESULT****", "CANCELLED");
                        }
                    }
                });


        binding.edtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent locationPickerIntent = new LocationPickerActivity.Builder()

    //                        .withLocation(41.4036299, 2.1743558)
//                .withGeolocApiKey("<PUT API KEY HERE>")
                        .withGooglePlacesApiKey("AIzaSyDBdqZz8exoNzJFmgoQ8vGABRvyAZbmfzs")
                        .withSearchZone("es_ES")
                        .withDefaultLocaleSearchZone()
                        .shouldReturnOkOnBackPressed()

                        .withCityHidden()
                        .withSatelliteViewHidden()
                        .withGooglePlacesEnabled()
                        .withGoogleTimeZoneEnabled()
                        .withVoiceSearchHidden()
                        .withUnnamedRoadHidden()
                        .build(getApplicationContext());

                lekuActivityResultLauncher.launch(locationPickerIntent);
            }
        });

        binding.startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DateTimePicker.Builder(CreateEventActivity.this).onDateTimeSetListener(new Function5<Integer, Integer, Integer, Integer, Integer, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, Integer integer2, Integer integer3, Integer integer4, Integer integer5) {
                        //order: (year, month, dayOfMonth, hourOfDay, minute). NB: month is zero-based, Jan is 0; Dec is 11.
                        int year = integer;
                        int month = integer2+1;
                        int dayOfMonth = integer3;
                        int hour = integer4;
                        int mint = integer5;
                        // dd-mm-yyyy hour of day : minutes
                        Log.e("integer",""+integer);
                        Log.e("integer2",""+integer2);
                        Log.e("integer3",""+integer3);
                        Log.e("integer4",""+integer4);
                        Log.e("integer5",""+integer5);

                        String date = dayOfMonth+"-"+month+"-"+year +" "+hour+":"+mint;
                        binding.startDateBtn.setText(date);
                        return null;
                    }
                }).is24HourView(true).build().show();

            }
        });
        binding.endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DateTimePicker.Builder(CreateEventActivity.this).onDateTimeSetListener(new Function5<Integer, Integer, Integer, Integer, Integer, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, Integer integer2, Integer integer3, Integer integer4, Integer integer5) {
                        //order: (year, month, dayOfMonth, hourOfDay, minute). NB: month is zero-based, Jan is 0; Dec is 11.
                        int year = integer;
                        int month = integer2+1;
                        int dayOfMonth = integer3;
                        int hour = integer4;
                        int mint = integer5;
                        String hourString = "";
                        String minString = "";
                        if(hour<10){
                            hourString= "0"+hour;
                        }else {
                            hourString = ""+hour;
                        }
                        if(mint<10){
                            minString= "0"+mint;
                        }else {
                            minString = ""+mint;
                        }
                        // dd-mm-yyyy hour of day : minutes
                        Log.e("integer",""+integer);
                        Log.e("integer2",""+integer2);
                        Log.e("integer3",""+integer3);
                        Log.e("integer4",""+integer4);
                        Log.e("integer5",""+integer5);

                        String date = dayOfMonth+"-"+month+"-"+year +" "+hourString+":"+minString;
                        binding.endDateBtn.setText(date);
                        return null;
                    }
                }).is24HourView(true).build().show();

            }
        });
        binding.continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // open add description page

                String name = binding.edtEventName.getText().toString().trim();
                String org = binding.edtOrg.getText().toString().trim();
                String startDate = binding.startDateBtn.getText().toString().trim();
                String endDate = binding.endDateBtn.getText().toString().trim();
                if(fileLink.isEmpty()){
                    Toast.makeText(CreateEventActivity.this, "Please upload an image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(name.isEmpty()){
                    Toast.makeText(CreateEventActivity.this, "Please enter event name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(org.isEmpty()){
                    Toast.makeText(CreateEventActivity.this, "Please enter organization name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(startDate.isEmpty()){
                    Toast.makeText(CreateEventActivity.this, "Please select start date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(endDate.isEmpty()){
                    Toast.makeText(CreateEventActivity.this, "Please select end date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(geoLat== 0 ||getLong==0||address.trim().isEmpty()){
                    Toast.makeText(CreateEventActivity.this, "Please select address", Toast.LENGTH_SHORT).show();

                }
                if(binding.edtTreeTarget.getText().toString().isEmpty()){
                    Toast.makeText(CreateEventActivity.this, "Please enter tree plantation target", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Integer.parseInt( binding.edtTreeTarget.getText().toString())<50){
                    Toast.makeText(CreateEventActivity.this, "Tree plantation target should be greater than 50", Toast.LENGTH_SHORT).show();
                    return;
                }
                // create Event


                HashMap<Object,Object> dataToUpload = new HashMap<>();

                dataToUpload.put("creator_id", Pref.getBasicUser().getId());
                dataToUpload.put("status", EventStatus.UPCOMING.toString());
                dataToUpload.put("address", address);
                dataToUpload.put("poster_url", fileLink);
                dataToUpload.put("lat", String.valueOf( geoLat));
                dataToUpload.put("long",String.valueOf( getLong));
                dataToUpload.put("organizer", new Gson().toJson(Pref.getBasicUser().setOrgnization(org)).toString());
                dataToUpload.put("start_date", startDate);
                dataToUpload.put("end_date", endDate);
                dataToUpload.put("tree_planted", binding.edtTreeTarget.getText().toString());
                dataToUpload.put("name", name);
                binding.mainLoadingBar.setVisibility(View.VISIBLE);
                DatabaseUtils.createDocument(CreateEventActivity.this, "event_collection", "unique()", dataToUpload, new DocumentCreateListener() {
                    @Override
                    public void onCreatedSuccessfully(Document document) {
                        binding.mainLoadingBar.setVisibility(View.GONE);
                      Intent intent =   new Intent(CreateEventActivity.this,AddEventDescriptionActivity.class);
                      intent.putExtra("doc_id",document.getId());
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onFailed(Result.Failure failure) {
                        binding.mainLoadingBar.setVisibility(View.GONE);
                        Toast.makeText(CreateEventActivity.this, "Couldn't create an event", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onException(AppwriteException exception) {
                        binding.mainLoadingBar.setVisibility(View.GONE);
                        Toast.makeText(CreateEventActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });




            }
        });


        binding.addEventLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // choose image
                imageChooser();

            }
        });
        activityResultLauncher
                = registerForActivityResult(
                new ActivityResultContracts
                        .StartActivityForResult(),
                result -> {
                    if (result.getResultCode()
                            == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // do your operation from here....
                        if (data != null
                                && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            Bitmap selectedImageBitmap = null;
                            try {
                                selectedImageBitmap
                                        = MediaStore.Images.Media.getBitmap(
                                        this.getContentResolver(),
                                        selectedImageUri);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                            binding.eventPoster.setVisibility(View.VISIBLE);
                            binding.eventPoster.setImageBitmap(
                                    selectedImageBitmap);
                            binding.addButtonLayout.setVisibility(View.GONE);
                            // uploadImage
                            uploadImage(selectedImageUri,selectedImageBitmap);
                        }
                    }
                });

    }

    private void uploadImage(Uri selectedImageUri, Bitmap selectedImageBitmap) {
        Storage storage = new Storage(AppWriteHelper.getClient());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String uniqueString = UUID.randomUUID().toString();
        String extention = GetFileExtension(selectedImageUri);
        Log.e("extention",extention);
        InputFile inputFile = InputFile.Companion.fromBytes(   byteArray,uniqueString+"."+extention,"image/"+extention);
        try {

            storage.createFile("event-posters", "unique()", inputFile, null,
                    uploadProgress -> {
                        Log.e("progress",uploadProgress+"");
                        double progress   = uploadProgress.getProgress();
                        binding.uploadProgressbar.setProgress((int) progress);
                        if(progress<100){
                            binding.uploading.setVisibility(View.VISIBLE);
                        }
                        else if(progress==100.0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO: 26/02/23 Create link to access the image
                                    binding.uploading.setVisibility(View.GONE);

                                    Toast.makeText(CreateEventActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            binding.uploading.setVisibility(View.GONE);
                        }
                        return null;
                    }
                    ,

                    new Continuation<io.appwrite.models.File>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object o) {
                        if(o instanceof Result.Failure){
                            ((Result.Failure) o).exception.printStackTrace();
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.uploading.setVisibility(View.GONE);
                                    File file = (File) o;
                                    fileLink = StorageUtils.fileLink("event-posters",file.getId());
                                    Log.e("file url",StorageUtils.fileLink("event-posters",file.getId()));
                                    Log.e("result",new Gson().toJson(o));
                                }
                            });

                        }
                }
            });
        } catch (AppwriteException e) {
            throw new RuntimeException(e);
        }
    }

    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(i);

        // pass the constant to compare it
        // with the returned requestCode
//        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK) {
//
//            // compare the resultCode with the
//            // SELECT_PICTURE constant
//            if (requestCode == SELECT_PICTURE) {
//                // Get the url of the image from data
//                Uri selectedImageUri = data.getData();
//                if (null != selectedImageUri) {
//                    // update the preview image in the layout
//                    binding.eventPoster.setImageURI(selectedImageUri);
//                }
//            }
//        }
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Get Extension
    public String GetFileExtension(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();

        // Return file Extension
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}