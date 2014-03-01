package com.ngusta.cupassist.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;

public class DesktopSourceCodeRequester implements SourceCodeRequester {

    static {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    @Override
    public String getSourceCode(String url) throws IOException {
        URL uri = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
        InputStream is = urlConnection.getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(is, "iso-8859-9"));

        int byteRead;
        StringBuilder builder = new StringBuilder();
        while ((byteRead = buffer.read()) != -1) {
            builder.append((char) byteRead);
        }
        buffer.close();
        urlConnection.disconnect();
        return builder.toString();
    }
}
