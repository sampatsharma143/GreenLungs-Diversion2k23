package com.shunyank.appwriteproject.activities.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.shunyank.appwriteproject.R;
import com.shunyank.appwriteproject.activities.AirQualityActivity;
import com.shunyank.appwriteproject.activities.NotificationActivity;
import com.shunyank.appwriteproject.activities.event.CreateEventActivity;
import com.shunyank.appwriteproject.activities.EditProfileActivity;
import com.shunyank.appwriteproject.activities.event.EventDetailsActivity;
import com.shunyank.appwriteproject.activities.ui.dashboard.AirQualityFragment;
import com.shunyank.appwriteproject.adapters.MyEventsAdapter;
import com.shunyank.appwriteproject.adapters.OtherEventsAdapter;
import com.shunyank.appwriteproject.databinding.FragmentHomeBinding;
import com.shunyank.appwriteproject.models.BasicUser;
import com.shunyank.appwriteproject.models.Components;
import com.shunyank.appwriteproject.models.MyEventModel;
import com.shunyank.appwriteproject.network.Constants;
import com.shunyank.appwriteproject.network.callbacks.DocumentFetchListener;
import com.shunyank.appwriteproject.network.callbacks.DocumentListFetchListener;
import com.shunyank.appwriteproject.network.utils.DatabaseUtils;
import com.shunyank.appwriteproject.utils.Greetings;
import com.shunyank.appwriteproject.utils.Pref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.appwrite.Query;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import kotlin.Result;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HashMap<Object, Object> qualityList;
    private FusedLocationProviderClient fusedLocationClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false);
        binding.myEventsRyc.setLayoutManager(layoutManager);

        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false);
        binding.otherEventsRyc.setLayoutManager(layoutManager1);

        binding.notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity(), NotificationActivity.class));
            }
        });
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        qualityList = new HashMap<>();
        qualityList.put(1,new AirQualityFragment.AirQuality(requireActivity().getColor(R.color.good),"Good"));
        qualityList.put(2,new AirQualityFragment.AirQuality(requireActivity().getColor(R.color.fair),"Fair"));
        qualityList.put(3,new AirQualityFragment.AirQuality(requireActivity().getColor(R.color.moderate),"Moderate"));
        qualityList.put(4,new AirQualityFragment.AirQuality(requireActivity().getColor(R.color.poor),"Poor"));
        qualityList.put(5,new AirQualityFragment.AirQuality(requireActivity().getColor(R.color.very_poor),"Very Poor"));

        BasicUser basicUser = Pref.getBasicUser();
        binding.greeting.setText(   Greetings.getGreetings());
        binding.userName.setText(basicUser.getName());

        binding.aqiLinearLayout.setVisibility(View.GONE);
        binding.aqiLoadingBar.setVisibility(View.VISIBLE);
        binding.airQualityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 26/02/23 slide to second fragment
