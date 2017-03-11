package edu.nyu.cs9223.main;

import edu.nyu.cs9223.twitterstream.TwitterStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class TMServlet extends HttpServlet {
    private static final String ES_URL = "https://search-cloud-computing-cl3869-mzhj7m6rkltbbqt3zdva34st7e.us-east-1.es.amazonaws.com/geo/geo-data-sample/";

    @Override
    public void init() throws ServletException {
        TwitterStream stream = new TwitterStream();
        Thread thread = new Thread(stream);
        thread.start();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        String res = queryToElasticSearch(request.getParameter("keyword"));
        System.out.println(res);

        try {
            out.println(res);
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private String queryToElasticSearch(String keyword) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(ES_URL+"_search?q=text:"+keyword);
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

            String temp = null;
            while((temp = reader.readLine()) != null) {
                sb.append(temp).append(" ");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }
}
