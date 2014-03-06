package com.ngusta.cupassist.parser;

import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.Team;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TournamentParser {

    public List<Team> parseTeams(String source, Set<Player> allPlayers) {
        ArrayList<Team> teams = new ArrayList<Team>();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document htmlDocument = builder.parse(new InputSource(new StringReader(source)));

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("//tr/td");
            NodeList nodeList = (NodeList) expr.evaluate(htmlDocument, XPathConstants.NODESET);

            Map<String, Player> allPlayersMap = new HashMap<>();
            for (Player player : allPlayers) {
                allPlayersMap.put(player.getNameAndClub(), player);
            }

            int numberOfRows = nodeList.getLength() / 8;
            for (int row = 1; row < numberOfRows; row++) {
                int i = 8 * row - 1;
                String[] names = nodeList.item(i).getTextContent().split("[,/]");
                if (names.length == 4) {
                    String club = nodeList.item(i + 1).getTextContent().trim();
                    String clazz = nodeList.item(i + 2).getTextContent().trim();
                    Date registrationDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(nodeList.item(i + 3).getTextContent().trim());
                    String playerAClub = club;
                    String playerBClub = club;
                    if (club.contains("/")) {
                        String[] clubs = club.split("/");
                        playerAClub = clubs[0].trim();
                        playerBClub = clubs[1].trim();
                    }
                    String playerAFirstName = names[1].trim();
                    String playerALastName = names[0].trim();
                    String playerBFirstName = names[3].trim();
                    String playerBLastName = names[2].trim();
                    Player playerA = findPlayer(allPlayersMap, playerAFirstName, playerALastName, playerAClub);
                    Player playerB = findPlayer(allPlayersMap, playerBFirstName, playerBLastName, playerBClub);
                    teams.add(new Team(playerA, playerB, registrationDate, clazz));
                }
            }
        } catch (ParserConfigurationException | SAXException | XPathExpressionException | IOException | ParseException e) {
            e.printStackTrace();
        }
        return teams;
    }

    private Player findPlayer(Map<String, Player> allPlayers, String playerFirstName, String playerLastName, String playerClub) {
        Player newPlayer = new Player(playerFirstName, playerLastName, playerClub);
        if (allPlayers.containsKey(newPlayer.getNameAndClub())) {
            return allPlayers.get(newPlayer.getNameAndClub());
        }
        return newPlayer;
    }
}