//                Navigation.findNavController(view).navigate(R.id.navigation_dashboard);

            }
        });
        findUserProfile();
        findOtherEvents();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String[] PERMISSIONS = {

                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            ActivityCompat.requestPermissions(requireActivity(),PERMISSIONS,1001);

        }else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                double lat = location.getLatitude();
                                double geoLong = location.getLongitude();

                                Log.e("lat", lat + "");
                                Log.e("geoLong", geoLong + "");
                                binding.addresss.setVisibility(View.VISIBLE);
                                binding.addresss.setText(getAddressFromLatLng(requireContext(),lat,geoLong));

                                fetchQuality(lat,geoLong);
                                // Logic to handle location object
                            }
                        }
                    });
        }
        
        
        return root;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            double lat = location.getLatitude();
                            double geoLong = location.getLongitude();

                            Log.e("lat", lat + "");
                            Log.e("geoLong", geoLong + "");
                            binding.addresss.setVisibility(View.VISIBLE);
                            binding.addresss.setText(getAddressFromLatLng(requireContext(),lat,geoLong));

                            fetchQuality(lat,geoLong);
                            // Logic to handle location object
                        }
                    }
                });
    }

    private void findOtherEvents() {
        List<String> searchQuery = new ArrayList<>();
        searchQuery.add(Query.Companion.notEqual("creator_id",Pref.getBasicUser().getId()));
        DatabaseUtils.fetchDocuments(Constants.EventCollection, searchQuery, new DocumentListFetchListener() {
            @Override
            public void onFetchSuccessfully(List<Document> documents) {
                //

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("document size",documents.size()+"");

                        //                        data.add(null);
                        ArrayList<MyEventModel> data = new ArrayList<>(DatabaseUtils.convertToModelList(documents, MyEventModel.class));
                        OtherEventsAdapter adapter = new OtherEventsAdapter();
                        adapter.setData(data);
                        adapter.setClickHandler(new OtherEventsAdapter.ClickHandler() {
                            @Override
                            public void onClick(MyEventModel myEventModel) {

                                    Intent intent =    new Intent(requireActivity(), EventDetailsActivity.class);
                                    intent.putExtra("event_id",myEventModel.id);
                                    intent.putExtra("can_edit",false);
                                    startActivity(intent);

                            }
                        });
                        binding.otherEventsRyc.setAdapter(adapter);
                    }
                });


            }

            @Override
            public void onFailed(Result.Failure failure) {
                failure.exception.printStackTrace();
            }

            @Override
            public void onException(AppwriteException exception) {
                exception.printStackTrace();
            }
        });



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void findUserProfile() {
        List<String> queries = new ArrayList<>();

        BasicUser basicUser = Pref.getBasicUser();

//        queries.add(Query.Companion.equal("$id",basicUser.getId()))
        DatabaseUtils.fetchDocument(Constants.UserCollectionId, basicUser.getId(), new DocumentFetchListener() {
            @Override
            public void onFetchSuccessfully(Document document) {
                // document size
                if(document!=null) {


                    fetchMyEvents(basicUser.getId());

                }
            }

            @Override
            public void onFailed(Result.Failure failure) {
                failure.exception.printStackTrace();
                Intent editProfileIntent = new Intent(requireActivity(), EditProfileActivity.class);
                editProfileIntent.putExtra("title","Complete Profile");
                editProfileIntent.putExtra("type",0);
                startActivity(editProfileIntent);
//                Log.e("cause", failure.exception.getCause().getMessage().toString());
            }

            @Override
            public void onException(AppwriteException exception) {
                Toast.makeText(requireActivity(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMyEvents(String id) {
        List<String> searchQuery = new ArrayList<>();
        searchQuery.add(Query.Companion.equal("creator_id",id));
        DatabaseUtils.fetchDocuments(Constants.EventCollection, searchQuery, new DocumentListFetchListener() {
            @Override
            public void onFetchSuccessfully(List<Document> documents) {
                //

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("document size",documents.size()+"");

                        ArrayList<MyEventModel> data = new ArrayList<>();
                        data.add(null);
                        data.addAll(     DatabaseUtils.convertToModelList(documents,MyEventModel.class));
                        MyEventsAdapter adapter = new MyEventsAdapter();
                        adapter.setData(data);
                        adapter.setClickHandler(new MyEventsAdapter.ClickHandler() {
                            @Override
                            public void onClick(MyEventModel myEventModel) {
                                if(myEventModel==null){
                                startActivity(new Intent(requireActivity(), CreateEventActivity.class));

                                }else {
                                    Intent intent =    new Intent(requireActivity(), EventDetailsActivity.class);
                                    intent.putExtra("event_id",myEventModel.id);
                                    intent.putExtra("can_edit",true);
                                    startActivity(intent);

                                }
                            }
                        });
                        binding.myEventsRyc.setAdapter(adapter);
                    }
                });


            }

            @Override
            public void onFailed(Result.Failure failure) {
                failure.exception.printStackTrace();
            }

            @Override
            public void onException(AppwriteException exception) {
                exception.printStackTrace();
            }
        });
        
        

    }

    
    // air quality section 
    private void fetchQuality(double lat, double geoLong) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonData =    getJSONObjectFromURL("https://api.openweathermap.org/data/2.5/air_pollution?lat="+lat+"&lon="+geoLong+"&appid=c2d67f2a2116bd23bbf207b6917e91b6");

                    int aiq = jsonData.getJSONArray("list").getJSONObject(0).getJSONObject("main").getInt("aqi");

                    Log.e("jsondata", new Gson().toJson( qualityList.get( aiq)));
                    AirQualityFragment.AirQuality airQuality = (AirQualityFragment.AirQuality) qualityList.get(aiq);
                    Components components = new Gson().fromJson(jsonData.getJSONArray("list").getJSONObject(0).getJSONObject("components").toString(),Components.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            binding.retroLoadingBar.setVisibility(View.GONE);
//                            binding.qualityText.setVisibility(View.VISIBLE);
//                            binding.airLeaf.setVisibility(View.VISIBLE);

                            Log.e("components",new Gson().toJson(components));

                            binding.aqiLinearLayout.setVisibility(View.VISIBLE);
                            binding.aqiLoadingBar.setVisibility(View.GONE);
                            binding.aqiText.setText(String.valueOf( aiq));
                            if(aiq==5){
                                binding.leafAqi.setImageTintList( ColorStateList.valueOf( requireActivity().getColor(R.color.very_poor)));

                            }
                            if(aiq==4){
                                binding.leafAqi.setImageTintList( ColorStateList.valueOf( requireActivity().getColor(R.color.poor)));

                            }
                            if(aiq==3){
                                binding.leafAqi.setImageTintList( ColorStateList.valueOf( requireActivity().getColor(R.color.moderate)));

                            }
                            if(aiq==2){
                                binding.leafAqi.setImageTintList( ColorStateList.valueOf( requireActivity().getColor(R.color.fair)));

                            }
                            if(aiq==1){
                                binding.leafAqi.setImageTintList( ColorStateList.valueOf( requireActivity().getColor(R.color.good)));

                            }
                            binding.aqiStatus.setText(airQuality.name);
                        }
                    });

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public static String getAddressFromLatLng(Context context, double lat, double geoLong) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, geoLong, 1);
            String address = addresses.get(0).getLocality() +","+addresses.get(0).getAdminArea()+","+addresses.get(0).getCountryName();
//            return addresses.get(0).getAddressLine(0);
            return address;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
   
    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();
        System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }
   
}