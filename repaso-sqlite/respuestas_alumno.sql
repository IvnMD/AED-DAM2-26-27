-- Completa cada ejercicio con UNA consulta SELECT (o WITH ... SELECT).
-- No borres las líneas: -- EJERCICIO NN
-- Termina cada consulta con punto y coma.

-- EJERCICIO 01
-- Mostrar los cinco primeros productos por identificador.
SELECT id, nombre, precio FROM Productos ORDER BY id ASC LIMIT 5;

-- EJERCICIO 02
-- Ver los cinco vehículos más recientes.
SELECT matricula, marca, modelo, anio FROM Vehiculos ORDER BY anio DESC, matricula ASC LIMIT 5;

-- EJERCICIO 03
-- Filtrar productos cuyo precio sea mayor de 100.
SELECT id, nombre, precio FROM Productos WHERE precio > 100 ORDER BY precio DESC, id ASC LIMIT 8;

-- EJERCICIO 04
-- Localizar vehículos de la marca Toyota.
SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE marca = 'Toyota' ORDER BY anio DESC, matricula ASC LIMIT 8;

-- EJERCICIO 05
-- Buscar productos cuyo nombre contenga Pro.
SELECT id, nombre, precio FROM Productos WHERE nombre LIKE '%Pro%' ORDER BY id ASC LIMIT 8;

-- EJERCICIO 06
-- Mostrar productos con precio comprendido entre 20 y 50.
SELECT id, nombre, precio FROM Productos WHERE precio >= 20 AND precio <= 50 ORDER BY precio ASC, id ASC LIMIT 8;

-- EJERCICIO 07
-- Listar marcas diferentes sin repetir.
SELECT DISTINCT marca FROM Vehiculos ORDER BY marca ASC;

-- EJERCICIO 08
-- Seleccionar vehículos de dos marcas concretas.
SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE marca = 'Ford' ORDER BY anio DESC, matricula ASC LIMIT 8;

-- EJERCICIO 09
-- Contar cuántos productos existen.
SELECT COUNT(*) AS total_productos FROM Productos;

-- EJERCICIO 10
-- Contar cuántos vehículos están registrados.
SELECT COUNT(*) AS total_vehiculos FROM Vehiculos;

-- EJERCICIO 11
-- Obtener precio mínimo, máximo y medio.
SELECT ROUND(MIN(precio), 2) AS minimo, ROUND(MAX(precio), 2) AS maximo, ROUND(AVG(precio), 2) AS media FROM Productos;

-- EJERCICIO 12
-- Contar las marcas diferentes.
SELECT COUNT(DISTINCT marca) AS marcas_diferentes FROM Vehiculos;

-- EJERCICIO 13
-- Calcular la suma de todos los precios del catálogo.
SELECT ROUND(SUM(precio), 2) AS suma_precios FROM Productos;

-- EJERCICIO 14
-- Contar productos con precio de al menos 50.
SELECT COUNT(*) AS productos_caros FROM Productos WHERE precio >= 50;

-- EJERCICIO 15
-- Contar vehículos de cada marca.
SELECT marca, COUNT(*) AS cantidad FROM Vehiculos GROUP BY marca ORDER BY marca ASC;

-- EJERCICIO 16
-- Encontrar los años de fabricación con más vehículos.
SELECT anio, COUNT(*) AS cantidad FROM Vehiculos GROUP BY anio HAVING COUNT(*) = (SELECT MAX(cantidad) FROM (SELECT COUNT(*) AS cantidad FROM Vehiculos GROUP BY anio)) ORDER BY anio DESC LIMIT 10;

-- EJERCICIO 17
-- Calcular año más antiguo y reciente por marca.
SELECT marca, MIN(anio) AS anio_min, MAX(anio) AS anio_max FROM Vehiculos GROUP BY marca ORDER BY marca ASC;

-- EJERCICIO 18
-- Contar productos por tramos de precio.
SELECT CASE WHEN precio < 25 THEN 'Menos de 25' WHEN precio < 75 THEN '25 a menos de 75' ELSE '75 o más' END AS tramo, COUNT(*) AS cantidad FROM Productos GROUP BY tramo ORDER BY CASE WHEN precio < 25 THEN 1 WHEN precio < 75 THEN 2 ELSE 3 END;

