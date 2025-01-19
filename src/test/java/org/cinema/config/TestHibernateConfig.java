package org.cinema.config;

import org.cinema.model.Movie;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class TestHibernateConfig {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration()
                    .configure("hibernate-test.cfg.xml")
                    .addAnnotatedClass(Movie.class)
                    .buildSessionFactory();
        }
        return sessionFactory;
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}

