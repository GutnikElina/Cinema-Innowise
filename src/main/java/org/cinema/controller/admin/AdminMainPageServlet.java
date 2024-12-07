package org.cinema.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.model.Movie;
import org.cinema.service.MovieService;
import org.cinema.service.impl.MovieServiceImpl;
import org.cinema.service.impl.TicketServiceImpl;
import org.cinema.util.OmdbApiUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@WebServlet(name = "AdminMainPageServlet", urlPatterns = {"/admin"})
public class AdminMainPageServlet extends HttpServlet {

    private MovieService movieService;

    @Override
    public void init() {
        movieService = MovieServiceImpl.getInstance();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String message = "";
        String movieTitle = request.getParameter("movieTitle");

        List<Movie> movies = new ArrayList<>();
        try {
            movies = movieService.searchMovies(movieTitle.trim());
        } catch (Exception e) {
            log.error("Error during movie search for title {}: {}", movieTitle, e.getMessage(), e);
            message = e.getMessage();
        }

        request.setAttribute("movies", movies);
        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);
    }
}