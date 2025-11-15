# Spring Batch Demo — Integración entre MySQL y PostgreSQL
## Descripción
Este proyecto es una demo educativa que busca demostrar las capacidades de Spring Batch para el procesamiento de grandes volúmenes de datos de forma controlada, robusta y trazable.
El objetivo es mostrar cómo Spring Batch permite leer datos desde una base de datos, procesarlos con lógica personalizada, manejar errores de forma controlada y escribir los resultados en distintos destinos (incluso en bases de datos diferentes).

## Problema a Resolver
En muchos sistemas empresariales, se requiere procesar grandes conjuntos de datos provenientes de una base de datos principal.
Durante ese procesamiento pueden ocurrir fallos o inconsistencias (por ejemplo, datos inválidos, formatos incorrectos o reglas de negocio incumplidas).
La necesidad es poder:
- Leer datos desde una fuente (MySQL).
- Procesarlos de forma controlada y transaccional, aplicando reglas de negocio simuladas.
- Registrar los fallos para su posterior análisis y reintento.
- Enviar los registros válidos a un destino diferente (PostgreSQL), representando un sistema remoto o externo.
- Asegurar trazabilidad, de modo que se pueda identificar qué registros se procesaron correctamente, cuáles fallaron y por qué.

El objetivo principal es demostrar las capacidades de Spring Batch para manejar escenarios ETL (Extract, Transform, Load) con control de errores, persistencia de estado y soporte transaccional.

## Architectura del Proyecto

- Spring Boot + Spring Batch: Framework principal para la configuración del job, steps y control de transacciones.
- Spring Data JDBC: Utilizado para la interacción con las bases de datos sin necesidad de un ORM pesado como JPA.
- MySQL (Base de Datos Fuente): Contiene los registros originales que deben ser procesados.
- PostgreSQL (Base de Datos Destino): Recibe los registros que fueron procesados exitosamente.
- Spring Batch Metadata Tables: Almacenadas en MySQL, para controlar la ejecución de jobs, steps, retries, y estados.

## Flujo del proceso
- ### Lectura
Spring Batch lee los registros desde MySQL usando un JdbcCursorItemReader o similar.

- ### Procesamiento (Simulación)

Se aplican reglas básicas de negocio (por ejemplo, validaciones de campos).
Algunos registros fallan intencionalmente para demostrar el manejo de errores.

- ### Escritura

Los registros válidos se insertan en la base PostgreSQL.
Los registros fallidos se registran en una tabla especial dentro de MySQL (por ejemplo, failed_records).

- ### Reintentos / Control de Errores

Los errores se capturan con SkipListener o RetryPolicy.
Se muestra cómo Spring Batch continúa el procesamiento sin detener el job completo ante errores parciales.