package com.ngusta.cupassist.parser;

import com.ngusta.cupassist.domain.Player;

import org.jsoup.nodes.Element;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class PlayerListParser {

    public Set<Player> parsePlayerList(InputStreamReader source) {
        JsonReader jsonReader = new JsonReader(source);
        Set<Player> players = new HashSet<>();
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                parsePlayer(jsonReader, players);
            }
            jsonReader.endArray();
            jsonReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return players;
    }

    private void parsePlayer(JsonReader jsonReader, Set<Player> players) throws IOException {
        int rank = 0, points = 0;
        String playerId = null, firstName = null, lastName = null, club = null, clazz = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String key = jsonReader.nextName();
            switch (key) {
                case "PlayerId":
                    playerId = "" + jsonReader.nextInt();
                    break;
                case "Name":
                    String[] name = jsonReader.nextString().split(",");
                    if (name.length >= 2) {
                        firstName = name[1].trim();
                        if (firstName.charAt(firstName.length() - 1) == '*') {
                            firstName = firstName.substring(0, firstName.length() - 1);
                        }
                    }
                    lastName = name[0].trim();
                    break;
                case "Rank":
                    rank = jsonReader.nextInt();
                    break;
                case "Points":
                    points = jsonReader.nextInt();
                    break;
                case "ClubName":
                    club = jsonReader.nextString();
                    break;
                case "ClassName":
                    clazz = jsonReader.nextString();
                    break;
                default:
                    jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        Player player = new Player(rank, firstName, lastName, club, points, points, playerId, clazz);
        players.add(player);
    }
}
