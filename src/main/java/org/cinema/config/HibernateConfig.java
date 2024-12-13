package org.cinema.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Configuration class for managing Hibernate.
 * Responsible for initializing and shutting down the {@link SessionFactory}.
 */
@Slf4j
@WebListener
public class HibernateConfig implements ServletContextListener {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory isn't initialized.");
        }
        return sessionFactory;
    }

    /**
     * Initializes the session factory at the application startup.
     * This method is called when the server starts.
     *
     * @param sce the servlet context event object
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            log.debug("Initializing Hibernate SessionFactory...");
            sessionFactory = new Configuration().configure().buildSessionFactory();
            sce.getServletContext().setAttribute("SessionFactory", sessionFactory);
            log.info("Hibernate SessionFactory initialized successfully.");
        } catch (HibernateException e) {
            log.error("Failed to initialize Hibernate SessionFactory(contextInitialized): {}", e.getMessage());
            throw new RuntimeException("SessionFactory initialization failed.", e);
        }
    }

    /**
     * Closes the session factory when the application shuts down.
     * This method is called when the server stops.
     *
     * @param sce the servlet context event object
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (sessionFactory != null) {
            try {
                log.debug("Closing Hibernate SessionFactory...");
                sessionFactory.close();
                log.info("Hibernate SessionFactory closed successfully.");
            } catch (HibernateException e) {
                log.error("Error closing Hibernate SessionFactory(contextDestroyed): {}", e.getMessage(), e);
            }
        } else {
            log.warn("SessionFactory is null, nothing to close.");
        }
    }
}
