# GUION DE EXPOSICION — 3 INTEGRANTES
## Sistema de Gestión de Préstamos de Instrumentos Musicales
### Programación Orientada a Objetos — Momento 2

---

> **Antes de exponer:**
> 1. Correr el backend: `mvn spring-boot:run`
> 2. Tener el IDE abierto con el proyecto
> 3. El frontend `http://localhost:8080` se muestra solo al final

> **Distribución de temas por integrante:**
> - Integrante 1 → Modelado de Dominio, Encapsulamiento, Herencia, Abstracción, Polimorfismo
> - Integrante 2 → Singleton, Factory, Builder, Adapter, Decorator
> - Integrante 3 → Strategy, State, Observer, Repository, Servicios, Spring Boot REST, Evolución Incremental + Demo

---

## INTRODUCCIÓN GENERAL
### La dice el Integrante 1 — todos de pie

"Buenos días profesor. Nuestro proyecto es un sistema para gestionar
el alquiler de instrumentos musicales de una tienda. El dueño puede
registrar guitarras, controlar su estado físico y su disponibilidad,
y los clientes pueden hacer reservas con fechas y precios calculados
automáticamente.

Lo desarrollamos con Java y Spring Boot. Cada parte del código
refleja directamente un tema visto en el curso. Vamos a explicarlos
uno por uno abriendo cada archivo."

---

---

# INTEGRANTE 1
## Temas: Modelado de Dominio · Encapsulamiento · Herencia · Abstracción · Polimorfismo

---

### TEMA 1 — FUNDAMENTOS DEL MODELADO DE DOMINIO

**Abrir:** `dominio/enums/EstadoInstrumento.java`

"Lo primero que hicimos fue analizar el negocio real y traducirlo
a código. Identificamos tres entidades principales: Instrumento,
Cliente y Reserva. Eso es modelado de dominio: capturar en código
las cosas que existen en el mundo real del negocio.

Para los valores fijos usamos enums. El estado físico de un
instrumento solo puede ser NUEVO, USADO, RAYADO o DAÑADO.
Al usar un enum, el compilador nos impide escribir un valor
inventado. Las reglas del negocio quedan capturadas directamente
en el código."

**Abrir:** `dominio/enums/DisponibilidadInstrumento.java`

"Este enum representa el ciclo de vida del instrumento dentro
del negocio: DISPONIBLE, RESERVADO, ALQUILADO, EN_REPARACION.
Más adelante el patrón State va a usar estos valores para controlar
qué acciones están permitidas en cada momento."

**Abrir:** `dominio/enums/TipoInstrumento.java`

"Y este enum clasifica el tipo de instrumento. Al tenerlos en un
enum, si mañana agregamos Violín solo añadimos un valor aquí.
Toda la lógica que depende del tipo sigue funcionando sin cambios."

---

### TEMA 2 — ENCAPSULAMIENTO

**Abrir:** `dominio/modelo/Instrumento.java`

"Aquí está la clase Instrumento. Observen que todos los atributos
son PRIVADOS: `private String marca`, `private double precioPorDia`.
Nadie desde afuera puede modificarlos directamente.

Para cambiar el precio existe el setter `setPrecioPorDia()`, y ese
setter tiene una validación: si el precio es cero o negativo,
lanza una excepción con un mensaje claro. La regla de negocio
vive dentro del objeto, no dispersa por el sistema.
Eso es encapsulamiento: proteger los datos y controlar cómo
y cuándo se pueden modificar."

**Señalar el método `estaDisponible()`:**

"También encapsulamos comportamiento. En lugar de que cada parte
del código compare el enum de disponibilidad por su cuenta,
ofrecemos el método `estaDisponible()`. Quien use el objeto
no necesita saber cómo funciona por dentro."

**Abrir:** `dominio/modelo/Cliente.java`

"En la clase Cliente también se ve el encapsulamiento. El método
`puedeHacerNuevaReserva()` encapsula la regla de negocio de que
un cliente no puede tener más de 3 reservas activas. La regla
vive dentro del objeto Cliente, no en el servicio externo.
Y el método `getReservas()` devuelve una copia inmutable de la
lista para que nadie pueda modificarla directamente desde afuera."

