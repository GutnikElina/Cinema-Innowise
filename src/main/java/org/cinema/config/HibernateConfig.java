package org.cinema.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Slf4j
public class HibernateConfig {
    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            log.info("Loading Hibernate SessionFactory...");
            return new Configuration().configure().buildSessionFactory();
        } catch (HibernateException e) {
            log.error("Error during SessionFactory initialization: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to initialize SessionFactory.", e);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            try {
                log.debug("Closing Hibernate SessionFactory...");
                sessionFactory.close();
                log.info("SessionFactory closed successfully.");
            } catch (HibernateException e) {
                log.error("Error closing SessionFactory: {}", e.getMessage(), e);
            }
        } else {
            log.warn("SessionFactory is already null. No action required.");
        }
    }
}
