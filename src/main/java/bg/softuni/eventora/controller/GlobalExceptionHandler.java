package bg.softuni.eventora.controller;

import bg.softuni.eventora.exception.BusinessRuleException;
import bg.softuni.eventora.exception.EventoraAccessDeniedException;
import bg.softuni.eventora.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String notFound(ResourceNotFoundException exception, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return errorPage("Страницата не е намерена", exception.getMessage(), 404, model);
    }

    @ExceptionHandler(EventoraAccessDeniedException.class)
    public String accessDenied(EventoraAccessDeniedException exception, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return errorPage("Нямаш достъп", exception.getMessage(), 403, model);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public String businessRule(BusinessRuleException exception, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return errorPage("Заявката не може да бъде изпълнена", exception.getMessage(), 400, model);
    }

    private String errorPage(String title, String message, int status, Model model) {
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);
        model.addAttribute("statusCode", status);
        return "error";
    }
}
