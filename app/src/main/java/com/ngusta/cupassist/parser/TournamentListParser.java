package com.ngusta.cupassist.parser;

import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.Tournament;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
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
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TournamentListParser {

    private String REGEXP_PATTERN_FOR_REGISTRATION_URL = "pamelding/redirect.php\\?tknavn=(.*?)\", \"_blank\"";

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
        return new ArrayList<>();
    }

    private ArrayList<Tournament> buildTournamentList(Document document) throws XPathExpressionException, ParseException {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("//tr[@class]/td");
        NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        ArrayList<Tournament> tournaments = new ArrayList<Tournament>();
        int numberOfRows = nodeList.getLength() / 7;
        for (int row = 0; row < numberOfRows; row++) {
            tournaments.add(createTournament(nodeList, 7 * row));
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

    public String parseRegistrationUrl(String source) {
        Pattern pattern = Pattern.compile(REGEXP_PATTERN_FOR_REGISTRATION_URL);
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public Map<Clazz, Integer> parseMaxNumberOfTeams(String source) {
        org.jsoup.nodes.Document doc = Jsoup.parse(source);
        Elements elements = doc.select("tr:has(td:contains(Klassdetaljer)) > td:nth-child(2) tr:gt(0) td:lt(2)");

        Map<Clazz, Integer> maxNumberOfTeamsMap = new HashMap<>(2);
        Iterator<org.jsoup.nodes.Element> iterator = elements.iterator();

        while (iterator.hasNext()) {
            org.jsoup.nodes.Element clazzElem = iterator.next();
            org.jsoup.nodes.Element maxNumberOfTeamsElem = iterator.hasNext() ? iterator.next() : null;

            if (maxNumberOfTeamsElem != null) {
                Clazz clazz = Clazz.parse(clazzElem.text().trim());
                int maxNumberOfTeams = Integer.parseInt(maxNumberOfTeamsElem.text().trim());
                maxNumberOfTeamsMap.put(clazz, maxNumberOfTeams);
            }
        }

        return maxNumberOfTeamsMap;
    }
}