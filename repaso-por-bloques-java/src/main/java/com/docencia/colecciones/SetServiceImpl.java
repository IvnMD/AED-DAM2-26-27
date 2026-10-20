package com.docencia.colecciones;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetServiceImpl implements SetService {

    @Override
    public Set<String> obtenerElementosUnicos(List<String> elementos) {
        if (elementos == null || elementos.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Set<String> resultado = new HashSet<>();
        for (String elemento : elementos) {
            resultado.add(elemento);
        }
        return resultado;
    }

    @Override
    public Boolean contieneElemento(Set<String> elementos, String valor) {
        if (elementos == null || valor == null || elementos.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return elementos.contains(valor);
    }

    @Override
    public Set<String> unirConjuntos(Set<String> primero, Set<String> segundo) {
        if (primero == null || primero.isEmpty() || segundo == null || segundo.isEmpty()) {
            throw new IllegalArgumentException();
        }
        // Set<String> resultado = new HashSet<>(); //! CON BUCLE ANIDADO
        // for (String string : primero) {
        // resultado.add(string);
        // for (String string2 : segundo) {
        // resultado.add(string2);
        // }
        // }

        Set<String> resultado = new HashSet<>(primero); // ! Copia primero
        resultado.addAll(segundo); // ! Añade todo segundo

        return resultado;
    }

    @Override
    public Set<String> intersectarConjuntos(Set<String> primero, Set<String> segundo) {
        if (primero == null || primero.isEmpty() || segundo == null || segundo.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Set<String> resultado = new HashSet<>(primero);
        // for (String elemento : primero) {   //! CON BUCLE
        //     if (segundo.contains(elemento)) {
        //         resultado.add(elemento);
        //     }
        // }
        resultado.retainAll(segundo);
        return resultado;
    }

    @Override
    public Set<String> restarConjuntos(Set<String> primero, Set<String> segundo) {
        if (primero == null || primero.isEmpty() || segundo == null || segundo.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Set<String> resultado = new HashSet<>(primero);

        resultado.removeAll(segundo);

        return resultado;
    }

}