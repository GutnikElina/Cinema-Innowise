package org.cinema.service;

import org.cinema.model.FilmSession;
import java.util.Optional;
import java.util.Set;

public interface SessionService {
    Set<FilmSession> findAll();
    Optional<FilmSession> getById(String id);
    String save(String movieTitle, String dateStr, String startTimeStr, String endTimeStr,
                String capacityStr, String priceStr);
    String update(String id, String movieTitle, String dateStr, String startTimeStr, String endTimeStr,
                  String capacityStr, String priceStr);
    String delete(String id);
}
