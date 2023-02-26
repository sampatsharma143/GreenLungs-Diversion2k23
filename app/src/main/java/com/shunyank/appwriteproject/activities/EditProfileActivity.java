package com.shunyank.appwriteproject.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.shunyank.appwriteproject.R;
import com.shunyank.appwriteproject.databinding.ActivityEditProfileBinding;
import com.shunyank.appwriteproject.enums.UserLevel;
import com.shunyank.appwriteproject.models.BasicUser;
import com.shunyank.appwriteproject.models.CompleteUserModel;
import com.shunyank.appwriteproject.network.AppWriteHelper;
import com.shunyank.appwriteproject.network.Constants;
import com.shunyank.appwriteproject.network.callbacks.DocumentCreateListener;
import com.shunyank.appwriteproject.network.utils.DatabaseUtils;
import com.shunyank.appwriteproject.utils.Pref;
import com.squareup.picasso.Picasso;

import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import io.appwrite.services.Avatars;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    private BasicUser user;
    int CAN_UPDATE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String title = getIntent().getStringExtra("title");
        CAN_UPDATE = getIntent().getIntExtra("type",1);
        // 1 = can update profile , visiting second time 
        // 0 =  can not update , after account create 
        binding.title.setText(title);

         user = Pref.getBasicUser();

        binding.userName.setText(user.getName());
        binding.userEmail.setText(user.getEmail());

        if(CAN_UPDATE==1){
            fetchProfile();
        }
        
        setProfile();
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.mainLoadingBar.setVisibility(View.GONE);

                // validate data


                final String regexStr = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";

                String mb = binding.edtContact.getText().toString();

                if (!mb.matches(regexStr)) {

                    Toast.makeText(EditProfileActivity.this,
                            "Please enter Valid Mobile Number", Toast.LENGTH_LONG).show();

                    return;
                }
                if(binding.edtAge.getText().toString().isEmpty()){
                    Toast.makeText(EditProfileActivity.this,
                            "Please enter your age", Toast.LENGTH_LONG).show();

                    return;
                }
                if(CAN_UPDATE==0) {
                    saveProfile();
                }else {
                    updateProfile();
                }
            }
        });
    }

    private void updateProfile() {
    }

    private void fetchProfile() {
    }

    private void saveProfile() {
        String org =    binding.edtOrg.getText().toString().trim();
        if(org.isEmpty()){
            org  = "no_org";
        }
//        Log.e("age",Integer.parseInt(  )+"");
        binding.mainLoadingBar.setVisibility(View.VISIBLE);
        CompleteUserModel completeUserModel = new CompleteUserModel(
               user.getName(),
               user.getEmail(),
                binding.edtAge.getText().toString(),
                binding.edtContact.getText().toString(),
                null,

                org,
                UserLevel.SPROUT.name().toString()
        );

        DatabaseUtils.createDocument(this, Constants.UserCollectionId,user.getId(), DatabaseUtils.convertToHashmap(completeUserModel), new DocumentCreateListener() {
            @Override
            public void onCreatedSuccessfully(Document document) {
                Toast.makeText(EditProfileActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                binding.mainLoadingBar.setVisibility(View.GONE);
                finish();
            }

            @Override
            public void onFailed(Result.Failure failure) {
                Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                binding.mainLoadingBar.setVisibility(View.GONE);

            }

            @Override
            public void onException(AppwriteException exception) {
                Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                binding.mainLoadingBar.setVisibility(View.GONE);
            }
        });

    }

    private void setProfile() {
        Avatars avatars = new Avatars(AppWriteHelper.getClient());

        try {
            avatars.getInitials(new Continuation<byte[]>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object o) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                    if(o instanceof Result.Failure){
                        Toast.makeText(EditProfileActivity.this, "Unable to load profile", Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                    }else {

                                byte[] bytes = (byte[]) o;
//                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
//                            binding.profileImage.setImageBitmap(bitmap);

                        binding.profileImage.setVisibility(View.VISIBLE);

                        Glide.with(binding.getRoot().getContext()).load(bytes).into(binding.profileImage);
                        binding.progressBar.setVisibility(View.GONE);
                    }

                    }
                    });

                }
            });
        } catch (AppwriteException e) {
            throw new RuntimeException(e);
        }
        
        // other details 
        
        
        
    }
}