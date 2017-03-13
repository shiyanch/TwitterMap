package edu.nyu.cs9223.servlet;

import edu.nyu.cs9223.elasticsearch.ElasticSearch;
import edu.nyu.cs9223.util.DateConvetor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class TweetDistanceServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lon = Double.parseDouble(request.getParameter("lon"));
        long distance = Long.parseLong(request.getParameter("distance"));
        String res = fetchWithinDistance(lat, lon, distance);

        try (PrintWriter out = response.getWriter()){
            out.println(res);
        }
    }

    private String fetchWithinDistance(double lat, double lon, long distance) {
        return ElasticSearch.filterTweetWithinDistance(lat, lon, distance);
    }
}
