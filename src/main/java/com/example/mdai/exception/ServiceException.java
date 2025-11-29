package com.example.mdai.exception;

/**
 * Excepción genérica de la capa de servicios.
 *
 * Se utiliza para encapsular errores que ocurren en los servicios de negocio y
 * que deben propagarse hasta un manejador global para devolver una respuesta
 * de error adecuada al usuario.
 */
public class ServiceException extends RuntimeException {

    /** Construye una excepción sin mensaje. */
    public ServiceException() { super(); }

    /**
     * Construye la excepción con un mensaje descriptivo.
     * @param message texto que describe el error en la capa de servicios
     */
    public ServiceException(String message) { super(message); }

    /**
     * Construye la excepción con mensaje y causa anidada.
     * @param message mensaje descriptivo
     * @param cause causa original
     */
    public ServiceException(String message, Throwable cause) { super(message, cause); }
}
