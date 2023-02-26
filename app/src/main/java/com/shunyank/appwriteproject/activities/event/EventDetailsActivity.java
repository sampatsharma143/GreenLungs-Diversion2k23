package com.shunyank.appwriteproject.activities.event;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shunyank.appwriteproject.activities.ui.dashboard.AirQualityFragment;
import com.shunyank.appwriteproject.databinding.ActivityEventDetailsBinding;
import com.shunyank.appwriteproject.enums.EventStatus;
import com.shunyank.appwriteproject.models.BasicUser;
import com.shunyank.appwriteproject.models.EventModel;
import com.shunyank.appwriteproject.network.Constants;
import com.shunyank.appwriteproject.network.callbacks.DocumentFetchListener;
import com.shunyank.appwriteproject.network.callbacks.DocumentUpdateListener;
import com.shunyank.appwriteproject.network.utils.DatabaseUtils;
import com.shunyank.appwriteproject.utils.ActivityUtils;
import com.shunyank.appwriteproject.utils.Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import kotlin.Result;

public class EventDetailsActivity extends AppCompatActivity {

    ActivityEventDetailsBinding binding;
    private boolean canEdit;
    private boolean canJoin=false;
    private EventModel model;
    String eventStatus = "";
    private boolean canCancel =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String eventId = getIntent().getStringExtra("event_id");
         canEdit = getIntent().getBooleanExtra("can_edit",false);
        if(canEdit){
            binding.deleteEvent.setVisibility(View.VISIBLE);
        }else {
            binding.deleteEvent.setVisibility(View.GONE);
            checkStatus(Pref.getBasicUser().getId());
        }
        ActivityUtils.setDarkStatus(this);
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventDetailsActivity.super.onBackPressed();
            }
        });
         fetchEvent(eventId);


         binding.shareButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String urlToShare = "https://greenlungs.co/events?eventID="+eventId;
                 final Intent intent = new Intent(Intent.ACTION_SEND);
                 intent.setType("text/plain");