---

### TEMA 3 — HERENCIA Y ABSTRACCIÓN

**Abrir:** `dominio/modelo/Instrumento.java` — señalar la palabra `abstract`

"Instrumento es una clase ABSTRACTA. ¿Por qué? Porque en la tienda
no existe 'un instrumento genérico'. Siempre es una guitarra
acústica, eléctrica o clásica. No tiene sentido crear
`new Instrumento()` directamente.

La clase define dos métodos abstractos: `calcularPrecioAlquiler()`
y `obtenerDescripcion()`. Están declarados pero sin implementación.
Le estamos diciendo a las subclases: ustedes DEBEN implementar esto,
cada una a su manera. Eso es ABSTRACCIÓN: definir el QUÉ
sin definir el CÓMO."

**Abrir:** `dominio/modelo/GuitarraAcustica.java`

"GuitarraAcustica usa `extends Instrumento` para heredar todo lo
común: marca, modelo, número de serie, precio por día, estado,
disponibilidad. No hay que repetirlos.

Pero además tiene atributos PROPIOS que no existen en otras clases:
`numeroCuerdas` y `materialCuerpo`. Eso es HERENCIA: reutilizar
lo común y agregar lo específico de cada tipo."

**Abrir:** `dominio/modelo/GuitarraElectrica.java`

"GuitarraEléctrica también extiende Instrumento. Sus atributos
propios son `tipoPastilla` e `incluyeAmplificador`. Misma herencia,
distinta especialización."

---

### TEMA 4 — POLIMORFISMO

**Abrir las tres clases juntas y comparar `calcularPrecioAlquiler`:**

"Aquí está el POLIMORFISMO. Los tres tipos implementan el mismo
método `calcularPrecioAlquiler`, pero con lógica completamente
diferente:

- Acústica: precio simple = días × precio por día
- Eléctrica: si incluye amplificador, cobra un 30% más
- Clásica: si el alquiler es de 7 días o más, aplica 10% de descuento

Si tenemos una variable de tipo `Instrumento` y llamamos al método,
el resultado cambia según qué objeto concreto hay adentro.
El mismo método, comportamiento diferente según el tipo real.
Eso es polimorfismo en acción."

---

---

# INTEGRANTE 2
## Temas: Singleton · Factory · Builder · Adapter · Decorator

---

### TEMA 5 — PATRÓN SINGLETON

**Abrir:** `patrones/creacionales/GestorTienda.java`

"El Singleton garantiza que en toda la aplicación exista exactamente
UNA instancia de un objeto. En nuestro caso: la tienda es una sola.
No tiene sentido tener dos configuraciones distintas porque habría
información incoherente.

¿Cómo funciona?
- El constructor es PRIVADO: nadie puede hacer `new GestorTienda()`
- La instancia se guarda en un atributo estático privado
- El único acceso es mediante `GestorTienda.getInstancia()`

La primera vez que se llama, crea el objeto. Las siguientes veces
devuelve el mismo. La palabra clave `synchronized` garantiza que
en aplicaciones con múltiples hilos, como Spring, no se creen
dos instancias al mismo tiempo."

---

### TEMA 6 — PATRÓN FACTORY

**Abrir:** `patrones/creacionales/InstrumentoFactory.java`

"El Factory centraliza la creación de objetos. El problema que
resuelve: cuando el dueño registra un instrumento nuevo, el servicio
no debería saber si usar `new GuitarraAcustica()` o
`new GuitarraElectrica()`. La fábrica centraliza esa decisión.

La fábrica recibe el tipo y decide qué subclase crear con un switch.
El servicio solo llama `InstrumentoFactory.crearInstrumento(tipo, ...)`.
El resto del sistema no conoce las subclases concretas.

Beneficio clave: si mañana agregamos Violín, solo modificamos
la Factory. El resto del sistema no cambia nada."

---

### TEMA 7 — PATRÓN BUILDER

**Abrir:** `patrones/creacionales/ReservaBuilder.java`

"El Builder construye objetos complejos paso a paso. El problema:
crear una Reserva requiere cuatro datos obligatorios, y si los
pusiéramos todos en un constructor sería fácil equivocarse de orden.

