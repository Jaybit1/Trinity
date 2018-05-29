package com.trinity.trinity.client.syntax;

import java.util.ArrayList;
import java.util.List;

public class SyntaxLibrary {

    public static TrinityTask getTrinityTaskFromSentence(String sentence) {
        TrinityTask task = TrinityTask.NONE;
        List<SyntaxElement> syntaxElements = getSentenceSyntax(sentence);
        int who = 0;
        boolean not = false;
        int answerSth = -1;
        TrinityTask objectiveTask = null;

        for (SyntaxElement element : syntaxElements) {
            if (element == SyntaxElement.DU) who = 0;
            else if (element == SyntaxElement.ICH) who = 1;
            else if (element == SyntaxElement.WIR) who = 2;
            else if (element == SyntaxElement.FRAGE_KÖNNEN) answerSth = 0;
            else if (element == SyntaxElement.FRAGE_WAS) answerSth = 1;
            else if (element == SyntaxElement.FRAGE_WIE) answerSth = 2;
            else if (element == SyntaxElement.FRAGE_GRUND) answerSth = 3;
            else if (element == SyntaxElement.FRAGE_OB) answerSth = 4;
            else if (element == SyntaxElement.NICHT || element == SyntaxElement.SCHLIESSEN) not = !not;
            else if (element == SyntaxElement.MUSIK) objectiveTask = TrinityTask.PLAY_MUSIC;
            else if (element == SyntaxElement.VERBINDUNG) objectiveTask = TrinityTask.CONNECT;
        }
        if (not && objectiveTask != null) {
            if (objectiveTask == TrinityTask.CONNECT) objectiveTask = TrinityTask.DISCONNECT;
            else if (objectiveTask == TrinityTask.PLAY_MUSIC) objectiveTask = TrinityTask.STOP_MUSIC;
        }
        if (objectiveTask != null) {
            if (answerSth == 4) {
                if (objectiveTask == TrinityTask.CONNECT || objectiveTask == TrinityTask.DISCONNECT) objectiveTask = TrinityTask.IS_CONNECTED;
                else if (objectiveTask == TrinityTask.PLAY_MUSIC) objectiveTask = TrinityTask.IS_MUSIC;
                else if (objectiveTask == TrinityTask.STOP_MUSIC) objectiveTask = TrinityTask.IS_MUSIC;
            }
            task = objectiveTask;
        }

        return task;
    }

    public static List<SyntaxElement> getSentenceSyntax(String sentence) {
        sentence = sentence.replace(",", "");
        List<SyntaxElement> out = new ArrayList<SyntaxElement>();
        String[] split = sentence.split(" ");
        for (String word : split) {
            SyntaxElement element = getSyntaxElement(word);
            if (element != SyntaxElement.NONE) out.add(element);
        }
        return out;
    }

    public static SyntaxElement getSyntaxElement(String word) {

        word = word.toLowerCase();
        for (SyntaxList le : list) {
            if (le.hasKey(word)) return le.getElement();
        }

        return SyntaxElement.NONE;
    }

    private static SyntaxList[] list = new SyntaxList[] {
            new SyntaxList(SyntaxElement.NICHT, new String[] { "nicht" }),
            new SyntaxList(SyntaxElement.MACHE, new String[] { "mach", "mache", "tu", "tue", "gib", "gebe", "geb", "versuche", "versuch" }),
            new SyntaxList(SyntaxElement.FRAGE_WAS, new String[] { "was" }),
            new SyntaxList(SyntaxElement.FRAGE_WIE, new String[] { "wie" }),
            new SyntaxList(SyntaxElement.FRAGE_GRUND, new String[] { "warum", "wieso" }),
            new SyntaxList(SyntaxElement.CREATE, new String[] { "erstell", "erstelle", "erstellen", "erschaffe", "erschaffen", "kreiere", "kreieren" }),
            new SyntaxList(SyntaxElement.FRAGE_KÖNNEN, new String[] { "kann", "kannst", "können" }),
            new SyntaxList(SyntaxElement.ICH, new String[] { "ich" }),
            new SyntaxList(SyntaxElement.DU, new String[] { "du" }),
            new SyntaxList(SyntaxElement.WIR, new String[] { "wir" }),
            new SyntaxList(SyntaxElement.VERBINDUNG, new String[] {"verbindung", "verbinde", "verbinden", "verbunden"}),
            new SyntaxList(SyntaxElement.ZU, new String[] { "zu", "auf" }),
            new SyntaxList(SyntaxElement.SCHLIESSEN, new String[] { "schließe", "schliesse", "schließen", "schliessen", "stop", "stopp", "stoppen", "anhalten",
                    "ausmachen", "abbrechen", "ausschalten", "aus", "brich", "breche" }),
            new SyntaxList(SyntaxElement.MUSIK, new String[] { "musik", "sound" }),
            new SyntaxList(SyntaxElement.FRAGE_OB, new String[] { "bist", "sind" })
    };

}
