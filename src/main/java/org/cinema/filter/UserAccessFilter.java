package org.cinema.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Filter for protecting the user section of the application.
 * Ensures that only users with a "USER" role can access pages under the "/user/*" URL pattern.
 * If the user is not logged in or does not have the "USER" role, they are redirected to the login page.
 */
@Slf4j
@WebFilter("/user/*")
public class UserAccessFilter implements Filter {

    /**
     * Checks the session for user privileges and either proceeds with the request or handles unauthorized access.
     *
     * @param request the servlet request.
     * @param response the servlet response.
     * @param chain the filter chain to pass the request along if authorized.
     * @throws IOException if an input or output error occurs during the filter's operation.
     * @throws ServletException if the request handling fails during the filter's operation.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        if (isUser(session)) {
            chain.doFilter(request, response);
        } else {
            handleUnauthorizedAdmin(httpRequest, httpResponse);
        }
    }

    /**
     * Verifies if the user has the "USER" role by checking the session attribute.
     *
     * @param session the HTTP session associated with the request.
     * @return true if the user has the "USER" role, false otherwise.
     */
    private boolean isUser(HttpSession session) {
        return session != null && "USER".equals(session.getAttribute("role"));
    }

    /**
     * Handles unauthorized access by redirecting to the login page with an error message.
     *
     * @param request the servlet request.
     * @param response the servlet response.
     * @throws ServletException if an error occurs during the request dispatch.
     * @throws IOException if an error occurs while forwarding the request.
     */
    private void handleUnauthorizedAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.warn("User is not logged in!");
        request.setAttribute("message",  "Error! You must log in.");
        request.getRequestDispatcher("/login").forward(request, response);
    }
}

