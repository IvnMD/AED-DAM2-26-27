package com.docencia.algoritmos;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class BusquedaServiceImpl implements BusquedaService {

    @Override
    public Integer buscarIndiceElemento(List<Integer> numeros, Integer valor) {
        if (valor == null || numeros == null){
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < numeros.size(); i++) {
            if (valor.equals(numeros.get(i))){
                return i;
            }
        }
        return -1;
    }

    @Override
    public Boolean existePalabra(List<String> palabras, String palabra) {
        if (palabras == null || palabra == null || palabra.isBlank()){
            throw new IllegalArgumentException();
        }
        palabra = palabra.toLowerCase();
        for (int i = 0; i < palabras.size(); i++) {

            if (palabra.equals(palabras.get(i).toLowerCase())){
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer encontrarMaximo(List<Integer> numeros) {
       if (numeros == null || numeros.isEmpty()){
           throw new IllegalArgumentException();
       }
       Integer maximo = numeros.get(0);
       for (int i = 0; i < numeros.size(); i++) {
           if (numeros.get(i) > maximo){
               maximo = numeros.get(i);
           }
       }
       return maximo;
    }

    @Override
    public Integer encontrarMinimo(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()){
            throw new IllegalArgumentException();
        }
        Integer minimo = numeros.get(0);
        for (int i = 0; i < numeros.size(); i++) {
            if (numeros.get(i) < minimo){
                minimo = numeros.get(i);
            }
        }
        return minimo;
    }

    @Override
    public Integer contarApariciones(List<Integer> numeros, Integer valor) {
        if (valor == null || numeros == null || numeros.isEmpty()){
            throw new IllegalArgumentException();
        }
        Integer contador = 0;
        for (int i = 0; i < numeros.size(); i++) {
            if (valor.equals(numeros.get(i))){
                contador++;
            }
        }
        return contador;
    }

}
