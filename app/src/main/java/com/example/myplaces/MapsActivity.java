package com.example.myplaces;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myplaces.data.MyPlace;
import com.example.myplaces.data.MyPlacesData;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Map;

public class MapsActivity extends AppCompatActivity {
    MapView map = null;
    IMapController mapController = null;
    static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    MyLocationNewOverlay myLocationOverlay;

    public static final int SHOW_MAP = 0;
    public static final int CENTER_PLACE = 1;
    public static final int SELECT_COORDINATES = 2;

    private int state = 0;
    private boolean selCoordsEnabled = false;
    private GeoPoint placeLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Intent mapIntent = getIntent();
            Bundle mapBundle = mapIntent.getExtras();
            if (mapBundle != null) {
                state = mapBundle.getInt("state");
                if (state == CENTER_PLACE) {
                    String placeLat = mapBundle.getString("latitude");
                    String placeLong = mapBundle.getString("longitude");

                    placeLoc = new GeoPoint(Double.parseDouble(placeLat), Double.parseDouble(placeLong));
                }
            }
        } catch (Exception e) {
            Log.d("Error", "Error reading state");
        }

        setContentView(R.layout.activity_maps);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        if (state != SELECT_COORDINATES) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MapsActivity.this, EditMyPlaceActivity.class);
                    startActivityForResult(i, 1);
                }
            });
        } else {
            ViewGroup layout = (ViewGroup) fab.getParent();
            if (layout != null) {
                layout.removeView(fab);
            }
        }

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map = findViewById(R.id.map);

        map.setMultiTouchControls(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if no permissions, request them
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            setupMap();
            setMyLocationOverlay();
            setOnMapClickOverlay();
        }

        mapController = map.getController();
        if (mapController != null) {
            mapController.setZoom(15.0);
            GeoPoint startPoint = new GeoPoint(43.3209, 21.8958);
            mapController.setCenter(startPoint);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (state == SELECT_COORDINATES && !selCoordsEnabled) {
            menu.add(0,1,1,"Select coordinates");
            menu.add(0,2,2, "Cancel");
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.outline_location_searching_white_24dp));
            menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.outline_cancel_white_24dp));

            menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menu.getItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_map, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (state == SELECT_COORDINATES && !selCoordsEnabled) {
            if (id == 1) {
                selCoordsEnabled = true;
                Toast.makeText(this, "Now choose a place on map", Toast.LENGTH_SHORT).show();
            } else { // cancel
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        } else {
            if (id == R.id.about_item) {
                Intent i = new Intent(MapsActivity.this, AboutActivity.class);
                startActivity(i);
            } else if (id == R.id.my_places_list_item) {
                Intent i = new Intent(MapsActivity.this, MyPlacesListActivity.class);
                startActivity(i);
            } else if (id == R.id.new_place_item) {
                Intent i = new Intent(MapsActivity.this, EditMyPlaceActivity.class);
                startActivity(i);
            } else if (id == android.R.id.home) {
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setMyLocationOverlay() {
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        myLocationOverlay.enableMyLocation();
        map.getOverlays().add(myLocationOverlay);
        mapController = map.getController();

        if (mapController != null) {
            mapController.setZoom(15.0);
            myLocationOverlay.enableFollowLocation();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (state == SHOW_MAP)
                        showMyPlaces();
                    else if (state == SELECT_COORDINATES)
                        setOnMapClickOverlay();
                    else {
                        // moveCamera ?
                    }
                    showMyPlaces();
                }
                return;
            }
        }
    }

    private void setOnMapClickOverlay() {
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (state == SELECT_COORDINATES && selCoordsEnabled) {
                    String lat = Double.toString(p.getLatitude());
                    String lon = Double.toString(p.getLongitude());

                    Intent locationIntent = new Intent();
                    locationIntent.putExtra("latitude", lat);
                    locationIntent.putExtra("longitude", lon);
                    setResult(Activity.RESULT_OK, locationIntent);
                    finish();
                }
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        MapEventsOverlay OverlayEvents = new MapEventsOverlay(mReceive);
        map.getOverlays().add(OverlayEvents);
    }

    private void setCenterPlaceOnMap() {
        mapController = map.getController();
        if (mapController != null) {
            mapController.setZoom(15.0);
            mapController.animateTo(placeLoc);
        }
    }

    private void setupMap() {
        switch(state) {
            case SHOW_MAP: {
                setMyLocationOverlay();
                break;
            }
            case SELECT_COORDINATES: {
                mapController = map.getController();
                if (mapController != null) {
                    mapController.setZoom(15.0);
                    mapController.setCenter(new GeoPoint(43.3209, 21.8958));
                }
                setMyLocationOverlay();
                break;
            }
            case CENTER_PLACE:
                default:
                    setCenterPlaceOnMap();
                    break;
        }
        showMyPlaces();
    }

    private void showMyPlaces() {
        if (myLocationOverlay != null) {
            this.map.getOverlays().remove(myLocationOverlay);
        }
        final ArrayList<OverlayItem> items = new ArrayList<>();
        for (int i = 0; i < MyPlacesData.getInstance().getMyPlaces().size(); ++i) {
            MyPlace place = MyPlacesData.getInstance().getPlace(i);
            OverlayItem item = new OverlayItem(place.name,place.description, new GeoPoint(Double.parseDouble(place.latitude), Double.parseDouble(place.longitude)));
            item.setMarker(this.getResources().getDrawable(R.drawable.myplace32));
            items.add(item);
        }

        ItemizedIconOverlay itemizedIconOverlay = new ItemizedIconOverlay<>(items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                Intent i = new Intent(MapsActivity.this, ViewPlaceActivity.class);
                i.putExtra("index", index);
                startActivity(i);
                return true;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                Intent i = new Intent(MapsActivity.this, EditMyPlaceActivity.class);
                i.putExtra("index", index);
                startActivityForResult(i, 1);
                return true;
            }
        }, getApplicationContext());
        this.map.getOverlays().add(itemizedIconOverlay);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            showMyPlaces();
        }
    }
}
