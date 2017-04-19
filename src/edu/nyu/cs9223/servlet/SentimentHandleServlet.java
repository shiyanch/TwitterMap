package edu.nyu.cs9223.servlet;

import edu.nyu.cs9223.elasticsearch.ElasticSearch;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class SentimentHandleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tweetJson = request.getParameter("tweet");
        System.out.println(tweetJson);
        ElasticSearch.indexToElasticSearch(tweetJson);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuffer sb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                sb.append(line);
        } catch (Exception e) { /*report an error*/ }

        System.out.println(sb.toString());
        if (sb.length() != 0) {
            ElasticSearch.indexToElasticSearch(sb.toString());
        }
    }
}
