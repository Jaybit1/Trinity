package com.trinity.trinity.client.soundsystem;

public class AnglicismAppender {

    private final static String[][] phoneticTranscriptions = new String[][] {
            {"jarvis", "dscharvis"},
            {"trinity", "trinnitie"},
            {"homeserver", "hohm-server"},
            {"online", "onlain"},
            {"hi", "hai"},
            {"google", "guhgel"}
    };

    public static String getPhoneticTranscription(String string) {
        string = string.toLowerCase();
        for (String[] pt: phoneticTranscriptions) {
            if (string.contains(" " + pt[0])) {
                string = string.replace(" " + pt[0], " " + pt[1]);
            }
            if (string.startsWith(pt[0])) {
                String sub = string.substring(pt[0].length());
                string = pt[1] + sub;
            }
        }
        return string;
    }

}
