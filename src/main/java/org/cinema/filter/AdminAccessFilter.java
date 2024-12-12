package org.cinema.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
@WebFilter("/admin/*")
public class AdminAccessFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        if (isAdmin(session)) {
            chain.doFilter(request, response);
        } else {
            handleUnauthorizedAdmin(httpRequest, httpResponse);
        }
    }

    private boolean isAdmin(HttpSession session) {
        return session != null && "ADMIN".equals(session.getAttribute("role"));
    }

    private void handleUnauthorizedAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.warn("Admin is not logged in!");
        request.setAttribute("message",  "Error! You must log in.");
        request.getRequestDispatcher("/login").forward(request, response);
    }
}