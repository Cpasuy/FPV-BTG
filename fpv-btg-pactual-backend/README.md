# Sistema de Gestión de Fondos BTG Pactual

## Descripción
Sistema web que permite a los clientes de BTG Pactual gestionar sus suscripciones a Fondos Voluntarios de Pensión (FPV) y Fondos de Inversión Colectiva (FIC).

## Funcionalidades
- ✅ Suscribirse a un nuevo fondo
- ✅ Cancelar suscripción a un fondo
- ✅ Ver historial de transacciones
- ✅ Notificaciones por email o SMS
- ✅ Validación de saldo y montos mínimos

## Tecnologías Utilizadas
- **Backend**: Java 17, Spring Boot 3.5.6
- **Base de Datos**: MongoDB
- **Documentación**: Swagger/OpenAPI
- **Notificaciones**: Spring Mail
- **Testing**: JUnit 5, Mockito

## Requisitos Previos
- Java 17 o superior
- Maven 3.6+
- MongoDB 4.4+
- Gmail account (para notificaciones por email)

## Instalación y Ejecución

### 1. Clonar el repositorio
```bash
git clone <url-del-repositorio>
cd FPV_BTG_PACTUAL
```

### 2. Configurar MongoDB
Asegúrate de que MongoDB esté ejecutándose en tu sistema:
```bash
# Windows
net start MongoDB

# Linux/Mac
sudo systemctl start mongod
```

### 3. Configurar variables de entorno (Opcional)
Crea un archivo `.env` o configura las siguientes variables:
```bash
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-contraseña-de-aplicacion
```

### 4. Compilar y ejecutar
```bash
# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run
```

### 5. Verificar la aplicación
- La aplicación estará disponible en: `http://localhost:8080`
- Documentación de la API: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/api-docs`

## Estructura del Proyecto
```
src/
├── main/
│   ├── java/com/btgpactual/fondos/gestionfondosclientes/
│   │   ├── controller/          # Controladores REST
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── exception/           # Manejo de excepciones
│   │   ├── model/               # Entidades del modelo
│   │   ├── repository/          # Repositorios de datos
│   │   └── service/             # Lógica de negocio
│   └── resources/
│       └── application.properties
└── test/                        # Pruebas unitarias
```

## API Endpoints

### Consultas
- `GET /api/btg/fondos` - Obtener lista de fondos disponibles
- `GET /api/btg/fondos/historial` - Obtener historial de transacciones

### Gestión de Suscripciones
- `POST /api/btg/fondos/suscribirse` - Suscribirse a un fondo
- `POST /api/btg/fondos/cancelar` - Cancelar suscripción a un fondo

## Ejemplos de Uso

### Obtener fondos disponibles
```bash
curl -X GET http://localhost:8080/api/btg/fondos
```

### Suscribirse a un fondo
```bash
curl -X POST http://localhost:8080/api/btg/fondos/suscribirse \
  -H "Content-Type: application/json" \
  -d '{
    "idFondo": "1",
    "monto": 100000,
    "preferenciaNotificacion": "EMAIL",
    "contacto": "cliente@example.com"
  }'
```

### Cancelar suscripción
```bash
curl -X POST http://localhost:8080/api/btg/fondos/cancelar \
  -H "Content-Type: application/json" \
  -d '{
    "idFondo": "1"
  }'
```

### Ver historial de transacciones
```bash
curl -X GET http://localhost:8080/api/btg/fondos/historial
```
