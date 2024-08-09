package com.example.layersdiseasedetection.ui.chats;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;



import com.example.layersdiseasedetection.MainContactsAdapter;
import com.example.layersdiseasedetection.R;
import com.example.layersdiseasedetection.data.UserDetails;
import com.example.layersdiseasedetection.Assets.OpenRouteService;
import com.example.layersdiseasedetection.Assets.RouteResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class FragmentChat extends Fragment {

    private RecyclerView contactsRecyclerView;
    private DatabaseReference contactsRef;
    private MainContactsAdapter contactsAdapter;
    private List<UserDetails> userList;
    private TextView TVDisplaycategory;

    ImageButton btnToChat;
    private MapView mMap;
    private Button buttonGetDirections;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserCategory;
    private double currentUserLatitude;
    private double currentUserLongitude;
    private UserDetails nearestUser;

    private ExecutorService executorService;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    LatLng userLocation; // Store user location
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationClient; // Initialize FusedLocationProviderClient

    private LocationRequest locationRequest;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        contactsRef = FirebaseDatabase.getInstance().getReference("Users");
        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        // Initialize views
        TVDisplaycategory = root.findViewById(R.id.TVcategoryDisplay);
        contactsRecyclerView = root.findViewById(R.id.rvContacts);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        contactsAdapter = new MainContactsAdapter(userList);
        contactsRecyclerView.setAdapter(contactsAdapter);



        // Initialize map and button
        mMap = root.findViewById(R.id.map);
        buttonGetDirections = root.findViewById(R.id.button_get_directions);

        // Configure map
        //public void setBuiltInZoomControls(boolean on);
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osmdroid", 0));
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setBuiltInZoomControls(true);
        mMap.setMultiTouchControls(true);
        IMapController mapController = mMap.getController();
        mapController.setZoom(10.0);

        // Set up RecyclerView click listener
        contactsAdapter.setOnItemClickListener(user -> {
            // Code to handle item click, if needed
        });

        // Load user category and refresh user list
        if (currentUser != null) {
            retrieveUserCategory();
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            // Optionally, redirect to login screen
        }

        // Set up directions button
        buttonGetDirections.setOnClickListener(v -> {
            if (nearestUser != null) {
                showRouteOnMap();
            } else {
                Toast.makeText(requireContext(), "No nearest user available", Toast.LENGTH_SHORT).show();
            }
        });

        // Check for location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            //startLocationUpdates();
        }

        return root;
    }

    private void retrieveUserCategory() {
        DatabaseReference currentUserRef = contactsRef.child(currentUser.getUid());
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                  //  updateMapWithLocation();
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Start location updates
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }

        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserCategory = snapshot.child("userCategory").getValue(String.class);
                    currentUserLatitude =userLocation.latitude;
                    currentUserLongitude =userLocation.longitude;

                    refreshUserList();
                } else {
                    Log.d("FragmentChat", "User category not found");
                    Toast.makeText(requireContext(), "User category not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshUserList() {
        if (currentUserCategory == null || currentUserCategory.isEmpty()) {
            return;
        }

        String oppositeCategory = currentUserCategory.equals("Farmer") ? "Veterinary Officer" : "Farmer";
        Query userQuery = contactsRef.orderByChild("userCategory").equalTo(oppositeCategory);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                nearestUser = null;
                double minDistance = Double.MAX_VALUE;

                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    UserDetails userInfo = contactSnapshot.getValue(UserDetails.class);
                    if (userInfo != null) {
                        double distance = calculateDistance(currentUserLatitude, currentUserLongitude, userInfo.getLatitude(), userInfo.getLongitude());
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestUser = userInfo;
                        }
                    }
                }

                if (nearestUser != null) {
                    userList.add(nearestUser);
                    TVDisplaycategory.setText("Nearest " + oppositeCategory + ": " + nearestUser.getUsername());
                    showUserLocationOnMap(); // Call to show both users on the map
                } else {
                    TVDisplaycategory.setText("No " + oppositeCategory + " found");
                }
                contactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load user list", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    private void showRouteOnMap() {
        if (nearestUser == null) {
            Toast.makeText(requireContext(), "No nearest user found", Toast.LENGTH_SHORT).show();
            return;
        }

        GeoPoint startPoint = new GeoPoint(currentUserLatitude, currentUserLongitude);
        GeoPoint endPoint = new GeoPoint(nearestUser.getLatitude(), nearestUser.getLongitude());

        addMarker(startPoint, "You");
        addMarker(endPoint, nearestUser.getUsername());

        requestDirections(startPoint, endPoint);
    }

    private void addMarker(GeoPoint point, String title) {
        if (point != null) {
            Marker marker = new Marker(mMap);
            marker.setPosition(point);
            marker.setTitle(title);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mMap.getOverlays().add(marker);
            mMap.invalidate();

        }}

    private void requestDirections(GeoPoint start, GeoPoint end) {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }

        executorService.execute(() -> {
            String startCoords = start.getLongitude() + "," + start.getLatitude();
            String endCoords = end.getLongitude() + "," + end.getLatitude();

            Log.d("Coordinates", "Start: " + startCoords + ", End: " + endCoords);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openrouteservice.org/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            OpenRouteService service = retrofit.create(OpenRouteService.class);

            String apiKey = "YOUR_API_KEY_HERE"; // Replace with your actual API key

            Call<RouteResponse> call = service.getDirections(apiKey, startCoords, endCoords);

            call.enqueue(new Callback<RouteResponse>() {
                @Override
                public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        RouteResponse routeResponse = response.body();
                        String polyline = routeResponse.getRoutes().get(0).getGeometry().getPolyline();
                        List<GeoPoint> routePoints = decodePolyline(polyline);

                        requireActivity().runOnUiThread(() -> drawRouteOnMap(routePoints));
                    } else {
                        Log.e("FragmentChat", "Failed to get directions: " + response.message());
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Failed to get directions", Toast.LENGTH_SHORT).show()
                        );
                    }
                }

                @Override
                public void onFailure(Call<RouteResponse> call, Throwable t) {
                    Log.e("FragmentChat", "API call failed: " + t.getMessage());
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "API call failed", Toast.LENGTH_SHORT).show()
                    );
                }
            });
        });
    }


    private List<GeoPoint> decodePolyline(String polyline) {
        List<GeoPoint> geoPoints = new ArrayList<>();
        int len = polyline.length();
        int index = 0;
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = polyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = polyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            GeoPoint geoPoint = new GeoPoint(lat / 1E5, lng / 1E5);
            geoPoints.add(geoPoint);
        }

        return geoPoints;
    }


    private void drawRouteOnMap(List<GeoPoint> routePoints) {
        if (routePoints == null || routePoints.isEmpty()) {
            return;
        }

        Polyline routePolyline = new Polyline();
        routePolyline.setPoints(routePoints);
        routePolyline.setColor(Color.BLUE);
        routePolyline.setWidth(5.0f);

        mMap.getOverlays().add(routePolyline);

        BoundingBox boundingBox = BoundingBox.fromGeoPoints(routePoints).increaseByScale(1.2f); // Optional: Add some padding
        mMap.zoomToBoundingBox(boundingBox, true);

        mMap.invalidate();
    }

    private void showUserLocationOnMap() {
        // Check if currentUser is not null
        if (currentUser != null) {
            // Create GeoPoint for current user
            GeoPoint currentUserLocation = new GeoPoint(currentUserLatitude, currentUserLongitude);
            addMarker(currentUserLocation, "You");

            // Check if nearestUser is not null
            if (nearestUser != null) {
                // Create GeoPoint for nearest user
                GeoPoint nearestUserLocation = new GeoPoint(nearestUser.getLatitude(), nearestUser.getLongitude());
                addMarker(nearestUserLocation, nearestUser.getUserCategory());

                // Create a bounding box to include both users
                BoundingBox boundingBox = new BoundingBox(
                        Math.max(currentUserLatitude, nearestUser.getLatitude()),
                        Math.max(currentUserLongitude, nearestUser.getLongitude()),
                        Math.min(currentUserLatitude, nearestUser.getLatitude()),
                        Math.min(currentUserLongitude, nearestUser.getLongitude())
                );

                // Adjust map view to include both markers
                mMap.zoomToBoundingBox(boundingBox, true);
            }
        }
    }



//    private void startLocationUpdates() {
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(10000); // 10 seconds
//        locationRequest.setFastestInterval(5000); // 5 seconds
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//                for (Location location : locationResult.getLocations()) {
//                    userLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                   // updateMapWithLocation();
//                }
//            }
//        };
//
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            // Start location updates
//            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
//        }
//    }
//    private void updateMapWithLocation() {
//        if (mMap != null && userLocation != null) {
//            mMap.clear();
//            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
//        }
//    }


}
