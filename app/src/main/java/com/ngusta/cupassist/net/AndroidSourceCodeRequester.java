package com.ngusta.cupassist.net;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AndroidSourceCodeRequester implements SourceCodeRequester {

    private static CookieStore cookieStore;

    @Override
    public String getSourceCode(String url) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        if (cookieStore != null) {
            httpClient.setCookieStore(cookieStore);
        }
        HttpResponse response = httpClient.execute(new HttpGet(url));
        cookieStore = httpClient.getCookieStore();
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            out.close();
            return out.toString();
        } else {
            response.getEntity().getContent().close();
            throw new IOException(statusLine.getReasonPhrase());
        }
    }
}
