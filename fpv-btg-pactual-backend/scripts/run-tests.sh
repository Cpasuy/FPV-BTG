#!/bin/bash

# Script para ejecutar todas las pruebas del proyecto

echo "🧪 Ejecutando pruebas del proyecto BTG Pactual Fondos..."

# Verificar que Maven esté instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven no está instalado. Por favor instala Maven primero."
    exit 1
fi

# Ejecutar pruebas unitarias
echo "📋 Ejecutando pruebas unitarias..."
mvn test -Dtest="*Test" -Dspring.profiles.active=test

# Verificar el resultado
if [ $? -eq 0 ]; then
    echo "✅ Pruebas unitarias completadas exitosamente"
else
    echo "❌ Algunas pruebas unitarias fallaron"
    exit 1
fi

# Ejecutar pruebas de integración
echo "🔗 Ejecutando pruebas de integración..."
mvn test -Dtest="*IntegrationTest" -Dspring.profiles.active=test

# Verificar el resultado
if [ $? -eq 0 ]; then
    echo "✅ Pruebas de integración completadas exitosamente"
else
    echo "❌ Algunas pruebas de integración fallaron"
    exit 1
fi

# Generar reporte de cobertura
echo "📊 Generando reporte de cobertura..."
mvn jacoco:report

echo "🎉 Todas las pruebas completadas exitosamente!"
echo "📁 Reporte de cobertura disponible en: target/site/jacoco/index.html"