// intent.putExtra(Intent.EXTRA_SUBJECT, "If any extra"); // NB: has no effect!
                 intent.putExtra(Intent.EXTRA_TEXT, "I am joining this plantation event on greenlungs.co, You can also join.\nClick the link to check out the event\n\n" + urlToShare);
                startActivity(intent);
             }
         });
         binding.eventStatus.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(model!=null) {
                     if (canJoin) {
                         if(model.volunteers_list!=null){

                             try {
                                 JSONArray jsonArray = new JSONArray(model.volunteers_list);
                                 jsonArray.put(Pref.getBasicUser());
                                 int newcount = Integer.parseInt( model.volunteers_count)+1;
                                 HashMap<Object,Object> data = new HashMap<>();
                                 data.put("volunteers_count",String.valueOf(newcount ));
                                 data.put("volunteers_list",new Gson().toJson(jsonArray).toString());
                                 binding.progressBar.setVisibility(View.VISIBLE);
                                 DatabaseUtils.updateDocument(Constants.EventCollection, eventId, data, new DocumentUpdateListener() {
                                     @Override
                                     public void onUpdatedSuccessfully(Document document) {
                                         runOnUiThread(new Runnable() {
                                             @Override
                                             public void run() {
                                                 binding.progressBar.setVisibility(View.GONE);
                                                 updateUserLevel(100);
                                                 Toast.makeText(EventDetailsActivity.this, "Joined Successfully", Toast.LENGTH_SHORT).show();
                                                fetchEvent(eventId);
                                             }
                                         });
                                     }

                                     @Override
                                     public void onFailed(Result.Failure failure) {
                                         runOnUiThread(new Runnable() {
                                             @Override
                                             public void run() {
                                                 binding.progressBar.setVisibility(View.GONE);

                                             }
                                         });
                                     }

                                     @Override
                                     public void onException(AppwriteException exception) {
                                         runOnUiThread(new Runnable() {
                                             @Override
                                             public void run() {
                                                 binding.progressBar.setVisibility(View.GONE);
                                             }
                                         });

                                     }
                                 });

                             } catch (JSONException e) {
                                 throw new RuntimeException(e);
                             }

                         }else {
                             ArrayList<BasicUser> list = new ArrayList<>();
                             list.add(Pref.getBasicUser());
                             int newcount = Integer.parseInt( model.volunteers_count)+1;
                             HashMap<Object,Object> data = new HashMap<>();
                             data.put("volunteers_count",String.valueOf(newcount ));
                             data.put("volunteers_list",new Gson().toJson(list).toString());
                             binding.progressBar.setVisibility(View.VISIBLE);
                             DatabaseUtils.updateDocument(Constants.EventCollection, eventId, data, new DocumentUpdateListener() {
                                 @Override
                                 public void onUpdatedSuccessfully(Document document) {
                                     runOnUiThread(new Runnable() {
                                         @Override
                                         public void run() {
                                             binding.progressBar.setVisibility(View.GONE);
                                             fetchEvent(eventId);
                                              updateUserLevel(100);
                                             Toast.makeText(EventDetailsActivity.this, "Joined Successfully", Toast.LENGTH_SHORT).show();

                                         }
                                     });
                                 }

                                 @Override
                                 public void onFailed(Result.Failure failure) {
                                     runOnUiThread(new Runnable() {
                                         @Override
                                         public void run() {
                                             binding.progressBar.setVisibility(View.GONE);

                                         }
                                     });
                                 }

                                 @Override
                                 public void onException(AppwriteException exception) {
                                     runOnUiThread(new Runnable() {
                                         @Override
                                         public void run() {
                                             binding.progressBar.setVisibility(View.GONE);
                                         }
                                     });

                                 }
                             });

                         }


                     } else if (canEdit) {
                         if(EventStatus.UPCOMING==EventStatus.valueOf(eventStatus)){

//                             binding.eventStatus.setText("Start Event");
                             HashMap<Object,Object> data = new HashMap<>();
                             data.put("status",EventStatus.ONGOING.toString());
                             updateEventStatus(data,eventId);

                         }
                         else  if(EventStatus.ONGOING==EventStatus.valueOf(eventStatus)) {

                             HashMap<Object,Object> data = new HashMap<>();
                             data.put("status",EventStatus.ENDED.toString());
                             updateEventStatus(data,eventId);
                         }
                         else {
                             binding.eventStatus.setText("Event Ended");

                             binding.eventStatus.setClickable( false);
                         }


                     }
                 }
             }
         });

    }

    private void updateUserLevel(int i) {
        HashMap<Object,Object> data = new HashMap<>();
        data.put("points",""+i);
        DatabaseUtils.updateDocument(Constants.UserCollectionId, Pref.getBasicUser().getId(), data, new DocumentUpdateListener() {
            @Override
            public void onUpdatedSuccessfully(Document document) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EventDetailsActivity.this, i+" Points Added", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailed(Result.Failure failure) {

            }

            @Override
            public void onException(AppwriteException exception) {

            }
        });
    }

    private void checkStatus(String id) {
        // fetch all the valintors

//        DatabaseUtils.fet
    }

    private void updateEventStatus(HashMap<Object,Object> data,String eventId){
        DatabaseUtils.updateDocument(Constants.EventCollection, eventId, data, new DocumentUpdateListener() {
            @Override
            public void onUpdatedSuccessfully(Document document) {
                fetchEvent(eventId);
            }

            @Override
            public void onFailed(Result.Failure failure) {

            }

            @Override
            public void onException(AppwriteException exception) {

            }
        });
    }

    private void fetchEvent(String eventId) {
        binding.progressBar.setVisibility(View.VISIBLE);

        DatabaseUtils.fetchDocument(Constants.EventCollection, eventId, new DocumentFetchListener() {
            @Override
            public void onFetchSuccessfully(Document document) {
                //
                if(document!=null){
                    // show
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                             binding.eventStatus.setVisibility(View.VISIBLE);
                             model = DatabaseUtils.convertToModel(document,EventModel.class);
                            setData(model);
                            checkMyStatus(model);
                            binding.progressBar.setVisibility(View.GONE);

                        }
                    });
                }
            }

            @Override
            public void onFailed(Result.Failure failure) {
                failure.exception.printStackTrace();
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onException(AppwriteException exception) {
                exception.printStackTrace();
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void checkMyStatus(EventModel model) {

        eventStatus = model.status;


        if(model.volunteers_list!=null){

            try {
                JSONArray jsonArray = new JSONArray(model.volunteers_list);

                for (int i =0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    if(id.contentEquals(Pref.getBasicUser().getId())){
                        canJoin = false;
                        binding.eventStatus.setText("Already Joined!");
                        break;
                    }
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }else if(!model.creator_id.contentEquals(Pref.getBasicUser().getId())) {
            binding.eventStatus.setText("Join");
            canJoin = true;
        }else {
            if(EventStatus.UPCOMING==EventStatus.valueOf(eventStatus)){

                binding.eventStatus.setText("Start Event");
            }
            else  if(EventStatus.ONGOING==EventStatus.valueOf(eventStatus)) {

                binding.eventStatus.setText("End Event");

            }
            else {
                binding.eventStatus.setText("Event Ended");

                binding.eventStatus.setClickable( false);
            }

        }
    }

    private void setData(EventModel model) {
        Glide.with(EventDetailsActivity.this).load(model.poster_url).into(binding.eventPosterImage);
        binding.titleText.setText(model.name);
        binding.statusText.setText(model.status);

        BasicUser basicUser = new Gson().fromJson(model.organizer,BasicUser.class);

        binding.organizationName.setText(basicUser.getOrg());
        binding.organizerUserName.setText(basicUser.getName());
        binding.volunteersCounting.setText(model.volunteers_count +" Volunteers");

        binding.treePlanted.setText(model.tree_planted +" Trees");
        binding.startDateTime.setText(model.start_date);
        binding.endDateTime.setText(model.end_date);
        binding.addresss.setText(AirQualityFragment.getAddressFromLatLng(this,Double.parseDouble(model.lat),Double.parseDouble(model.geoLong)));
        binding.mapOpener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String my_data= String.format(Locale.ENGLISH, "http://maps.google.com/?q="+model.lat+","+model.geoLong);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(my_data));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
    }
}