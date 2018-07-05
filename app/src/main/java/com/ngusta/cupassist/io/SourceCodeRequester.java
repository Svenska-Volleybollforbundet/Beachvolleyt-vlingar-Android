package com.ngusta.cupassist.io;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import android.os.StrictMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SourceCodeRequester {

    private static Map<String, String> cookies = new HashMap<>();

    public String getSourceCode(String url) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection.Response response = Jsoup.connect(url).cookies(cookies).method(Connection.Method.GET).timeout(20000).execute();
        cookies.putAll(response.cookies());
        return response.body();
    }
}
