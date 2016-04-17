package com.ngusta.cupassist;

import com.ngusta.beachvolley.domain.Clazz;
import com.ngusta.beachvolley.domain.Player;
import com.ngusta.cupassist.adapters.PlayerAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

public class PlayerListFragment extends Fragment {

    private RecyclerView recyclerView;

    private PlayerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.player_list, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        if (adapter != null) {
            recyclerView.setAdapter(adapter);
        }
        return recyclerView;
    }

    public void addPlayers(List<Player> players, Clazz clazz) {
        adapter = new PlayerAdapter(players, clazz);
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }
    }
}
