package org.cinema.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Конфигурационный класс для работы с Hibernate.
 * Отвечает за инициализацию и завершение работы фабрики сессий {@link SessionFactory}.
 */
@Slf4j
@WebListener
public class HibernateConfig implements ServletContextListener {

    private static SessionFactory sessionFactory;

    /**
     * Возвращает экземпляр {@link SessionFactory}.
     *
     * @return {@link SessionFactory}
     * @throws IllegalStateException если фабрика сессий не была инициализирована.
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory isn't initialized.");
        }
        return sessionFactory;
    }

    /**
     * Инициализирует фабрику сессий при старте приложения.
     * Этот метод вызывается при запуске сервера.
     *
     * @param sce объект, представляющий событие контекста сервлета
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
     * Закрывает фабрику сессий при завершении работы приложения.
     * Этот метод вызывается при остановке сервера.
     *
     * @param sce объект, представляющий событие контекста сервлета
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (sessionFactory != null) {
            try {
                log.info("Closing Hibernate SessionFactory...");
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
