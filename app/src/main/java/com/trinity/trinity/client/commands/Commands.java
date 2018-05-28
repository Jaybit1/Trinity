package com.trinity.trinity.client.commands;

public class Commands {
    public static class server {
        public static final String[] CONNECT = {"Verbinde", "Verbinde zum Homeserver", "Verbindung herstellen"};
        public static final String[] DISCONNECT = {"Trenne", "Trenne die Verbindung", "Verbindung trennen"};
    }
    public static class media {
        public static final String[] VOLUME = {"Lautstärke"};
        public static class music {
            public static final String[] PLAY = {"Spiele", "Play", "Musik", "Spiele Musik"};
            public static final String[] PAUSE = {"Pause", "Halt", "Anhalten"};
        }
    }
    public static class eastereggs {
        public static final String[] HYDRA = {"Heil Hydra", "Hail Hydra"};
        public static final String[] THANKS = {"Danke", "Vielen Dank", "Danke Schön", "Ich danke Dir"};
    }
    public static class information {
        public static final String[] YES = {"Ja", "Genau", "Exakt"};
        public static final String[] NO = {"Nein", "Stopp", "Halt"};
        public static final String[] VERB = {"Ist", "Sind", "Bedeutet", "Bedeuten"};
        public static final String[] ADJECTIVE = {"Ein", "Eine", "Der", "Die", "Das"};
    }
    public static class generic {
        public static final String[] NEXT = {"Weiter", "Nächste", "Nächster", "Nächstens"};
        public static final String[] STOP = {"Stop", "Stopp"};
        public static final String[] REPEAT = {"Wiederhole", "Nochmal", "Was", "Wie bitte", "Ich hab dich nicht verstanden", "Ich habe dich nicht verstanden"};
        public static final String[] FETCH_INFORMATION = {"Was", "Wie", "Warum", "Weshalb"};
        public static final String[] MAKE_HIGHER = {"Erhöhe", "Vergrößere", "Steigere"};
        public static final String[] MAKE_LOWER = {"Verringere", "Verkleinere", "Senke"};
        public static final String[] SET = {"Setze"};
    }

    public void execute(String cmd) {
        CommandHandler.onCommand(cmd);
    }

    public void execute(String[] cmd) {
        CommandHandler.onCommand(cmd[0]);
    }
}
