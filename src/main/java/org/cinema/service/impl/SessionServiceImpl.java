package org.cinema.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.filmSessionDTO.FilmSessionCreateDTO;
import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.dto.filmSessionDTO.FilmSessionUpdateDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.mapper.filmSessionMapper.FilmSessionCreateMapper;
import org.cinema.mapper.filmSessionMapper.FilmSessionResponseMapper;
import org.cinema.mapper.filmSessionMapper.FilmSessionUpdateMapper;
import org.cinema.model.FilmSession;
import org.cinema.model.Movie;
import org.cinema.repository.MovieRepository;
import org.cinema.repository.SessionRepository;
import org.cinema.service.SessionService;
import org.cinema.util.ValidationUtil;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final MovieRepository movieRepository;

    @Override
    @Transactional
    public String save(FilmSessionCreateDTO createDTO, Long movieId) {
        Movie movie = findMovieById(movieId);

        ValidationUtil.validateParameters(createDTO.getPrice(), createDTO.getDate(),
                createDTO.getCapacity(), createDTO.getStartTime(), createDTO.getEndTime());

        FilmSession filmSession = FilmSessionCreateMapper.INSTANCE.toEntity(createDTO);
        filmSession.setMovie(movie);

        checkForOverlappingSessions(movieId, filmSession);

        sessionRepository.save(filmSession);
        log.info("Film session successfully added for movie '{}'.", filmSession.getMovie().getTitle());
        return "Success! Film session successfully added.";
    }

    @Override
    @Transactional
    public String update(FilmSessionUpdateDTO updateDTO, Long movieId) {
        Movie movie = findMovieById(movieId);

        ValidationUtil.validateParameters(updateDTO.getPrice(), updateDTO.getDate(),
                updateDTO.getCapacity(), updateDTO.getStartTime(), updateDTO.getEndTime());

        FilmSession filmSession = FilmSessionUpdateMapper.INSTANCE.toEntity(updateDTO);
        filmSession.setMovie(movie);

        sessionRepository.save(filmSession);
        log.info("Film session successfully updated with id '{}'.", filmSession.getId());
        return "Success! Film session successfully updated.";
    }

    @Override
    @Transactional
    public String delete(String id) {
        Long sessionId = ValidationUtil.parseLong(id);
        sessionRepository.deleteById(sessionId);
        log.info("Film session successfully deleted with id '{}'.", id);
        return "Success! Film session successfully deleted.";
    }

    @Override
    public FilmSessionResponseDTO getById(String id) {
        Long sessionId = ValidationUtil.parseLong(id);
        return sessionRepository.findById(sessionId)
                .map(FilmSessionResponseMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new NoDataFoundException("Error! Film session not found."));
    }

    @Override
    public List<FilmSessionResponseDTO> findAll() {
        return sessionRepository.findAll().stream()
                .map(FilmSessionResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FilmSessionResponseDTO> findByDate(String dateStr) {
        ValidationUtil.validateDate(LocalDate.parse(dateStr));
        return sessionRepository.findByDate(LocalDate.parse(dateStr)).stream()
                .map(FilmSessionResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    private Movie findMovieById(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new NoDataFoundException("Error! Movie with ID " + movieId + " doesn't exist!"));
    }

    private void checkForOverlappingSessions(Long movieId, FilmSession filmSession) {
        if (sessionRepository.existsOverlappingSession(movieId, filmSession.getDate(),
                filmSession.getStartTime(), filmSession.getEndTime())) {
            throw new EntityAlreadyExistException("Error! Film session already exists on this film and time. Try again.");
        }
    }
}