package org.cinema.handler;

import lombok.extern.slf4j.Slf4j;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.exception.OmdbApiException;
import org.cinema.util.ConstantsUtil;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collections;

@Slf4j
public class ErrorHandler {

    public static void handleError(Model model, Exception e) {
        String userMessage = resolveErrorMessage(e);
        log.error(userMessage, e);
        model.addAttribute(ConstantsUtil.MOVIES_PARAM, Collections.emptyList());
        model.addAttribute(ConstantsUtil.MESSAGE_PARAM, userMessage);
    }

    public static void handleError(RedirectAttributes redirectAttributes, String message, Exception e) {
        log.error(message, e);
        redirectAttributes.addFlashAttribute(ConstantsUtil.MESSAGE_PARAM, message);
    }

    public static String resolveErrorMessage(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return "Error! Invalid input: " + e.getMessage();
        } else if (e instanceof NoDataFoundException) {
            return "Error! No data found: " + e.getMessage();
        } else if (e instanceof EntityAlreadyExistException) {
            return e.getMessage();
        } else if (e instanceof OmdbApiException) {
            return "Error! Failed to communicate with OMDB API. Please try again later.";
        } else {
            return "An unexpected error occurred.";
        }
    }
}
