package org.cinema.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

@WebFilter("/*")
public class LanguageFilter implements Filter {

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