package com.ngusta.cupassist.parser;

import com.ngusta.cupassist.activity.MyApplication;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.net.AndroidSourceCodeRequester;
import com.ngusta.cupassist.net.DesktopSourceCodeRequester;
import com.ngusta.cupassist.net.SourceCodeRequester;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TournamentListParser {

    private String REGEXP_PATTERN_FOR_REDIRECT_URL = "pamelding/redirect.php\\?tknavn=(.*?)\", \"_blank\"";

    public ArrayList<Tournament> parseTournamentList(String source) {
        try {
            source = source
                    .replace("KFUM Gymnastik & IA Karskrona", "KFUM Gymnastik &amp; IA Karskrona")
                    .replace("<br>", "<br/>");
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document htmlDocument = builder.parse(new InputSource(new StringReader(source)));
            return buildTournamentList(htmlDocument);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println(source);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<Tournament>();
    }

    private ArrayList<Tournament> buildTournamentList(Document document) throws XPathExpressionException, ParseException {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("//tr[@class]/td");
        NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        ArrayList<Tournament> tournaments = new ArrayList<Tournament>();
        int numberOfRows = nodeList.getLength() / 7;
        for (int row = 0; row < numberOfRows; row++) {
            tournaments.add(createTournament(nodeList, 7*row));
        }
        return tournaments;
    }

    private Tournament createTournament(NodeList nodeList, int startIndex) throws ParseException {
        Date startDate = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).parse(nodeList.item(startIndex).getTextContent());
        String period = nodeList.item(startIndex + 2).getTextContent();
        String club = nodeList.item(startIndex + 3).getTextContent();
        String name = nodeList.item(startIndex + 4).getTextContent();
        String url = getUrl(nodeList, startIndex + 4);
        String level = nodeList.item(startIndex + 5).getTextContent();
        String classes = nodeList.item(startIndex + 6).getTextContent();
        return new Tournament(startDate, period, club, name, url, level, classes);
    }

    private String getUrl(NodeList nodeList, int urlIndex) {
        Node anchor = nodeList.item(urlIndex).getChildNodes().item(0);
        return anchor instanceof Element ? ((Element) anchor).getAttribute("href") : "";
    }

    public String parseRedirectUrl(String source) {
        Pattern pattern = Pattern.compile(REGEXP_PATTERN_FOR_REDIRECT_URL);
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}