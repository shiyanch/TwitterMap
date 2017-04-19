package edu.nyu.cs9223.servlet;

import edu.nyu.cs9223.elasticsearch.ElasticSearch;
import edu.nyu.cs9223.twitterstream.TwitterStream;
import edu.nyu.cs9223.util.DateConvetor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class TweetFetchServlet extends HttpServlet {
    private String lastTime = "";

    @Override
    public void init() throws ServletException {
        TwitterStream stream = new TwitterStream();
        Thread thread = new Thread(stream);
        thread.start();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String res = fetchFromElasticSearch(request.getParameter("all"));

        try (PrintWriter out = response.getWriter()){
            out.println(res);
        }
    }

    private String fetchFromElasticSearch(String parameter) {
        if (parameter != null) {
            return ElasticSearch.fetchAllTweets();
        }

        String temp = lastTime;
        lastTime = DateConvetor.currentDateTime();
        return temp.isEmpty()?ElasticSearch.fetchAllTweets():ElasticSearch.fetchLastestTweets(temp);
    }
}