Con el Builder se hace así:
```
new ReservaBuilder()
    .paraCliente(cliente)
    .delInstrumento(instrumento)
    .desdeFecha(LocalDate.of(2024, 2, 1))
    .hastaFecha(LocalDate.of(2024, 2, 8))
    .build();
```

Cada método retorna el mismo Builder para poder encadenarlos.
El `build()` final valida que todo esté completo. Si falta algún
dato, lanza una excepción diciendo exactamente qué falta.
También verifica que el instrumento esté disponible y que el
cliente pueda hacer una nueva reserva, antes de construir el objeto."

---

### TEMA 8 — PATRÓN ADAPTER

**Abrir:** `patrones/estructurales/ServicioSmsLegado.java`

"Aquí tenemos un sistema de SMS externo que ya existe y tiene
su propia interfaz. Su método se llama `mandarSms(numero, texto)`.
Imaginemos que es una librería de terceros que no podemos modificar."

**Abrir:** `patrones/estructurales/NotificadorExterno.java`

"Nuestro sistema usa esta interfaz con el método
`enviarNotificacion(destinatario, asunto, mensaje)`.
Son incompatibles entre sí."

**Abrir:** `patrones/estructurales/AdaptadorSms.java`

"El Adapter soluciona esa incompatibilidad. Implementa nuestra
interfaz `NotificadorExterno` por afuera, pero internamente
usa el `ServicioSmsLegado`. Traduce los parámetros de un formato
al otro.

Es exactamente como un adaptador de corriente al viajar:
el enchufe del país tiene otra forma, pero el adaptador hace
que tu cargador funcione igual. Nuestro sistema llama
`enviarNotificacion()` sin saber que por dentro usa `mandarSms()`."

---

### TEMA 9 — PATRÓN DECORATOR

**Abrir:** `patrones/estructurales/InstrumentoDecorador.java`

"El Decorator agrega comportamiento extra a un objeto sin modificar
su clase original. Esta clase abstracta envuelve cualquier
`Instrumento` y delega todos los métodos al instrumento envuelto."

**Abrir:** `patrones/estructurales/InstrumentoConSeguro.java`

"InstrumentoConSeguro es un decorador concreto. En su
`calcularPrecioAlquiler()` llama al precio original y le suma
el costo del seguro. El instrumento decorado no sabe nada de esto.

La potencia del Decorator es que se pueden APILAR:
```
Instrumento guitarra    = new GuitarraAcustica(...);
Instrumento conSeguro   = new InstrumentoConSeguro(guitarra, 5000);
Instrumento conAfinador = new InstrumentoConAfinador(conSeguro, 2000);
// precio final = base + seguro + afinador
```

Sin el Decorator tendríamos que crear:
GuitarraConSeguro, GuitarraConAfinador, GuitarraConSeguroYAfinador...
una explosión de clases que el Decorator evita completamente."

**Abrir:** `patrones/estructurales/InstrumentoConAfinador.java`

"Este es el segundo decorador. Muestra que se pueden apilar
indefinidamente sin modificar ninguna clase original."

---

---

# INTEGRANTE 3
## Temas: Strategy · State · Observer · Repository · Servicios · Spring Boot REST · Evolución Incremental · Demo

---

### TEMA 10 — PATRÓN STRATEGY

**Abrir:** `patrones/comportamentales/EstrategiaDescuento.java`

"El Strategy permite cambiar el algoritmo de descuento en tiempo
de ejecución sin modificar el servicio. Aquí está la interfaz:
define el contrato con dos métodos: `calcularDescuento()` y
`obtenerNombreEstrategia()`."

**Abrir:** `patrones/comportamentales/DescuentoClienteVIP.java`
**Abrir:** `patrones/comportamentales/DescuentoTemporada.java`

"Hay tres implementaciones concretas:
- `DescuentoSinDescuento`: devuelve el precio tal cual
- `DescuentoClienteVIP`: aplica 20% de descuento
- `DescuentoTemporada`: aplica un porcentaje configurable

