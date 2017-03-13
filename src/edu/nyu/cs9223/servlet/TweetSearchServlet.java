package edu.nyu.cs9223.servlet;

import edu.nyu.cs9223.elasticsearch.ElasticSearch;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class TweetSearchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String res = queryFromElasticSearch(request.getParameter("keyword"));

        try (PrintWriter out = response.getWriter()){
            out.println(res);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private String queryFromElasticSearch(String keyword) {
        return ElasticSearch.searchKeyWord(keyword);
    }
}
