package com.trinity.trinity;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class Util {

    public static File save(byte[] data, String folder, String fileName, Context c) {
        File Folder = new File(c.getFilesDir() + File.separator + folder);
        Folder.mkdir();
        File file = new File(c.getFilesDir() + File.separator
                + folder + File.separator + fileName);

        try {
            if (file.exists())
                file.delete();
            file.createNewFile();
            OutputStream osw = new FileOutputStream(file, true);
            osw.write(data);
        } catch (IOException e) { e.printStackTrace(); }
        return file;
    }

    public static File tempsave(byte[] data, String folder, String fileName, Context c) {
        File Folder = new File(c.getCacheDir() + File.separator + folder);
        Folder.mkdir();
        File file = new File(c.getCacheDir() + File.separator
                + folder + File.separator + fileName);
        file.deleteOnExit();
        Folder.deleteOnExit();

        try {
            if (file.exists())
                file.delete();
            file.createNewFile();
            OutputStream osw = new FileOutputStream(file, true);
            osw.write(data);
        } catch (IOException e) { e.printStackTrace(); }
        return file;
    }

    /*public static Notification getNotificationById(int id) {
        NotificationManager nm = (NotificationManager) MainActivity.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        StatusBarNotification[] barNotifications = nm.;
        for(StatusBarNotification notification: barNotifications) {
            if (notification.getId() == id) {
                return notification.getNotification();
            }
        }
        return null;
    }*/


    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    public static double getSimilarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0)
            return 1.0; /* both strings are zero length */
        /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    private static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static String removeUnnecessaryParts (String string) {
        string = string.replaceAll("\\s*\\([^\\)]*\\)\\s*", " ");
        return string;
    }

    public static String removeFirstWord (String string) {
        String[] parts = string.split(" ", 2);
        if (parts.length <= 1)
            return string;
        return parts[1];
    }

    public static class JSONReader {
        private static String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }
    }

}
