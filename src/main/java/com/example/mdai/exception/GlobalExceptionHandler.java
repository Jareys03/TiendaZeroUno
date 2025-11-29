package com.example.mdai.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1) Ejemplo: error de recurso no encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex,
                                 HttpServletRequest request,
                                 Model model) {

        logger.error("Recurso no encontrado: {} - URL: {}", ex.getMessage(), request.getRequestURI());

        model.addAttribute("mensaje", ex.getMessage());
        return "error/generic";  // o "error/404" si algún día creas una vista específica
    }

    // 2) Ejemplo: errores de servicio
    @ExceptionHandler(ServiceException.class)
    public String handleService(ServiceException ex,
                                HttpServletRequest request,
                                Model model) {

        logger.error("Error de servicio: {} - URL: {}", ex.getMessage(), request.getRequestURI());

        model.addAttribute("mensaje", ex.getMessage());
        return "error/generic";
    }

    // 3) Catch-all: cualquier otra Exception que no haya sido tratada antes
    @ExceptionHandler(Exception.class)
    public String handleUnexpectedException(Exception ex,
                                            HttpServletRequest request,
                                            Model model) {

        logger.error("Excepción inesperada: {} - URL: {}", ex.getMessage(), request.getRequestURI(), ex);

        model.addAttribute("mensaje", "Ha ocurrido un error inesperado. Inténtalo de nuevo más tarde.");
        return "error/generic";
    }
}
