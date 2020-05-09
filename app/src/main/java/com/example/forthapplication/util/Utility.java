package com.example.forthapplication.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    public static String getCurrentTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM\n  yyyy");
            String currentDate = dateFormat.format(new Date());
            System.out.println(currentDate);
            return currentDate;
        }
        catch (Exception e) {
            return null;
        }
    }
}
