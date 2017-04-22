package edu.nyu.cs9223.elasticsearch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ElasticSearch {
    private static final String ES_URL = "";

    public static void indexToElasticSearch(String body) {
        postWithBody("", body);
    }

    public static String fetchAllTweets() {
        String json = "{\n" +
                "\"from\": 0," +
                "\"size\": 100," +
                "\t\"query\" : {\n" +
                "        \"match_all\": {}\n" +
                "    },\n" +
                "    \"sort\": { \"date\": { \"order\": \"desc\" }}\n" +
                "}";
        return postWithBody("_search", json);
    }

    public static String fetchLastestTweets(String lastTime) {
        String json = "{\n" +
                "\"from\": 0," +
                "\"size\": 100," +
                "    \"query\": {\n" +
                "        \"range\" : {\n" +
                "            \"date\" : {\n" +
                "                \"gte\" : \"" + lastTime + "\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        return postWithBody("_search", json);
    }

    public static String filterTweetWithinDistance(double lat, double lon, long distance) {
        String json = "{\n" +
                "   \"query\" : {\n" +
                "      \"bool\" : { \n" +
                "         \"must\" : {\n" +
                "           \"geo_distance\": {\n" +
                "               \"distance\": \"" + distance + "km\", \n" +
                "               \"location\": [" + lat + ", " + lon + "]\n" +
                "           }\n" +
                "         }\n" +
                "      }\n" +
                "   }\n" +
                "}";
        return postWithBody("_search", json);
    }

    public static String searchKeyWord(String keyword) {
        return getWithParameter("_search?q=text:"+keyword);
    }

    private static String getWithParameter(String postUrl) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(ES_URL+postUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Accept-Encoding", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String temp;
            while((temp = reader.readLine()) != null) {
                sb.append(temp).append(" ");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }

    private static String postWithBody(String postUrl, String body) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(ES_URL+postUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Accept-Encoding", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStream os = connection.getOutputStream();
            os.write(body.getBytes("UTF-8"));
            os.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String temp;
            while((temp = reader.readLine()) != null) {
                sb.append(temp).append(" ");
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }
}
