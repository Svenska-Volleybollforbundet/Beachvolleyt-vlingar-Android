package com.ngusta.beachvolley.backend.io;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SourceCodeRequester {

    private static Map<String, String> cookies = new HashMap<>();

    public String getSourceCode(String url) throws IOException {
        Connection.Response response = Jsoup.connect(url).cookies(cookies).method(Connection.Method.GET).execute();
        cookies.putAll(response.cookies());
        return response.body();
    }
}
