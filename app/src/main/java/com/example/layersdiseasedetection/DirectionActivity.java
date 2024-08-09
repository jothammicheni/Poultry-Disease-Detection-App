//package com.example.layersdiseasedetection;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import org.osmdroid.api.IMapController;
//import org.osmdroid.config.Configuration;
//import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
//import org.osmdroid.util.BoundingBox;
//import org.osmdroid.util.GeoPoint;
//import org.osmdroid.views.MapView;
//import org.osmdroid.views.overlay.Marker;
//import org.osmdroid.views.overlay.Polyline;
//
//import com.example.layersdiseasedetection.data.UserDetails;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
//
//public class DirectionActivity extends AppCompatActivity {
//
//    private MapView mMap;
//    private Button buttonGetDirections;
//    private GeoPoint officerGeoPoint;
//    private GeoPoint farmerGeoPoint;
//    private DatabaseReference databaseReference;
//    private ExecutorService executorService;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));
//
//        setContentView(R.layout.activity_direction);
//
//        mMap = findViewById(R.id.map);
//        mMap.setTileSource(TileSourceFactory.MAPNIK);
//        mMap.setBuiltInZoomControls(true);
//        mMap.setMultiTouchControls(true);
//
//        IMapController mapController = mMap.getController();
//        mapController.setZoom(10.0);
//
//        buttonGetDirections = findViewById(R.id.button_get_directions);
//        databaseReference = FirebaseDatabase.getInstance().getReference("users");
//        executorService = Executors.newSingleThreadExecutor();
//        mAuth = FirebaseAuth.getInstance();
//
//        buttonGetDirections.setOnClickListener(v -> fetchLocationsAndGetDirections());
//    }
//
//    private void fetchLocationsAndGetDirections() {
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser == null) {
//            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String currentUserId = currentUser.getUid();
//        String farmerUserId = getIntent().getStringExtra("userId");
//
//        if (currentUserId == null || farmerUserId == null) {
//            Toast.makeText(this, "User ID information is missing", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                UserDetails officerUser = null;
//                UserModel farmerUser = null;
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String userId = snapshot.getKey();
//                    UserModel userModel = snapshot.getValue(UserModel.class);
//
//                    if (userModel == null || userModel.getUserType() == null || userModel.getLocation() == null) {
//                        continue;  // Skip this user if any required data is missing
//                    }
//
//                    if (userId.equals(currentUserId)) {
//                        officerUser = userModel;
//                    } else if (userId.equals(farmerUserId)) {
//                        farmerUser = userModel;
//                    }
//
//                    if (officerUser != null && farmerUser != null) {
//                        break;  // Exit the loop if we've found both users
//                    }
//                }
//
//                if (officerUser != null && farmerUser != null) {
//                    officerGeoPoint = parseGeoPoint(officerUser.getLocation());
//                    farmerGeoPoint = parseGeoPoint(farmerUser.getLocation());
//
//                    if (officerGeoPoint != null) {
//                        addMarker(officerGeoPoint, "Officer");
//                    }
//                    if (farmerGeoPoint != null) {
//                        addMarker(farmerGeoPoint, "Farmer");
//                    }
//
//                    if (officerGeoPoint != null && farmerGeoPoint != null) {
//                        requestDirections(officerGeoPoint, farmerGeoPoint);
//
//                        BoundingBox boundingBox = new BoundingBox(
//                                Math.max(officerGeoPoint.getLatitude(), farmerGeoPoint.getLatitude()),
//                                Math.max(officerGeoPoint.getLongitude(), farmerGeoPoint.getLongitude()),
//                                Math.min(officerGeoPoint.getLatitude(), farmerGeoPoint.getLatitude()),
//                                Math.min(officerGeoPoint.getLongitude(), farmerGeoPoint.getLongitude())
//                        );
//
//                        mMap.zoomToBoundingBox(boundingBox, true);
//                        mMap.getController().setZoom(15.0);
//                    } else {
//                        Log.e("DirectionActivity", "Could not parse officer or farmer locations.");
//                        Toast.makeText(DirectionActivity.this, "Could not parse locations", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Log.e("DirectionActivity", "Could not retrieve officer or farmer data.");
//                    Toast.makeText(DirectionActivity.this, "Could not retrieve user data", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("Firebase Error", "Failed to read from database: " + databaseError.getMessage());
//                Toast.makeText(DirectionActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private GeoPoint parseGeoPoint(String location) {
//        try {
//            if (location == null || location.trim().isEmpty()) {
//                throw new IllegalArgumentException("Location string is empty");
//            }
//
//            String[] latLng = location.trim().split(",");
//            if (latLng.length != 2) {
//                throw new IllegalArgumentException("Location string is not in the correct format");
//            }
//
//            double latitude = Double.parseDouble(latLng[0].trim());
//            double longitude = Double.parseDouble(latLng[1].trim());
//            return new GeoPoint(latitude, longitude);
//        } catch (Exception e) {
//            Log.e("GeoPoint Error", "Failed to parse location: " + location, e);
//            return null;
//        }
//    }
//
//    private void addMarker(GeoPoint point, String title) {
//        if (point != null) {
//            Marker marker = new Marker(mMap);
//            marker.setPosition(point);
//            marker.setTitle(title);
//            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//            mMap.getOverlays().add(marker);
//            mMap.invalidate();
//        }
//    }
//
//    private void requestDirections(GeoPoint start, GeoPoint end) {
//        executorService.execute(() -> {
//            String startCoords = start.getLongitude() + "," + start.getLatitude();
//            String endCoords = end.getLongitude() + "," + end.getLatitude();
//
//            Log.d("Coordinates", "Start: " + startCoords + ", End: " + endCoords);
//
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//                    .build();
//
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl("https://api.openrouteservice.org/")
//                    .client(client)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//            OpenRouteService service = retrofit.create(OpenRouteService.class);
//
//            String apiKey = "5b3ce3597851110001cf6248e7cb7ef29b5448b8aab144c0b829ef92"; // Replace with your actual API key
//
//            Call<RouteResponse> call = service.getDirections(apiKey, startCoords, endCoords);
//
//            call.enqueue(new Callback<RouteResponse>() {
//                @Override
//                public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        if (response.body().routes != null && response.body().routes.length > 0) {
//                            drawRoute(response.body().routes[0].geometry.coordinates);
//                        } else {
//                            Log.e("API Error", "No routes found in response");
//                            runOnUiThread(() -> Toast.makeText(DirectionActivity.this, "No routes found", Toast.LENGTH_SHORT).show());
//                        }
//                    } else {
//                        handleApiError(response);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<RouteResponse> call, Throwable t) {
//                    Log.e("API Error", "Failed to fetch directions: " + t.getMessage());
//                    runOnUiThread(() -> Toast.makeText(DirectionActivity.this, "Failed to fetch directions", Toast.LENGTH_SHORT).show());
//                }
//            });
//        });
//    }
//
//    private void handleApiError(Response<RouteResponse> response) {
//        Log.e("API Response", "Error: Code = " + response.code());
//        if (response.errorBody() != null) {
//            try {
//                String errorBody = response.errorBody().string();
//                Log.e("API Error", "Error body: " + errorBody);
//            } catch (IOException e) {
//                Log.e("API Error", "Error parsing error body", e);
//            }
//        }
//        runOnUiThread(() -> Toast.makeText(DirectionActivity.this, "Error fetching directions: " + response.message(), Toast.LENGTH_SHORT).show());
//    }
//
//    private void drawRoute(List<List<Double>> coordinates) {
//        if (coordinates == null || coordinates.isEmpty()) {
//            Log.e("Draw Route", "No coordinates available");
//            runOnUiThread(() -> Toast.makeText(this, "No valid route coordinates", Toast.LENGTH_SHORT).show());
//            return;
//        }
//
//        Polyline line = new Polyline();
//        List<GeoPoint> points = new ArrayList<>();
//
//        for (List<Double> coordinate : coordinates) {
//            if (coordinate.size() == 2) {
//                points.add(new GeoPoint(coordinate.get(1), coordinate.get(0))); // Latitude, Longitude
//            } else {
//                Log.e("Coordinates Error", "Invalid coordinate size: " + coordinate.toString());
//            }
//        }
//
//        if (!points.isEmpty()) {
//            line.setPoints(points);
//            line.setColor(Color.BLUE);
//            line.setWidth(8f);
//            runOnUiThread(() -> {
//                mMap.getOverlays().add(line);
//                mMap.invalidate();
//            });
//        } else {
//            Log.e("Draw Route", "Failed to generate points from coordinates");
//            runOnUiThread(() -> Toast.makeText(this, "Failed to generate route", Toast.LENGTH_SHORT).show());
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mMap.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mMap.onPause();
//    }
//}