Cuando se crea una reserva, el servicio recibe el tipo de descuento,
selecciona la estrategia correcta y la aplica. Si mañana queremos
un descuento para estudiantes, solo creamos una nueva clase que
implemente la interfaz. El servicio no se modifica."

---

### TEMA 11 — PATRÓN STATE

**Abrir:** `patrones/comportamentales/EstadoDisponibilidad.java`

"El State modela que un instrumento se comporta diferente según
su estado actual. Esta interfaz define las acciones posibles:
reservar, alquilar, devolver, enviarAReparacion, terminarReparacion."

**Abrir:** `patrones/comportamentales/EstadoDisponible.java`
**Abrir:** `patrones/comportamentales/EstadoAlquilado.java`

"Cada estado es una clase que sabe qué está permitido y qué no.
Por ejemplo, en `EstadoDisponible` el método `reservar()` cambia
la disponibilidad sin problema. Pero en `EstadoAlquilado` el mismo
método `reservar()` lanza una excepción con mensaje claro.

Sin este patrón, el servicio tendría un if/else gigante para cada
combinación de estado y acción. Con State, cada estado controla
su propia lógica. Cuatro estados, cuatro clases, cero ifs en el servicio."

---

### TEMA 12 — PATRÓN OBSERVER

**Abrir:** `patrones/comportamentales/ObservadorReserva.java`

"El Observer desacopla quien genera un evento de quien reacciona.
Esta interfaz define los tres eventos posibles: al crear una reserva,
al cancelarla, y al devolver el instrumento."

**Abrir:** `patrones/comportamentales/GestorEventosReserva.java`

"El GestorEventos es el publicador. Tiene una lista de observadores
registrados. Cuando ocurre un evento, notifica a todos
automáticamente con un for."

**Abrir:** `patrones/comportamentales/ObservadorNotificacionSms.java`
**Abrir:** `patrones/comportamentales/ObservadorRegistroLog.java`

"Hay dos observadores registrados:
- `ObservadorNotificacionSms`: envía el SMS al cliente usando
  el Adapter que vimos antes. Aquí se unen dos patrones.
- `ObservadorRegistroLog`: registra la operación en consola
  con fecha y hora.

Si mañana queremos enviar email, creamos `ObservadorEmail` y lo
registramos en el Gestor. El servicio de reservas no se toca."

---

### TEMA 13 — PATRÓN REPOSITORY Y PERSISTENCIA

**Abrir:** `repositorio/InstrumentoRepositorio.java`

"El Repository abstrae la persistencia de datos. La capa de
servicios no sabe ni le importa si los datos están en una base
de datos, en un archivo o en memoria. Solo llama al repositorio.

Con Spring Data JPA, extendiendo `JpaRepository` obtenemos
automáticamente sin escribir SQL:
`save()`, `findById()`, `findAll()`, `deleteById()`.

Y los métodos con nombre especial se generan solos. Spring lee
`findByDisponibilidad` y construye automáticamente:
`SELECT * FROM instrumentos WHERE disponibilidad = ?`
Cero SQL escrito por nosotros."

**Abrir:** `repositorio/ReservaRepositorio.java` — señalar `@Query`

"Cuando el nombre del método sería muy largo, usamos `@Query`
con una consulta JPQL personalizada. JPQL es como SQL pero usando
los nombres de las clases Java en lugar de los nombres de las tablas."

---

### TEMA 14 — CAPA DE SERVICIOS Y ENCAPSULAMIENTO DE REGLAS DE NEGOCIO

**Abrir:** `servicio/ReservaServicio.java` — señalar el método `crearReserva`

"La capa de servicios es el coordinador de la lógica de negocio.
Miren el método `crearReserva`. En una sola operación se unen
cinco patrones trabajando juntos:

1. Busca el cliente e instrumento en el REPOSITORY
2. Usa el BUILDER para construir la Reserva con validaciones
3. Usa el STATE para cambiar la disponibilidad del instrumento
4. Selecciona y aplica la STRATEGY de descuento correcta
5. Notifica a todos los OBSERVERS: SMS y Log
6. Persiste todo en la base de datos

La anotación `@Transactional` garantiza que si cualquier paso falla,
todos los cambios en base de datos se revierten automáticamente.
O todo funciona, o nada se guarda."