-- EJERCICIO 19
-- Filtrar marcas con al menos cinco vehículos.
SELECT marca, COUNT(*) AS cantidad FROM Vehiculos GROUP BY marca HAVING COUNT(*) >= 5 ORDER BY marca ASC;

-- EJERCICIO 20
-- Mostrar años en los que hay al menos tres vehículos.
SELECT anio, COUNT(*) AS cantidad FROM Vehiculos GROUP BY anio HAVING COUNT(*) >= 3 ORDER BY cantidad DESC, anio DESC LIMIT 10;

-- EJERCICIO 21
-- Mostrar el precio medio por familia, usando la primera palabra del nombre.
SELECT SUBSTR(nombre, 1, INSTR(nombre, ' ')-1) as familia, count(*) as cantidad , round(AVG(precio),2) as media FROM Productos group by SUBSTR(nombre, 1, INSTR(nombre, ' ')-1) ORDER BY cantidad desc, familia asc LIMIT 12;

-- EJERCICIO 22
-- Mostrar familias que tengan al menos cuatro productos y precio medio superior a 35.
SELECT SUBSTR(nombre, 1, INSTR(nombre, ' ') - 1) AS familia, COUNT(*) AS productos, ROUND(AVG(precio), 2) AS media FROM Productos GROUP BY familia HAVING COUNT(*) >= 4 AND AVG(precio) > 35 ORDER BY media DESC, familia ASC;
-- EJERCICIO 23
-- Mostrar productos más caros que la media general.
SELECT id, nombre, precio FROM Productos WHERE precio > (SELECT AVG(precio) FROM Productos) ORDER BY precio DESC, id ASC LIMIT 10;

-- EJERCICIO 24
-- Listar los modelos de los vehículos posteriores al año 2020 agrupados por marca.
SELECT marca, COUNT(*) AS vehiculos_recientes FROM Vehiculos WHERE anio > 2020 GROUP BY marca ORDER BY vehiculos_recientes DESC, marca ASC;

-- EJERCICIO 25
-- Detectar los productos empatados con el precio máximo.
SELECT id, nombre, precio FROM Productos WHERE precio = (SELECT MAX(precio) FROM Productos) ORDER BY id ASC;

-- EJERCICIO 26
-- Clasificar productos según el precio usando un ranking.
SELECT id, nombre, precio, RANK() OVER (ORDER BY precio DESC) AS puesto FROM Productos ORDER BY precio DESC, id ASC LIMIT 10;

-- EJERCICIO 27
-- Calcular un total acumulado de precios ordenados por identificador.
SELECT id, nombre, precio, SUM(precio) OVER (ORDER BY id ASC) AS acumulado FROM Productos ORDER BY id ASC LIMIT 10;


-- EJERCICIO 28
-- Comparar cada producto con el precio medio de su familia.
WITH familias AS (SELECT id, nombre, SUBSTR(nombre, 1, INSTR(nombre, ' ') - 1) AS familia, precio, AVG(precio) OVER (PARTITION BY SUBSTR(nombre, 1, INSTR(nombre, ' ') - 1)) AS media_familia FROM Productos) SELECT id, nombre, familia, precio, ROUND(media_familia, 2) AS media_familia FROM familias ORDER BY familia ASC, precio DESC, id ASC LIMIT 12;

-- EJERCICIO 29
-- Mostrar el vehículo más reciente de cada marca, incluyendo empates.
WITH recientes AS (SELECT matricula, marca, modelo, anio, RANK() OVER (PARTITION BY marca ORDER BY anio DESC) AS posicion FROM Vehiculos) SELECT matricula, marca, modelo, anio FROM recientes WHERE posicion = 1 ORDER BY marca ASC;

-- EJERCICIO 30
-- Resumir por marca el total, año medio y antigüedad, conservando solo marcas con al menos tres vehículos.
WITH resumen AS (SELECT marca, COUNT(*) AS total, ROUND(AVG(anio), 1) AS anio_medio, MIN(anio) AS mas_antiguo, MAX(anio) AS mas_reciente FROM Vehiculos GROUP BY marca HAVING COUNT(*) >= 3) SELECT marca, total, anio_medio, mas_antiguo, mas_reciente FROM resumen ORDER BY marca ASC;


