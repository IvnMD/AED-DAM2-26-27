package com.docencia.algoritmos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrdenacionServiceImpl implements OrdenacionService {

    @Override
    public List<Integer> ordenarBurbujaAscendente(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()) {
            throw new IllegalArgumentException();
        }
        List<Integer> resultado = new ArrayList<>(numeros);
        resultado.sort(null); //! Ascendente: [1, 2, 5, 8]
        return resultado;
    }

    @Override
    public List<Integer> ordenarBurbujaDescendente(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()) {
            throw new IllegalArgumentException();
        }
        List<Integer> resultado = new ArrayList<>(numeros);
        resultado.sort(Comparator.reverseOrder()); //! Descendente: [8, 5, 2, 1]
        return resultado;
    }

    @Override
    public List<String> ordenarPalabrasAlfabeticamente(List<String> palabras) {
        if (palabras == null || palabras.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> resultado = new ArrayList<>(palabras);
        resultado.sort(String.CASE_INSENSITIVE_ORDER);
        return resultado;
    }

    @Override
    public List<Integer> invertirLista(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()) {
            throw new IllegalArgumentException();
        }
        List<Integer> resultado = new ArrayList<>(numeros);
        Collections.reverse(resultado);

        return resultado;
    }

    @Override
    public Boolean estaOrdenadaAscendente(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()) {
            throw new IllegalArgumentException();
        }
        List<Integer> resultado = new ArrayList<>(numeros);
        resultado.sort(null);
        for (int i = 0; i < numeros.size() - 1; i++) {
            if (numeros.get(i) > numeros.get(i + 1)) {
                return false;
            }
        }
        return true;
    }
    
}
