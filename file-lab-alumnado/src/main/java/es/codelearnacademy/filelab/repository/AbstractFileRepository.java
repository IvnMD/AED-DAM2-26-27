package es.codelearnacademy.filelab.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractFileRepository<T, ID> implements IRepository<T, ID> {

    @Override
    public List<T> findAll() {
        try {
            return readAll();
        } catch (IOException e) {
            return List.of();
        }

    }

    @Override
    public Optional<T> findById(ID id) {
        try {
            List<T> lista = readAll();
            for (T e : lista) {
                if (id.equals(getId(e))) {
                    return Optional.of(e);
                }
            }
        } catch (IOException e) {
            return Optional.empty();
        }

        return Optional.empty();
    }

    @Override
    public boolean create(T entity) {
        try {
            List<T> lista = readAll();

            for (T e : lista) {
                if (getId(e).equals(getId(entity))) {
                    return false;
                }
            }

            lista.add(entity);
            writeAll(lista);
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean update(T entity) {
        try {
            List<T> lista = readAll();
            // Usamos un bucle clásico para obtener el índice numérico
            for (int i = 0; i < lista.size(); i++) {
                if (getId(lista.get(i)).equals(getId(entity))) {
                    lista.set(i, entity); // Reemplaza usando el índice 'i'
                    writeAll(lista);      // Guarda los cambios en el archivo
                    return true;          // Actualización exitosa
                }
            }
        } catch (IOException e) {
            return false; // Retorna false si ocurre un error de lectura/escritura
        }
        return false; // Retorna false si no se encontró el ID en la lista

    }

    @Override
    public boolean delete(ID id) {
        try {
            // 1. Leemos el estado actual del fichero
            List<T> lista = readAll();

            // 2. Eliminamos de la lista usando el ID
            // removeIf devuelve true si encontró y borró el elemento
            boolean eliminado = lista.removeIf(elemento -> getId(elemento).equals(id));

            // 3. ¡Crucial! Si se eliminó algo, reescribimos el fichero para persistir el cambio
            if (eliminado) {
                writeAll(lista);
                return true; // Borrado exitoso y guardado
            }

            return false; // El ID no existía en el fichero

        } catch (IOException e) {
            // Devolvemos false si falla la operación con el archivo
            return false;
        }
    }

    protected abstract ID getId(T entity);

    protected abstract List<T> readAll() throws IOException;

    protected abstract void writeAll(List<T> entities) throws IOException;
}
