package com.docencia.colecciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapServiceImpl implements MapService {

    @Override
    public Map<String, Integer> contarFrecuenciaPalabras(List<String> palabras) {
        if (palabras == null) {
            throw new IllegalArgumentException();
        }
        if (palabras.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, Integer> resultado = new HashMap<>();
        for (String string : palabras) {
            resultado.put(string, resultado.getOrDefault(string, 0) + 1);
        }
        return resultado;
    }

    @Override
    public Integer obtenerValorPorClave(Map<String, Integer> mapa, String clave) {
        if (mapa == null || clave == null || mapa.isEmpty() || clave.isBlank()) {
            throw new IllegalArgumentException();
        }
        if (!mapa.containsKey(clave)){
            return 0;
        }
        return mapa.get(clave);

    }

    @Override
    public Map<String, Double> calcularMediaPorCategoria(Map<String, List<Integer>> datos) {
        if (datos == null) {
            throw new IllegalArgumentException();
        }

        Map<String, Double> resultado = new HashMap<>();

        for (Map.Entry<String, List<Integer>> entrada : datos.entrySet()) {
            String categoria = entrada.getKey();
            List<Integer> valores = entrada.getValue();

            if (!valores.isEmpty()) {
                int suma = 0;
                for (Integer valor : valores) {
                    suma += valor;
                }
                double media = suma / valores.size();
                resultado.put(categoria, media);
            }
        }

        return resultado;
    }

    @Override
    public String obtenerClaveConMayorValor(Map<String, Integer> mapa) {
        if (mapa == null || mapa.isEmpty()) {
            throw new IllegalArgumentException();
        }
        String claveMaxima = null;
        Integer valorMaximo = 0;

        for (Map.Entry<String, Integer> entrada : mapa.entrySet()) {
            if (entrada.getValue() > valorMaximo) {
                valorMaximo = entrada.getValue();
                claveMaxima = entrada.getKey();
            }
        }

        return claveMaxima;
    }

    @Override
    public Map<String, Integer> filtrarPorValorMinimo(Map<String, Integer> mapa, Integer minimo) {
        if (mapa == null || mapa.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Map<String, Integer> resultado = new HashMap<>();
        for (Map.Entry<String, Integer> entrada : mapa.entrySet()) {
            if (entrada.getValue() >= minimo) {
                resultado.put(entrada.getKey(), entrada.getValue());
            }
        }

        return resultado;

    }
}