---

### TEMA 15 — SPRING BOOT Y ARQUITECTURA REST

**Abrir:** `controlador/ReservaControlador.java`

"El Controlador REST es la puerta de entrada desde el exterior.
No tiene lógica de negocio. Su único trabajo es recibir la petición
HTTP, extraer los datos, llamar al servicio y devolver la respuesta.

La arquitectura REST usa los verbos HTTP con significado semántico:
- GET    → consultar datos, no modifica nada
- POST   → crear un recurso nuevo
- PUT    → modificar un recurso existente
- DELETE → eliminar o cancelar

`@RestController` hace que Spring convierta automáticamente
los objetos Java a JSON. No escribimos ningún serializador."

**Abrir:** `InstrumentosMusicalesApp.java`

"Con esta única clase y `@SpringBootApplication`, Spring Boot:
levanta el servidor web embebido Tomcat, configura la base de datos
H2 en memoria, registra todos los servicios y controladores
automáticamente, y expone todos los endpoints REST.
Todo con una sola línea: `SpringApplication.run(...)`."

---

### TEMA 16 — EVOLUCIÓN INCREMENTAL DEL DISEÑO

"El último tema es la evolución incremental. El sistema fue
diseñado para crecer sin romper lo que ya funciona:

- Para agregar **Violín**: creamos `Violin extends Instrumento`,
  implementamos los dos métodos abstractos, y lo añadimos
  a la Factory. El resto no cambia.

- Para un **nuevo descuento**: creamos una clase que implemente
  `EstrategiaDescuento`. El servicio no se toca.

- Para **notificar por email**: creamos `ObservadorEmail` y lo
  registramos en el Gestor. Una línea de código.

- Para **cambiar a MySQL**: modificamos solo el `application.properties`.
  Todo el código de Repository, Servicio y Controlador sigue igual.

Cada patrón que usamos contribuye a esta extensibilidad.
El sistema está abierto para extensión pero cerrado para modificación."

---

---

# DEMO FINAL EN EL NAVEGADOR
### La conduce el Integrante 3 — los demás complementan si el profesor pregunta

**Abrir:** `http://localhost:8080`

"Profesor, ahora vamos a mostrar el sistema funcionando en tiempo
real. Cada acción que hagamos aquí activa el código que acabamos
de explicar."

**Paso 1 — Dashboard**
"Aquí vemos los contadores en tiempo real. Estos datos vienen de
`GET /api/instrumentos`, `GET /api/clientes` y
`GET /api/reservas/activas`. Las tarjetas muestran los instrumentos
disponibles, cada uno es un objeto concreto de la jerarquía de herencia."

**Paso 2 — Pestaña Instrumentos**
"Aquí registramos un nuevo instrumento. Al hacer clic en Registrar,
el controlador recibe el POST, el servicio llama a la Factory,
y la Factory crea el objeto concreto según el tipo.
Observen que los botones de acción en la tabla cambian según
el estado de cada instrumento. Eso es el patrón State."

*(Registrar una guitarra nueva y mostrar cómo aparece en la tabla)*

**Paso 3 — Pestaña Clientes**
"Registramos un cliente. El setter del objeto Cliente valida
los datos antes de guardarlos. Si intentamos registrar un cliente
con el mismo número de identificación, el servicio lo rechaza."

*(Registrar un cliente nuevo)*

**Paso 4 — Pestaña Reservas**
"Aquí se unen todos los patrones. Seleccionamos un cliente,
un instrumento disponible, las fechas y el tipo de descuento.
Al crear la reserva, en la consola del backend van a ver aparecer
dos líneas automáticamente: el SMS del Observer y el Log de auditoría.
Eso es el patrón Observer en acción."

*(Crear una reserva con descuento VIP y mostrar la consola del backend)*

"Observen también que el instrumento que reservamos ya no aparece
en la lista de disponibles. El patrón State cambió su disponibilidad
a RESERVADO automáticamente."

*(Volver a Instrumentos y mostrar el cambio de estado)*

"Por último, registramos la devolución. El cliente devuelve el
instrumento en estado Rayado. El sistema actualiza el estado físico,
finaliza la reserva, y el Observer notifica la devolución.
El instrumento vuelve a estar Disponible."

