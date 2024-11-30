package org.cinema.dao;

import lombok.extern.slf4j.Slf4j;
import org.cinema.config.HibernateConfig;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class BaseDao {
    protected static final SessionFactory sessionFactory = HibernateConfig.getSessionFactory();

    /**
     * Выполняет транзакцию без возврата результата (добавление, обновление, удаление).
     * @param action действие, которое должно быть выполнено в рамках транзакции
     */
    protected void executeTransaction(Consumer<Session> action) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            log.debug("Transaction started...");
            transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
            log.debug("Transaction successfully completed.");
        } catch (HibernateException e) {
            log.error("Hibernate error during transaction execution: {}", e.getMessage(), e);
            handleTransactionRollback(transaction);
        } catch (Exception e) {
            log.error("Unexpected error during transaction without result: {}", e.getMessage(), e);
            handleTransactionRollback(transaction);
        }
    }

    /**
     * Выполняет транзакцию, которая возвращает результат (получение данных).
     * @param action действие, которое должно быть выполнено в рамках транзакции
     * @param <R> тип результата
     * @return результат выполнения действия
     */
    protected <R> R executeTransactionWithResult(Function<Session, R> action) {
        Transaction transaction = null;
        R result = null;
        try (Session session = sessionFactory.openSession()) {
            log.debug("Transaction with result started...");
            transaction = session.beginTransaction();
            result = action.apply(session);
            transaction.commit();
            log.debug("Transaction successfully completed with result: {}", result != null ? result : "No result returned");
        } catch (HibernateException e) {
            log.error("Hibernate error during transaction with result: {}", e.getMessage(), e);
            handleTransactionRollback(transaction);
        } catch (Exception e) {
            log.error("Unexpected error during transaction with result: {}", e.getMessage(), e);
            handleTransactionRollback(transaction);
        }
        return result;
    }

    private void handleTransactionRollback(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
                log.warn("Transaction rolled back due to an error.");
            } catch (HibernateException e) {
                log.error("Error during transaction rollback: {}", e.getMessage(), e);
            }
        }
    }
}
