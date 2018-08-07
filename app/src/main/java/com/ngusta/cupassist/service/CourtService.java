package com.ngusta.cupassist.service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.ngusta.cupassist.activity.CourtsActivity;
import com.ngusta.cupassist.domain.Court;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CourtService {

    private DatabaseReference courtsRef;

    private List<Court> courts;

    public CourtService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        courtsRef = database.getReference("courts");
        this.courts = new ArrayList<>();
    }

    public List<Court> getCourts() {
        return courts;
    }

    public void loadCourts(final CourtsActivity courtsActivity) {
        courtsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CourtService.this.courts.add(snapshot.getValue(Court.class));
                }
                courtsActivity.addMarkersForCourts(courts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void writeCourtsToDb() {
        courts = new ArrayList<>();
        courts.add(new Court(59.288181, 18.061400, "Enskede", "Endast medlemmar får spela", null));
        courts.add(
                new Court(59.323223, 17.914447, "Kärsön", "Sex banor. Hemmaarena för Bromma KFUK-KFUM. Se hemsidan för info om bokning och priser.",
                        "http://idrottonline.se/BrommaKFUK-KFUM-Volleyboll/Beachvolleyboll/Beachvolleyboll"));

        courtsRef.setValue(courts);
    }
}
