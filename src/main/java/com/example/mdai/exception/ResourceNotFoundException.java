package com.example.mdai.exception;

/**
 * Excepción lanzada cuando una entidad o recurso solicitado no existe.
 *
 * Uso típico: cuando un controlador o servicio busca por id y no existe, se lanza
 * esta excepción para mapearla a un 404 (ver {@link GlobalExceptionHandler}).
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construye una {@code ResourceNotFoundException} sin mensaje.
     */
    public ResourceNotFoundException() { super(); }

    /**
     * Construye la excepción con un mensaje descriptivo.
     * @param message mensaje que describe el recurso no encontrado
     */
    public ResourceNotFoundException(String message) { super(message); }

    /**
     * Construye la excepción con mensaje y causa anidada.
     * @param message mensaje descriptivo
     * @param cause causa original que provocó esta excepción
     */
    public ResourceNotFoundException(String message, Throwable cause) { super(message, cause); }
}
