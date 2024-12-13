package org.cinema.repository;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Base DAO class that provides common methods for handling database transactions.
 * This class contains methods to execute Hibernate transactions that either do or don't return results.
 * It abstracts the session management and transaction handling to simplify database operations for subclasses.
 * This class is intended to be extended by other Repositories that interact with specific entities.
 */
@Slf4j
public class BaseRepository {

    protected final SessionFactory sessionFactory;

    protected BaseRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Executes a transaction that does not return a result (e.g., insert, update, delete).
     *
     * @param action the operation to be performed within the transaction
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
            log.error("Hibernate error during transaction execution: {}", e.getMessage());
            handleTransactionRollback(transaction);
            throw new RuntimeException("Hibernate error during transaction.", e);
        } catch (Exception e) {
            log.error("Unexpected error during transaction without result: {}", e.getMessage());
            handleTransactionRollback(transaction);
            throw new RuntimeException("Unexpected error during transaction.", e);
        }
    }

    /**
     * Executes an operation that returns a result (e.g., fetching data).
     *
     * @param action the operation to be performed
     * @param <R>    the type of the result
     * @return the result of the operation
     */
    protected <R> R executeWithResult(Function<Session, R> action) {
        try (Session session = sessionFactory.openSession()) {
            log.debug("Session opened for operation...");
            R result = action.apply(session);
            log.debug("Transaction successfully completed!");
            return result;
        } catch (HibernateException e) {
            log.error("Hibernate error during operation: {}", e.getMessage());
            throw new RuntimeException("Hibernate error during operation.", e);
        } catch (Exception e) {
            log.error("Unexpected error during operation: {}", e.getMessage());
            throw new RuntimeException("Unexpected error during operation.", e);
        }
    }

    /**
     * Handles transaction rollback in case of an error.
     *
     * @param transaction the transaction to roll back
     */
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
