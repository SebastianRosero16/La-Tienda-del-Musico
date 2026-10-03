package com.tienda.instrumentos.patrones.estructurales;

/**
 * =====================================================================
 * PATRON ESTRUCTURAL: ADAPTER - Parte 1/3
 * =====================================================================
 *
 * Esta interfaz representa el sistema de notificaciones que
 * NUESTRO codigo conoce y usa internamente.
 * Define el contrato: "cualquier notificador debe poder enviar un mensaje".
 */
public interface NotificadorExterno {

    /**
     * Envia una notificacion al destinatario indicado.
     *
     * @param destinatario puede ser un correo, un numero de celular, etc.
     * @param asunto       titulo o asunto del mensaje
     * @param mensaje      cuerpo del mensaje
     */
    void enviarNotificacion(String destinatario, String asunto, String mensaje);
}
