package com.ngusta.cupassist.service;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.ngusta.cupassist.domain.Court;
import com.ngusta.cupassist.domain.OnMarkerChangeListener;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CourtService {

    private DatabaseReference courtsRef;

    private boolean courtsInitiated = false;

    public CourtService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        courtsRef = database.getReference("courts");
    }

    public void loadCourts(final OnMarkerChangeListener courtActivity) {
        courtsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Court> courts = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Court court = snapshot.getValue(Court.class);
                    courts.put(snapshot.getKey(), court);
                }
                courtActivity.addMarkers(courts);
                courtsInitiated = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        courtsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String prevChildKey) {
                if (!courtsInitiated) {
                    return;
                }
                String key = dataSnapshot.getKey();
                Court court = dataSnapshot.getValue(Court.class);
                courtActivity.addMarker(key, court);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String prevChildKey) {
                if (!courtsInitiated) {
                    return;
                }
                Court changedCourt = dataSnapshot.getValue(Court.class);
                if (changedCourt == null) {
                    return;
                }
                courtActivity.updateMarker(dataSnapshot.getKey(), changedCourt);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (!courtsInitiated) {
                    return;
                }
                courtActivity.removeMarker(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (!courtsInitiated) {
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!courtsInitiated) {
                    return;
                }
            }
        });
    }

    public void addCourt(double latitude, double longitude) {
        Court newCourt = new Court(latitude, longitude, "Ny bana");
        courtsRef.push().setValue(newCourt);
    }

    public void updateCourt(String key, Court updatedCourt) {
        courtsRef.child(key).setValue(updatedCourt);
    }

    public void removeCourt(String key) {
        courtsRef.child(key).removeValue();
    }
}
