-- ALUMNADO: escribe aquí las 25 consultas del enunciado, identificadas del 01 al 25.
-- Utiliza schema.sql, los DAO y los tests para comprobar cada consulta.

-- 1. Libros ordenados por título.
SELECT id_libro, titulo, isbn, genero, anio_publicacion, ejemplares, id_autor
FROM libros
ORDER BY titulo;

-- 2. Socios activos ordenados por fecha de alta descendente.
SELECT id_socio, nombre, email, telefono, fecha_alta, activo
FROM socios
WHERE activo = 1
ORDER BY fecha_alta DESC;

-- 3. Libros cuyo título contiene el texto indicado.
-- Sustituye :texto por el texto buscado, por ejemplo '%prueba%'.
SELECT id_libro, titulo, isbn, genero, anio_publicacion, ejemplares, id_autor
FROM libros
WHERE titulo LIKE :texto
ORDER BY titulo;

-- 4. Libros publicados desde un año concreto y de un género determinado.
-- Sustituye :anio y :genero por los valores deseados.
SELECT id_libro, titulo, isbn, genero, anio_publicacion, ejemplares, id_autor
FROM libros
WHERE anio_publicacion >= :anio
  AND genero = :genero
ORDER BY anio_publicacion, titulo;

-- 5. Géneros distintos registrados en el catálogo.
SELECT DISTINCT genero
FROM libros
ORDER BY genero;

-- 6. Libros con nombre y nacionalidad del autor.
SELECT l.id_libro,
       l.titulo,
       l.isbn,
       l.genero,
       l.anio_publicacion,
       l.ejemplares,
       a.nombre AS autor,
       a.nacionalidad
FROM libros l
JOIN autores a ON a.id_autor = l.id_autor
ORDER BY l.titulo;

-- 7. Préstamos con socio, libro y fechas.
SELECT p.id_prestamo,
       s.nombre AS socio,
       l.titulo AS libro,
       p.fecha_prestamo,
       p.fecha_limite,
       p.fecha_devolucion
FROM prestamos p
JOIN socios s ON s.id_socio = p.id_socio
JOIN libros l ON l.id_libro = p.id_libro
ORDER BY p.fecha_prestamo, p.id_prestamo;

-- 8. Préstamos activos de un socio.
-- Sustituye :idSocio por el identificador del socio.
SELECT p.id_prestamo,
       p.id_socio,
       p.id_libro,
       p.fecha_prestamo,
       p.fecha_limite,
       p.fecha_devolucion
FROM prestamos p
WHERE p.id_socio = :idSocio
  AND p.fecha_devolucion IS NULL
ORDER BY p.fecha_prestamo, p.id_prestamo;

-- 9. Socios que nunca han realizado préstamos.
SELECT s.id_socio,
       s.nombre,
       s.email,
       s.telefono,
       s.fecha_alta,
       s.activo
FROM socios s
LEFT JOIN prestamos p ON p.id_socio = s.id_socio
WHERE p.id_prestamo IS NULL
ORDER BY s.nombre;

-- 10. Reservas pendientes con nombre del socio, título y autor.
SELECT r.id_reserva,
       s.nombre AS socio,
       l.titulo AS libro,
       a.nombre AS autor,
       r.fecha_reserva,
       r.estado
FROM reservas r
JOIN socios s ON s.id_socio = r.id_socio
JOIN libros l ON l.id_libro = r.id_libro
JOIN autores a ON a.id_autor = l.id_autor
WHERE r.estado = 'PENDIENTE'
ORDER BY r.fecha_reserva, r.id_reserva;

-- 11. Cantidad de libros por género.
SELECT genero,
       COUNT(*) AS cantidad_libros
FROM libros
GROUP BY genero
ORDER BY cantidad_libros DESC, genero;

-- 12. Cantidad de libros por autor, incluyendo autores sin libros.
SELECT a.id_autor,
       a.nombre,
       a.nacionalidad,
       COUNT(l.id_libro) AS cantidad_libros
FROM autores a
LEFT JOIN libros l ON l.id_autor = a.id_autor
GROUP BY a.id_autor, a.nombre, a.nacionalidad
ORDER BY cantidad_libros DESC, a.nombre;

