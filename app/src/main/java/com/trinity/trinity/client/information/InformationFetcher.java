package com.trinity.trinity.client.information;

import android.app.SearchManager;
import android.content.Intent;

import com.trinity.trinity.Util;
import com.trinity.trinity.client.MainActivity;
import com.trinity.trinity.client.Trinity;
import com.trinity.trinity.client.commands.CommandHandler;
import com.trinity.trinity.client.commands.Commands;
import com.trinity.trinity.client.soundsystem.TTS;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class InformationFetcher {

    public static InformationFetcher INSTANCE;

    public static InformationFetcher getInstance() {
        if (INSTANCE == null)
            INSTANCE = new InformationFetcher();
        return INSTANCE;
    }


    public void fetchInformation(final String cmd) {
        if (!isLocalCmd(cmd)) {
            CommandHandler.executeQNA("Ich kann dir damit nicht weiterhelfen. Soll ich eine Internet suche starten?", new Runnable() {
                @Override
                public void run() {
                    executeWikipediaSearch(cmd);
                }
            });
        }
    }

    private boolean isLocalCmd(String cmd) {
        return false;
    }

    public void executeGoogleSearch(String search) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, search);
        MainActivity.getInstance().startActivity(intent);
    }

    public void executeWikipediaSearch (String key) {
        final String originalKey = key;
        if (CommandHandler.cmdStartsWith(key, Commands.generic.FETCH_INFORMATION)) {
            key = Util.removeFirstWord(key);
            // Remove verb
            if (CommandHandler.cmdStartsWith(key, Commands.information.VERB))
                key = Util.removeFirstWord(key);
            // Remove article
            if (CommandHandler.cmdStartsWith(key, Commands.information.ADJECTIVE))
                key = Util.removeFirstWord(key);
        }
        final String finalKey = key;

        new Thread(new Runnable() {
            @Override
            public void run() {
                /*ArrayList<String> resultTitles = getResultTitles(finalKey);
                if (resultTitles == null || resultTitles.size() == 0) {
                    executeGoogleSearch(originalKey);
                    return;
                } Map<String, Integer> intices = new HashMap<>();
                for (int i=0; i<resultTitles.size(); i++)
                    intices.put(resultTitles.get(i), i);

                Collections.sort(resultTitles, new Comparator<String>() {
                    @Override
                    public int compare(String r1, String r2) {
                        double val1 = Util.getSimilarity(finalKey, r1);
                        double val2 = Util.getSimilarity(finalKey, r2);
                        if (val1 < val2)
                            return 1;
                        if (val1 > val2)
                            return -1;
                        return 0;
                    }
                });
                int indexOfBestResult = intices.get(resultTitles.get(resultTitles.size() - 1));*/
                String extract = getResultExtract(finalKey);
                TTS.getInstance().stopAndDequeueAll();
                Trinity.log(extract);
            }
        }).start();
    }

    /*private ArrayList<String> getResultTitles(String key) {
        String url = "https://de.wikipedia.org/w/api.php?format=json&action=query&generator=search&gsrnamespace=0&gsrsearch=" + key;
        ArrayList<String> array = new ArrayList<>();
        try {
            JSONObject main = Util.JSONReader.readJsonFromUrl(url);
            JSONObject pages = main.getJSONObject("query").getJSONObject("pages");
            Iterator<String> iterator = pages.keys();
            while (iterator.hasNext()) {
                String a = iterator.next();
                String aTitle = ((JSONObject) pages.get(a)).getString("title");
                array.add(aTitle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            return array;
        }
        return array;
    }*/

    private String getResultExtract (String key) {
        String url = "https://de.wikipedia.org/w/api.php?format=json&action=query&generator=search&gsrnamespace=0&gsrlimit=1&prop=extracts&exintro=true&explaintext=true&exsentences=2&exlimit=max&gsroffset=0&gsrsearch=" + key;
        String extract = "";
        try {
            JSONObject main = Util.JSONReader.readJsonFromUrl(url);
            JSONObject pages = main.getJSONObject("query").getJSONObject("pages");
            String pageid = pages.keys().next();
            extract = Util.removeUnnecessaryParts(pages.getJSONObject(pageid).getString("extract"));
        } catch (IOException | JSONException e) { e.printStackTrace(); }

        return extract;
    }

}