*(Registrar devolución desde la tabla de reservas)*

---

---

# CIERRE — Entre todos

"En resumen, desarrollamos un sistema completo que cubre todas
las temáticas del curso:"

```
TEMA                         ARCHIVO PRINCIPAL
─────────────────────────────────────────────────────────────────
Modelado de Dominio          Instrumento.java, Cliente.java, Reserva.java
Encapsulamiento              Instrumento.java, Cliente.java (atributos privados)
Herencia                     GuitarraAcustica/Electrica/Clasica extends Instrumento
Abstracción                  Instrumento.java (clase abstract + métodos abstractos)
Polimorfismo                 calcularPrecioAlquiler() diferente en cada subclase
Singleton                    GestorTienda.java
Factory                      InstrumentoFactory.java
Builder                      ReservaBuilder.java
Adapter                      AdaptadorSms.java + ServicioSmsLegado.java
Decorator                    InstrumentoConSeguro.java + InstrumentoConAfinador.java
Strategy                     EstrategiaDescuento.java + 3 implementaciones
State                        EstadoDisponible/Reservado/Alquilado/EnReparacion.java
Observer                     GestorEventosReserva.java + 2 observadores
Repository y Persistencia    InstrumentoRepositorio / ClienteRepositorio / ReservaRepositorio
Capa de Servicios            InstrumentoServicio / ClienteServicio / ReservaServicio
Spring Boot REST             3 Controladores + application.properties
Evolución Incremental        Diseño abierto a extensión en cada capa
```

"¿Tiene preguntas, profesor?"

---

---

# POSIBLES PREGUNTAS DEL PROFESOR

**P: ¿Por qué usaron H2 y no MySQL?**
R: H2 es una base de datos en memoria, ideal para desarrollo y
demos porque no requiere instalación. Para producción solo
cambiamos el `application.properties` con los datos de MySQL.
El código no cambia porque el Repository abstrae la base de datos.

**P: ¿Cuál es la diferencia entre Herencia y Polimorfismo?**
R: Herencia es el mecanismo por el cual la subclase obtiene los
atributos y métodos del padre. Polimorfismo es el resultado:
el mismo método se comporta diferente según el objeto concreto.
La herencia es lo que habilita el polimorfismo.

**P: ¿Por qué clase abstracta y no interfaz para Instrumento?**
R: Porque Instrumento tiene atributos comunes como marca, modelo
y precio, y lógica compartida como `estaDisponible()`. Las interfaces
en Java no pueden tener estado de instancia. La clase abstracta
es la opción correcta cuando hay datos y comportamiento comunes.

**P: ¿Qué ventaja tiene el Builder sobre un constructor normal?**
R: Con varios parámetros es fácil equivocarse del orden. El Builder
hace el código legible como una oración y valida cada campo
por separado. Si falta algo, el error dice exactamente qué falta.

**P: ¿Cómo funciona la inyección de dependencias en Spring?**
R: Spring crea y administra automáticamente los objetos anotados
con `@Service`, `@Repository` y `@Component`. Cuando un servicio
necesita un repositorio, Spring lo inyecta por el constructor.
Nosotros no usamos `new` para esos objetos.

**P: ¿Qué pasa si intento reservar un instrumento alquilado?**
R: El patrón State lo controla. El objeto `EstadoAlquilado` lanza
una excepción con el mensaje claro. El controlador la captura
y devuelve un HTTP 400 con el error en JSON. En el frontend
aparece la alerta roja con el mensaje.

**P: ¿Para qué sirve @Transactional?**
R: Garantiza que todas las operaciones de base de datos dentro
del método se ejecuten como una unidad atómica. Si falla
cualquier paso, todos los cambios se revierten automáticamente.
O todo se guarda, o nada se guarda.

**P: ¿Qué diferencia hay entre Adapter y Decorator?**
R: El Adapter convierte una interfaz incompatible en otra compatible,
sin agregar funcionalidad nueva. El Decorator sí agrega
funcionalidad nueva al objeto envuelto, sin modificar su clase.
Ambos envuelven un objeto pero con propósitos distintos.
