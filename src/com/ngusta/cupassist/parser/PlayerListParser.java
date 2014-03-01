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
import java.util.ArrayList;

public class PlayerListParser {
    public ArrayList<Player> parsePlayerList(String source) {
        try {
            source = source
                    .replace("&", "&amp;")
                    .replace("<br>", "<br/>")
                    .replace("class='curpoint'", "")
                    .replace("colspan=5", "colspan=\"5\"")
                    .replace("</body>", "</p></body>");
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document htmlDocument = builder.parse(new InputSource(new StringReader(source)));
            return buildPlayerList(htmlDocument);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println(source);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return new ArrayList<Player>();
    }

    private ArrayList<Player> buildPlayerList(Document document) throws XPathExpressionException {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("//tr/td");
        NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        ArrayList<Player> players = new ArrayList<Player>();
        int startIndex = 6;
        int numberOfRows = (nodeList.getLength() - startIndex - 1) / 5;
        for (int row = 0; row < numberOfRows; row++) {
            int i = startIndex + 5 * row;
            if (nodeList.item(i).getTextContent().equals("Spelare utan licens")) {
                startIndex++;
                row--;
                i = startIndex + 5 * row;
                continue;
            }
            players.add(createPlayer(nodeList, i));
        }
        return players;
    }

    private Player createPlayer(NodeList nodeList, int i) {
        String rankString = nodeList.item(i).getTextContent();
        Integer rank = !rankString.equals("&nbsp;") ? Integer.parseInt(rankString) : null;
        String[] name = nodeList.item(i + 1).getTextContent().split(",");
        String firstName = name[1].trim();
        String lastName = name[0].trim();
        String club = nodeList.item(i + 2).getTextContent().trim();
        int rankPoints = Integer.parseInt(nodeList.item(i + 3).getTextContent());
        int entryPoints = Integer.parseInt(nodeList.item(i + 4).getTextContent());
        return new Player(rank, firstName, lastName, club, rankPoints, entryPoints);
    }
}
