package com;

import com.service;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class JobServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String skill = req.getParameter("skill");

        req.setAttribute("jobs", JobService.search(keyword, skill));
        req.getRequestDispatcher("jobs.jsp").forward(req, resp);
    }
}
