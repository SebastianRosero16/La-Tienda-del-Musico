package com.tienda.instrumentos.dominio.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * =====================================================================
 * TEMAS CUBIERTOS:
 *   - Fundamentos del Modelado de Dominio: Reserva es la entidad
 *     que conecta un Cliente con un Instrumento en el tiempo.
 *   - Encapsulamiento: atributos privados, logica interna
 *   - Encapsulamiento de Reglas de Negocio: calculos y validaciones
 *     de fechas viven dentro de la entidad
 * =====================================================================
 */
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacion con el cliente que hace la reserva
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Relacion con el instrumento reservado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrumento_id", nullable = false)
    private Instrumento instrumento;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaDevolucion;

    @Column(nullable = false)
    private double precioTotal;

    // Estado de la reserva
    @Column(nullable = false)
    private boolean activa;

    // Estado del instrumento al momento de entregarlo
    @Column
    private String estadoAlMomentoDeEntrega;

    // Estado del instrumento cuando es devuelto
    @Column
    private String estadoAlMomentoDeDevolucion;

    // Constructor vacio requerido por JPA
    public Reserva() {}

    public Reserva(Cliente cliente, Instrumento instrumento,
                   LocalDate fechaInicio, LocalDate fechaDevolucion) {
        // Validacion de regla de negocio: la fecha de devolucion debe ser posterior
        if (!fechaDevolucion.isAfter(fechaInicio)) {
            throw new IllegalArgumentException(
                "La fecha de devolucion debe ser posterior a la fecha de inicio");
        }
        this.cliente = cliente;
        this.instrumento = instrumento;
        this.fechaInicio = fechaInicio;
        this.fechaDevolucion = fechaDevolucion;
        this.activa = true;

        // Calculamos el precio total automaticamente al crear la reserva
        int dias = calcularDias();
        this.precioTotal = instrumento.calcularPrecioAlquiler(dias);

        // Guardamos el estado actual del instrumento al momento de entregarlo
        this.estadoAlMomentoDeEntrega = instrumento.getEstado().getDescripcion();
    }

    // =========================================================
    // ENCAPSULAMIENTO DE REGLAS DE NEGOCIO
    // =========================================================

    /**
     * Calcula cuantos dias dura el alquiler.
     * La logica esta encapsulada aqui, no en el servicio.
     */
    public int calcularDias() {
        return (int) ChronoUnit.DAYS.between(fechaInicio, fechaDevolucion);
    }

    /**
     * Verifica si la reserva esta activa (aun no fue devuelta).
     */
    public boolean estaActiva() {
        return this.activa;
    }

    /**
     * Verifica si el instrumento fue devuelto tarde.
     */
    public boolean estaVencida() {
        return activa && LocalDate.now().isAfter(fechaDevolucion);
    }

    /**
     * Finaliza la reserva cuando el cliente devuelve el instrumento.
     */
    public void finalizarReserva(String estadoAlDevolver) {
        this.activa = false;
        this.estadoAlMomentoDeDevolucion = estadoAlDevolver;
    }

    // GETTERS Y SETTERS
    public Long getId() { return id; }

    public Cliente getCliente() { return cliente; }

    public Instrumento getInstrumento() { return instrumento; }

    public LocalDate getFechaInicio() { return fechaInicio; }

    public LocalDate getFechaDevolucion() { return fechaDevolucion; }

    public double getPrecioTotal() { return precioTotal; }

    public String getEstadoAlMomentoDeEntrega() { return estadoAlMomentoDeEntrega; }

    public String getEstadoAlMomentoDeDevolucion() { return estadoAlMomentoDeDevolucion; }

    public void setActiva(boolean activa) { this.activa = activa; }

    @Override
    public String toString() {
        return String.format("Reserva #%d | %s | %s -> %s | %d dias | Total: $%.0f",
                id, cliente.getNombre(), fechaInicio, fechaDevolucion,
                calcularDias(), precioTotal);
    }
}
