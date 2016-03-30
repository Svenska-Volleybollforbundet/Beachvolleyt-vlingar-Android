/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.ngusta.beachvolley.backend;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.*;

public class MyServlet extends HttpServlet {

    static Logger Log = Logger.getLogger("com.ngusta.beachvolley.backend.MyServlet");

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Log.info("Got cron message, constructing email.");

        //Create a new Firebase instance and subscribe on child events.
        Firebase firebase = new Firebase("https://incandescent-heat-8146.firebaseio.com/");
        firebase.child("" + System.currentTimeMillis()).setValue(new Date().toString());
        /*firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.info(dataSnapshot.toString());
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });*/
    }
}