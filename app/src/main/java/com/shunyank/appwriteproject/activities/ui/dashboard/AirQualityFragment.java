package com.shunyank.appwriteproject.activities.ui.dashboard;

import android.Manifest;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.shunyank.appwriteproject.R;
import com.shunyank.appwriteproject.databinding.FragmentAirqualityBinding;
import com.shunyank.appwriteproject.models.Components;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class AirQualityFragment extends Fragment {

    private FragmentAirqualityBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private HashMap<Object, Object> qualityList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentAirqualityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        qualityList = new HashMap<>();
        qualityList.put(1,new AirQuality(requireActivity().getColor(R.color.good),"Good"));
        qualityList.put(2,new AirQuality(requireActivity().getColor(R.color.fair),"Fair"));
        qualityList.put(3,new AirQuality(requireActivity().getColor(R.color.moderate),"Moderate"));
        qualityList.put(4,new AirQuality(requireActivity().getColor(R.color.poor),"Poor"));
        qualityList.put(5,new AirQuality(requireActivity().getColor(R.color.very_poor),"Very Poor"));

        binding.airLeaf.setVisibility(View.GONE);
        binding.titleText.setVisibility(View.GONE);
        binding.retroLoadingBar.setVisibility(View.VISIBLE);

//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

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
                                binding.titleText.setVisibility(View.VISIBLE);
                                binding.titleText.setText(getAddressFromLatLng(requireContext(),lat,geoLong));

                                fetchQuality(lat,geoLong);
                                // Logic to handle location object
                            }
                        }
                    });
        }

        return root;
    }

    private void fetchQuality(double lat, double geoLong) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                 JSONObject jsonData =    getJSONObjectFromURL("https://api.openweathermap.org/data/2.5/air_pollution?lat="+lat+"&lon="+geoLong+"&appid=c2d67f2a2116bd23bbf207b6917e91b6");

                int aiq = jsonData.getJSONArray("list").getJSONObject(0).getJSONObject("main").getInt("aqi");

                 Log.e("jsondata", new Gson().toJson( qualityList.get( aiq)));
                 AirQuality airQuality = (AirQuality) qualityList.get(aiq);
                    Components components = new Gson().fromJson(jsonData.getJSONArray("list").getJSONObject(0).getJSONObject("components").toString(),Components.class);
                 getActivity().runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         binding.retroLoadingBar.setVisibility(View.GONE);
                         binding.qualityText.setVisibility(View.VISIBLE);
                         binding.airLeaf.setVisibility(View.VISIBLE);

                            Log.e("components",new Gson().toJson(components));

                            binding.coText.setText(String.valueOf( components.co));
                            binding.sulphurText.setText(String.valueOf( components.so2));
                            binding.ozText.setText(String.valueOf(components.o3 ));
                            binding.airAqiText.setText(String.valueOf( aiq));
                         if(aiq==5){
                             binding.airLeaf.setImageTintList( ColorStateList.valueOf( requireActivity().getColor(R.color.very_poor)));

                         }
                         if(aiq==4){
                             binding.airLeaf.setImageTintList( ColorStateList.valueOf( requireActivity().getColor(R.color.poor)));

                         }
                         if(aiq==3){
                             binding.airLeaf.setImageTintList( ColorStateList.valueOf( requireActivity().getColor(R.color.moderate)));

                         }
                         if(aiq==2){
                             binding.airLeaf.setImageTintList( ColorStateList.valueOf( requireActivity().getColor(R.color.fair)));

                         }
                         if(aiq==1){
                             binding.airLeaf.setImageTintList( ColorStateList.valueOf( requireActivity().getColor(R.color.good)));

                         }
                         binding.qualityText.setText(airQuality.name);
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
    public static class AirQuality{
        public int color;
        public String name;

        public int getColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        public AirQuality(int color, String name) {
            this.color = color;
            this.name = name;
        }
    }
}