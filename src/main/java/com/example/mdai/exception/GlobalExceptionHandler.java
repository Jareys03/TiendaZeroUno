package com.example.mdai.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Manejador global de excepciones para la aplicación.
 *
 * Centraliza la conversión de excepciones en vistas de error (plantilla
 * "error/generic") y establece códigos de estado HTTP apropiados.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja las {@link ResourceNotFoundException} y devuelve una vista con 404.
     * @param ex excepción lanzada
     * @param req petición HTTP actual
     * @return ModelAndView apuntando a la plantilla de error
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        logger.warn("Recurso no encontrado: {} - URL: {}", ex.getMessage(), req.getRequestURI());
        ModelAndView mav = new ModelAndView();
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("error", ex.getMessage());
        mav.setViewName("error/generic");
        return mav;
    }

    /**
     * Maneja las {@link ServiceException} lanzadas por la capa de servicios.
     * Devuelve un 500 y un mensaje genérico para no exponer detalles internos.
     */
    @ExceptionHandler(ServiceException.class)
    public ModelAndView handleServiceError(ServiceException ex, HttpServletRequest req) {
        logger.error("Error en la capa de servicios: {} - URL: {}", ex.getMessage(), req.getRequestURI(), ex);
        ModelAndView mav = new ModelAndView();
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        mav.addObject("error", "Ocurrió un error interno. Por favor inténtalo más tarde.");
        mav.setViewName("error/generic");
        return mav;
    }

    /**
     * Maneja excepciones de tipo {@link IllegalArgumentException} y devuelve 400.
     * Útil para validar entradas y devolver mensajes claros al usuario.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
        logger.warn("Solicitud inválida: {} - URL: {}", ex.getMessage(), req.getRequestURI());
        ModelAndView mav = new ModelAndView();
        mav.setStatus(HttpStatus.BAD_REQUEST);
        mav.addObject("error", ex.getMessage());
        mav.setViewName("error/generic");
        return mav;
    }

    /**
     * Maneja cualquier excepción no prevista y devuelve un 500 genérico.
     * Siempre debe ser la última captura para evitar que excepciones no
     * controladas propaguen una traza al usuario final.
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneric(Exception ex, HttpServletRequest req) {
        logger.error("Excepción inesperada: {} - URL: {}", ex.getMessage(), req.getRequestURI(), ex);
        ModelAndView mav = new ModelAndView();
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        mav.addObject("error", "Error inesperado. Contacta con el administrador.");
        mav.setViewName("error/generic");
        return mav;
    }
}
