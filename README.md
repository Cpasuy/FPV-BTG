# FPV BTG Pactual - Sistema de Gestión de Fondos

Sistema web completo para la gestión de Fondos Voluntarios de Pensión (FPV) y Fondos de Inversión Colectiva (FIC) de BTG Pactual.

## 🏗️ Arquitectura del Sistema

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   Angular 20    │◄──►│   Spring Boot   │◄──►│   MongoDB       │
│   Puerto: 4200  │    │   Puerto: 8080  │    │   Puerto: 27017 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 📁 Estructura del Proyecto

```
FPV-BTG/
├── fpv-btg-pactual-backend/     # Backend (Spring Boot + MongoDB)
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── fpv-btg-pactual/             # Frontend (Angular 20)
│   ├── src/
│   ├── package.json
│   └── README.md
└── README.md                    # Este archivo
```

## 🚀 Inicio Rápido

### Opción 1: Ejecutar por Separado (Recomendado para desarrollo)

#### 1. Backend (Spring Boot)
```bash
# Navegar al directorio del backend
cd fpv-btg-pactual-backend

# Instalar dependencias y ejecutar
mvn clean install
mvn spring-boot:run
```
**El backend estará disponible en:** http://localhost:8080

#### 2. Frontend (Angular)
```bash
# En una nueva terminal, navegar al directorio del frontend
cd fpv-btg-pactual

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm start
```
**El frontend estará disponible en:** http://localhost:4200


## 📋 Prerrequisitos

### Para ejecutar por separado:
- **Java 11** (para el backend)
- **Maven 3.6+** (para el backend)
- **Node.js 20+** (para el frontend)
- **MongoDB 4.4+** (base de datos)


## 🔧 Configuración

### 1. Base de Datos (MongoDB)
Asegúrate de que MongoDB esté ejecutándose:

```bash
# Windows
net start MongoDB

# Linux/Mac
sudo systemctl start mongod
```

```

## 🌐 URLs de Acceso

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **Frontend** | http://localhost:4200 | Interfaz de usuario |
| **Backend API** | http://localhost:8080 | API REST |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Documentación de la API |
| **MongoDB** | localhost:27017 | Base de datos |

## 📚 API Endpoints

### Consultas
- `GET /api/btg/fondos` - Obtener lista de fondos disponibles
- `GET /api/btg/fondos/historial` - Obtener historial de transacciones

### Gestión
- `POST /api/btg/fondos/suscribirse` - Suscribirse a un fondo
- `POST /api/btg/fondos/cancelar` - Cancelar suscripción a un fondo

## 🎯 Funcionalidades

- ✅ **Suscribirse a fondos** - Los clientes pueden suscribirse a FPV y FIC
- ✅ **Cancelar suscripciones** - Cancelar suscripciones activas
- ✅ **Ver historial** - Consultar todas las transacciones
- ✅ **Notificaciones** - Email automático al suscribirse/cancelar
- ✅ **Validaciones** - Montos mínimos y saldos disponibles


## 📖 Documentación Adicional

- [Backend README](fpv-btg-pactual-backend/README.md) - Documentación detallada del backend
- [Frontend README](fpv-btg-pactual/README.md) - Documentación del frontend
- [API Documentation](http://localhost:8080/swagger-ui.html) - Swagger UI (cuando el backend esté ejecutándose)