-- 13. Cinco socios con más préstamos.
SELECT s.id_socio,
       s.nombre,
       COUNT(p.id_prestamo) AS total_prestamos
FROM socios s
LEFT JOIN prestamos p ON p.id_socio = s.id_socio
GROUP BY s.id_socio, s.nombre
ORDER BY total_prestamos DESC, s.nombre
LIMIT 5;

-- 14. Géneros con más de cinco préstamos históricos.
SELECT l.genero,
       COUNT(p.id_prestamo) AS total_prestamos
FROM prestamos p
JOIN libros l ON l.id_libro = p.id_libro
GROUP BY l.genero
HAVING COUNT(p.id_prestamo) > 5
ORDER BY total_prestamos DESC, l.genero;

-- 15. Por libro: ejemplares totales, prestados actualmente y disponibles.
SELECT l.id_libro,
       l.titulo,
       l.ejemplares AS ejemplares_totales,
       COUNT(p.id_prestamo) AS prestados_actualmente,
       l.ejemplares - COUNT(p.id_prestamo) AS disponibles
FROM libros l
LEFT JOIN prestamos p
    ON p.id_libro = l.id_libro
   AND p.fecha_devolucion IS NULL
GROUP BY l.id_libro, l.titulo, l.ejemplares
ORDER BY l.titulo;

-- 16. Libros que nunca se han prestado.
SELECT l.id_libro,
       l.titulo,
       l.isbn,
       l.genero,
       l.anio_publicacion,
       l.ejemplares,
       l.id_autor
FROM libros l
LEFT JOIN prestamos p ON p.id_libro = l.id_libro
WHERE p.id_prestamo IS NULL
ORDER BY l.titulo;

-- 17. Socios con préstamos vencidos en una fecha de referencia.
-- Sustituye :fecha por una fecha en formato YYYY-MM-DD.
SELECT DISTINCT s.id_socio,
       s.nombre,
       s.email,
       s.telefono,
       s.fecha_alta,
       s.activo
FROM socios s
JOIN prestamos p ON p.id_socio = s.id_socio
WHERE p.fecha_devolucion IS NULL
  AND p.fecha_limite < :fecha
ORDER BY s.nombre;

-- 18. Libros con más préstamos históricos que la media del catálogo,
-- incluyendo los libros sin préstamos al calcular la media.
SELECT l.id_libro,
       l.titulo,
       COUNT(p.id_prestamo) AS total_prestamos
FROM libros l
LEFT JOIN prestamos p ON p.id_libro = l.id_libro
GROUP BY l.id_libro, l.titulo
HAVING COUNT(p.id_prestamo) > (
    SELECT AVG(total)
    FROM (
        SELECT COUNT(p2.id_prestamo) AS total
        FROM libros l2
        LEFT JOIN prestamos p2 ON p2.id_libro = l2.id_libro
        GROUP BY l2.id_libro
    )
)
ORDER BY total_prestamos DESC, l.titulo;

-- 19. Autores cuyos libros han sido prestados por al menos tres socios distintos.
SELECT a.id_autor,
       a.nombre,
       a.nacionalidad,
       COUNT(DISTINCT p.id_socio) AS socios_distintos
FROM autores a
JOIN libros l ON l.id_autor = a.id_autor
JOIN prestamos p ON p.id_libro = l.id_libro
GROUP BY a.id_autor, a.nombre, a.nacionalidad
HAVING COUNT(DISTINCT p.id_socio) >= 3
ORDER BY socios_distintos DESC, a.nombre;

-- 20. Libros con reservas pendientes que no tienen ejemplares disponibles.
SELECT DISTINCT l.id_libro,
       l.titulo,
       l.isbn,
       l.genero,
       l.ejemplares,
       COUNT(r.id_reserva) AS reservas_pendientes
FROM libros l
JOIN reservas r ON r.id_libro = l.id_libro
WHERE r.estado = 'PENDIENTE'
  AND l.ejemplares <= (
      SELECT COUNT(*)
      FROM prestamos p
      WHERE p.id_libro = l.id_libro
        AND p.fecha_devolucion IS NULL
  )
GROUP BY l.id_libro, l.titulo, l.isbn, l.genero, l.ejemplares
ORDER BY l.titulo;

