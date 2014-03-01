package com.ngusta.cupassist.parser;

import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.Team;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.net.DesktopSourceCodeRequester;
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

public class TournamentParser {

    public ArrayList<Team> parseTeams(String source) {
        ArrayList<Team> teams = new ArrayList<Team>();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document htmlDocument = builder.parse(new InputSource(new StringReader(source)));

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("//tr/td");
            NodeList nodeList = (NodeList) expr.evaluate(htmlDocument, XPathConstants.NODESET);

            int numberOfRows = nodeList.getLength() / 8;
            for (int row = 1; row < numberOfRows; row++) {
                int i = 8*row - 1;
                String[] names = nodeList.item(i).getTextContent().split("[,/]");
                if (names.length == 4) {
                    teams.add(new Team(new Player(names[1].trim(), names[0].trim()), new Player(names[3].trim(), names[2].trim())));//TODO Look up player? Or replace later?
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return teams;
    }
}
