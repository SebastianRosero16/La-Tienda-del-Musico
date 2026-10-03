# Sistema de Gestión de Préstamos de Instrumentos Musicales
**Proyecto Evaluativo - Programación Orientada a Objetos | Momento 2**

---

## Cómo ejecutar el proyecto

### Requisitos
- Java 17 o superior
- Maven 3.6+

### Pasos
```bash
# Desde la carpeta raiz del proyecto:
mvn spring-boot:run
```

La aplicación queda disponible en:
- **API REST:** http://localhost:8080/api
- **Consola H2 (base de datos):** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:tiendainstrumentos`
  - Usuario: `sa` | Contraseña: (vacía)

---

## Endpoints disponibles para la DEMO

### Instrumentos
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/instrumentos | Lista todos |
| GET | /api/instrumentos/disponibles | Solo disponibles |
| GET | /api/instrumentos/{id} | Buscar por ID |
| POST | /api/instrumentos | Registrar nuevo |
| PUT | /api/instrumentos/{id}/reservar | Cambiar a RESERVADO |
| PUT | /api/instrumentos/{id}/alquilar | Cambiar a ALQUILADO |
| PUT | /api/instrumentos/{id}/devolver | Registrar devolución |
| PUT | /api/instrumentos/{id}/reparacion | Enviar a reparación |
| PUT | /api/instrumentos/{id}/reparacion/terminar | Terminar reparación |

### Clientes
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/clientes | Lista todos |
| GET | /api/clientes/{id} | Buscar por ID |
| GET | /api/clientes/buscar?nombre=X | Buscar por nombre |
| POST | /api/clientes | Registrar nuevo |

### Reservas
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/reservas | Lista todas |
| GET | /api/reservas/activas | Solo activas |
| GET | /api/reservas/cliente/{id} | Reservas de un cliente |
| POST | /api/reservas | Crear reserva |
| PUT | /api/reservas/{id}/devolucion | Registrar devolución |
| DELETE | /api/reservas/{id} | Cancelar reserva |

---

## Ejemplos de JSON para la DEMO

### Registrar instrumento
```json
POST /api/instrumentos
{
  "tipo": "GUITARRA_ACUSTICA",
  "marca": "Yamaha",
  "modelo": "F310",
  "numeroDeSerie": "YAM-100",
  "precioPorDia": 25000
}
```

### Registrar cliente
```json
POST /api/clientes
{
  "nombre": "Juan Pérez",
  "numeroIdentificacion": "12345678",
  "celular": "3001234567"
}
```

### Crear reserva (activa Builder + State + Strategy + Observer)
```json
POST /api/reservas
{
  "clienteId": 1,
  "instrumentoId": 1,
  "fechaInicio": "2024-02-01",
  "fechaDevolucion": "2024-02-08",
  "tipoDescuento": "VIP"
}
```

### Registrar devolución
```json
PUT /api/reservas/1/devolucion
{
  "estadoDevolucion": "RAYADO"
}
```