-- 21. Ranking de los tres libros más prestados de un mes indicado.
-- Sustituye :anioMes por un valor en formato YYYY-MM, por ejemplo '2025-02'.
SELECT l.id_libro,
       l.titulo,
       a.nombre AS autor,
       COUNT(p.id_prestamo) AS total_prestamos
FROM prestamos p
JOIN libros l ON l.id_libro = p.id_libro
JOIN autores a ON a.id_autor = l.id_autor
WHERE SUBSTR(p.fecha_prestamo, 1, 7) = :anioMes
GROUP BY l.id_libro, l.titulo, a.nombre
ORDER BY total_prestamos DESC, l.titulo
LIMIT 3;

-- 22. Historial completo de préstamos de un socio,
-- indicando devoluciones y retrasos.
-- Sustituye :idSocio por el identificador del socio.
SELECT p.id_prestamo,
       l.titulo AS libro,
       p.fecha_prestamo,
       p.fecha_limite,
       p.fecha_devolucion,
       CASE
           WHEN p.fecha_devolucion IS NULL AND p.fecha_limite < DATE('now')
               THEN 'VENCIDO'
           WHEN p.fecha_devolucion IS NOT NULL AND p.fecha_devolucion > p.fecha_limite
               THEN 'DEVUELTO CON RETRASO'
           WHEN p.fecha_devolucion IS NOT NULL
               THEN 'DEVUELTO'
           ELSE 'ACTIVO'
       END AS estado
FROM prestamos p
JOIN libros l ON l.id_libro = p.id_libro
WHERE p.id_socio = :idSocio
ORDER BY p.fecha_prestamo, p.id_prestamo;

-- 23. Reserva pendiente más antigua de cada libro.
SELECT r.id_reserva,
       r.id_libro,
       l.titulo,
       r.id_socio,
       s.nombre AS socio,
       r.fecha_reserva,
       r.estado
FROM reservas r
JOIN libros l ON l.id_libro = r.id_libro
JOIN socios s ON s.id_socio = r.id_socio
WHERE r.estado = 'PENDIENTE'
  AND r.fecha_reserva = (
      SELECT MIN(r2.fecha_reserva)
      FROM reservas r2
      WHERE r2.id_libro = r.id_libro
        AND r2.estado = 'PENDIENTE'
  )
ORDER BY r.id_libro, r.fecha_reserva, r.id_reserva;

-- 24. Estadísticas por género: títulos, ejemplares,
-- préstamos históricos y préstamos activos.
SELECT l.genero,
       COUNT(DISTINCT l.id_libro) AS total_titulos,
       SUM(l.ejemplares) AS total_ejemplares,
       COUNT(p.id_prestamo) AS prestamos_historicos,
       COUNT(CASE WHEN p.fecha_devolucion IS NULL THEN 1 END) AS prestamos_activos
FROM libros l
LEFT JOIN prestamos p ON p.id_libro = l.id_libro
GROUP BY l.genero
ORDER BY l.genero;

-- 25. Registro atómico de un préstamo solo si el socio está activo
-- y queda un ejemplar disponible.
--
-- Pasos:
-- 1. Comprobar que el socio existe y está activo.
-- 2. Comprobar que el libro existe.
-- 3. Comprobar que el número de préstamos activos es menor que los ejemplares.
-- 4. Insertar el préstamo solo si se cumplen las tres condiciones.
-- 5. La condición se evalúa en la misma instrucción INSERT para evitar
--    que dos operaciones simultáneas superen el límite de ejemplares.
--
-- Sustituye :idSocio, :idLibro, :inicio y :limite por los valores deseados.
INSERT INTO prestamos
    (id_socio, id_libro, fecha_prestamo, fecha_limite, fecha_devolucion)
SELECT :idSocio, :idLibro, :inicio, :limite, NULL
WHERE EXISTS (
    SELECT 1
    FROM socios
    WHERE id_socio = :idSocio
      AND activo = 1
)
AND EXISTS (
    SELECT 1
    FROM libros
    WHERE id_libro = :idLibro
)
AND (
    SELECT ejemplares
    FROM libros
    WHERE id_libro = :idLibro
) > (
    SELECT COUNT(*)
    FROM prestamos
    WHERE id_libro = :idLibro
      AND fecha_devolucion IS NULL
);