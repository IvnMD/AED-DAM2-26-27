# Evidencia de persistencia en SQLite

## Procedimiento realizado

1. Arranqué la aplicación con el perfil `jdbc`.
2. Creé la película `PEL003`, titulada `Prueba de persistencia`,
   con año 2020 y duración de 100 minutos.
3. Comprobé que la creación devolvía HTTP 201.
4. Detuve completamente la aplicación con Ctrl + C.
5. Volví a arrancarla utilizando el mismo fichero
   `data/peliculas.sqlite3`.
6. Consulté `PEL003` después del reinicio.
7. Comprobé que devolvía HTTP 200 con los mismos datos.
8. Eliminé la película de prueba y comprobé la respuesta HTTP 204.
9. Consulté el catálogo final y comprobé que seguían existiendo
   `PEL001` y `PEL002`.

## Archivos de evidencia

- `01-creacion.txt`: respuesta de creación.
- `02-arranque-tras-reinicio.txt`: registro del nuevo arranque.
- `03-consulta-tras-reinicio.txt`: respuesta de consulta tras reiniciar.
- `04-limpieza.txt`: respuesta de borrado de la película de prueba.
- `05-catalogo-final.txt`: catálogo después de la limpieza.

## Resultado

La película creada se conservó después de detener y volver a arrancar
la aplicación, utilizando el mismo fichero SQLite.