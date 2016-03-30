/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.ngusta.beachvolley.backend;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ngusta.beachvolley.backend.io.PlayerListCache;
import com.ngusta.beachvolley.backend.io.TournamentListCache;
import com.ngusta.beachvolley.backend.parser.TournamentParser;
import com.ngusta.beachvolley.domain.Player;
import com.ngusta.beachvolley.domain.Tournament;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.*;

public class MyServlet extends HttpServlet {

    static Logger Log = Logger.getLogger("com.ngusta.beachvolley.backend.MyServlet");

    private Firebase firebase;

    private Map<String, Player> players;

    private List<Tournament> tournaments;

    private TournamentListCache tournamentListCache;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Log.info("hej");
        firebase = new Firebase("https://incandescent-heat-8146.firebaseio.com/");
        firebase.child("Latest ran").setValue(new Date().toString());

        updatePlayers();
        tournamentListCache = new TournamentListCache();
        tournaments = tournamentListCache.getTournaments();
        tournamentListCache.getTournamentDetails(tournaments.get(29), players);
        firebase.child("tournaments").setValue(tournaments);
        /*firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.info(dataSnapshot.toString());
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });*/
    }

    private void updatePlayers() {
        PlayerListCache playerListCache = new PlayerListCache();
        players = playerListCache.getPlayers();
        firebase.child("players").setValue(players);
    }
}
