package com.ngusta.cupassist.service;

import com.ngusta.cupassist.domain.Court;

import java.util.ArrayList;
import java.util.List;

public class CourtService {

    public List<Court> courts;

    public CourtService() {
        this.courts = new ArrayList<>();
        courts.add(new Court(59.288181, 18.061400, "Enskede", "Endast medlemmar får spela", null));
        courts.add(new Court(59.323223, 17.914447, "Kärsön", "Sex banor. Hemmaarena för Bromma KFUK-KFUM",
                "http://idrottonline.se/BrommaKFUK-KFUM-Volleyboll/Beachvolleyboll/Beachvolleyboll"));
    }

    public List<Court> getCourts() {
        return courts;
    }
}
