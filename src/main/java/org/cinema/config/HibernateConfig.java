package org.cinema.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Конфигурационный класс для работы с Hibernate.
 * Отвечает за инициализацию и завершение работы фабрики сессий {@link SessionFactory}.
 */
@Slf4j
public class HibernateConfig {
    /**
     * Глобальная фабрика сессий Hibernate.
     * Создаётся при загрузке класса и используется для управления сессиями в приложении.
     */
    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Создаёт и настраивает фабрику сессий Hibernate.
     * Загружает настройки из файла конфигурации hibernate.cfg.xml.
     *
     * @return настроенная фабрика сессий {@link SessionFactory}.
     * @throws IllegalStateException если инициализация фабрики завершилась неудачно.
     */
    private static SessionFactory buildSessionFactory() {
        try {
            log.info("Loading Hibernate SessionFactory...");
            return new Configuration().configure().buildSessionFactory();
        } catch (HibernateException e) {
            log.error("Error during SessionFactory initialization: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to initialize SessionFactory.", e);
        }
    }

    /**
     * Завершает работу фабрики сессий Hibernate.
     * Вызывает метод {@link SessionFactory#close()}, чтобы освободить ресурсы.
     * Если фабрика уже закрыта или равна {@code null}, выполняет безопасную проверку и выводит предупреждение.
     */
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
