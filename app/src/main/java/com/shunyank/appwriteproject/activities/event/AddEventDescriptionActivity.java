package com.shunyank.appwriteproject.activities.event;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shunyank.appwriteproject.R;
import com.shunyank.appwriteproject.databinding.ActivityAddEventDescriptionBinding;
import com.shunyank.appwriteproject.databinding.ActivityCreateEventBinding;
import com.shunyank.appwriteproject.network.callbacks.DocumentUpdateListener;
import com.shunyank.appwriteproject.network.utils.DatabaseUtils;
import com.shunyank.appwriteproject.utils.ActivityUtils;

import java.util.HashMap;

import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import jp.wasabeef.richeditor.RichEditor;
import kotlin.Result;

public class AddEventDescriptionActivity extends AppCompatActivity {
    ActivityAddEventDescriptionBinding binding;
    String docId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEventDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityUtils.setDarkStatus(this);

        RichEditor mEditor  = new RichEditor(this);
        mEditor.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mEditor.setPadding(10,10,10,10);
        mEditor.setBackgroundColor(getColor(R.color.ofwhite));
        mEditor.setTextColor(getResources().getColor(R.color.text_grey_color));
        mEditor.setHtml("Enter Description here...");
//        mEditor.setFontSize(7);
        binding.view.addView(mEditor);


        // get docId
        docId = getIntent().getStringExtra("doc_id");





//        mEditor.setFontSize(7);
//        boolean firstTime = true;
        mEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditor.setTextColor(getResources().getColor(R.color.editor_black));
            }
        });
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {

            }
        });
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save event
                HashMap<Object,Object> dataToUpload = new HashMap<>();
                dataToUpload.put("description",mEditor.getHtml().toString());

                DatabaseUtils.updateDocument("event_collection", docId, dataToUpload, new DocumentUpdateListener() {
                    @Override
                    public void onUpdatedSuccessfully(Document document) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddEventDescriptionActivity.this, "Description added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                    }

                    @Override
                    public void onFailed(Result.Failure failure) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddEventDescriptionActivity.this, "Couldn't add description", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }

                    @Override
                    public void onException(AppwriteException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddEventDescriptionActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                });


            }
        });
        binding.undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        binding.redoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        binding.boldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        binding.h1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        binding.h2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        binding.h5Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        binding.linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddEventDescriptionActivity.super.onBackPressed();
            }
        });
    }
}