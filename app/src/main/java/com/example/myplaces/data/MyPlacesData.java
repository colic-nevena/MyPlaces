package com.example.myplaces.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.ListUpdateCallback;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MyPlacesData {
    private ArrayList<MyPlace> myPlaces;
    private HashMap<String, Integer> myPlacesKeyIndexMapping;
    private DatabaseReference database;
    private static final String FIREBASE_CHILD = "my-places";

    private MyPlacesData() {
        myPlaces = new ArrayList<>();
        myPlacesKeyIndexMapping = new HashMap<>();
        database = FirebaseDatabase.getInstance().getReference();
        database.child(FIREBASE_CHILD).addChildEventListener(childEventListener);
        database.child(FIREBASE_CHILD).addListenerForSingleValueEvent(parentEventListener);

//        myPlaces.add(new MyPlace("Trg Kralja Milana", "Spomenik borcima za oslobodjenje Nisa", "43.3209", "21.8958"));
    }

    ValueEventListener parentEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (updateListener != null) {
                updateListener.onListUpdated();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String myPlaceKey = dataSnapshot.getKey();
            if (!myPlacesKeyIndexMapping.containsKey(myPlaceKey)) {
                MyPlace place = dataSnapshot.getValue(MyPlace.class);
                place.key = myPlaceKey;
                myPlaces.add(place);
                myPlacesKeyIndexMapping.put(myPlaceKey, myPlaces.size() - 1);

                if (updateListener != null) {
                    updateListener.onListUpdated();
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String myPlaceKey = dataSnapshot.getKey();
            MyPlace place = dataSnapshot.getValue(MyPlace.class);
            place.key = myPlaceKey;

            if (myPlacesKeyIndexMapping.containsKey(myPlaceKey)) {
                int index = myPlacesKeyIndexMapping.get(myPlaceKey);
                myPlaces.set(index, place);
            } else {
                myPlaces.add(place);
                myPlacesKeyIndexMapping.put(myPlaceKey, myPlaces.size()-1);
            }

            if (updateListener != null) {
                updateListener.onListUpdated();
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String myPlaceKey = dataSnapshot.getKey();
            if (myPlacesKeyIndexMapping.containsKey(myPlaceKey)) {
                int index = myPlacesKeyIndexMapping.get(myPlaceKey);
                myPlaces.remove(index);
                recreateKeyIndexMapping();

                if (updateListener != null) {
                    updateListener.onListUpdated();
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ListUpdatedEventListener updateListener;

    public void setEventListener(ListUpdatedEventListener listener) {
        updateListener = listener;
    }

    public interface ListUpdatedEventListener {
        void onListUpdated();
    }

    private static class SingletonHolder {
        public static final MyPlacesData instance = new MyPlacesData();
    }

    public static MyPlacesData getInstance() {
        return SingletonHolder.instance;
    }

    public ArrayList<MyPlace> getMyPlaces() {
        return myPlaces;
    }

    public void addNewPlace(MyPlace place) {
//        this.myPlaces.add(place);
        String key = database.push().getKey();
        myPlaces.add(place);
        myPlacesKeyIndexMapping.put(key, myPlaces.size() - 1);
        database.child(FIREBASE_CHILD).child(key).setValue(place);
        place.key = key;

        recreateKeyIndexMapping();
    }

    public void removePlace(int index) {
//        this.myPlaces.remove(index);
        database.child(FIREBASE_CHILD).child(myPlaces.get(index).key).removeValue();
        myPlaces.remove(index);
        recreateKeyIndexMapping();
    }

    public void updatePlace(int index, String nme, String desc, String lng, String lat) {
        MyPlace myPlace = myPlaces.get(index);
        myPlace.name = nme;
        myPlace.description = desc;
        myPlace.latitude = lat;
        myPlace.longitude = lng;

        database.child(FIREBASE_CHILD).child(myPlace.key).setValue(myPlace);
    }

    private void recreateKeyIndexMapping() {
        myPlacesKeyIndexMapping.clear();
        for (int i = 0; i < myPlaces.size(); ++i) {
            myPlacesKeyIndexMapping.put(myPlaces.get(i).key, i);
        }
    }

    public MyPlace getPlace(int index) {
        return myPlaces.get(index);
    }
}
