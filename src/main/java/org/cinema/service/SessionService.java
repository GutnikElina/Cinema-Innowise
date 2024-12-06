package org.cinema.service;

import org.cinema.model.FilmSession;
import java.util.List;
import java.util.Optional;

public interface SessionService {
    List<FilmSession> findAll();
    Optional<FilmSession> findById(int id);
    String save(String movieTitle, String dateStr, String startTimeStr, String endTimeStr,
                String capacityStr, String priceStr);
    String update(int id, String movieTitle, String dateStr, String startTimeStr, String endTimeStr,
                  String capacityStr, String priceStr);
    String delete(int id);
}
