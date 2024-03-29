package com.ngusta.cupassist;

import com.ngusta.cupassist.adapters.PlayerAdapter;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.service.PlayerService;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class PlayerListFragment extends Fragment {

    private RecyclerView recyclerView;

    private PlayerAdapter adapter;

    private PlayerService playerService;

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
        adapter = new PlayerAdapter(players, clazz, playerService);
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public PlayerAdapter getAdapter() {
        return adapter;
    }
}
