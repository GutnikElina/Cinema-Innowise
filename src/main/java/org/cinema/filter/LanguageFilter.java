package org.cinema.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

/**
 * Filter that manages the user's preferred language for the application.
 * This filter checks the "lang" parameter in the request, updates the session language attribute,
 * and sets the locale for the application based on the selected language.
 * It applies to all requests ("/").
 */
@WebFilter("/*")
public class LanguageFilter implements Filter {

    /**
     * Intercepts all incoming requests to check and set the preferred language.
     * If the "lang" parameter is provided in the request, it updates the session attribute.
     * If no language is specified, the default language is set to English.
     * The locale for the application is updated based on the session's language setting.
     *
     * @param request the servlet request.
     * @param response the servlet response.
     * @param chain the filter chain that passes the request and response along to the next filter or servlet.
     * @throws IOException if an I/O error occurs during request handling.
     * @throws ServletException if a servlet error occurs during request handling.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession();

        String lang = httpRequest.getParameter("lang");
        if (lang != null) {
            session.setAttribute("lang", lang);
        }

        String language = (String) session.getAttribute("lang");
        if (language == null) {
            language = "en";
        }
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        chain.doFilter(request, response);
    }
}