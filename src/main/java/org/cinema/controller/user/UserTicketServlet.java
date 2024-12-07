package org.cinema.controller.user;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet(name = "UserTicketServlet", urlPatterns = {"/user/tickets"})
public class UserTicketServlet extends HttpServlet {
}

