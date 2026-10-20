package com.docencia.cadenas;

public class StringServiceImpl implements StringService {

    @Override
    public String normalizarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException();
        }
        texto = texto.replaceAll("\\s+", " ").trim().toLowerCase();

        return texto;

    }

    @Override
    public Boolean esPalindromo(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException();
        }

        String normalizado = texto.replaceAll("\\s+", "").toLowerCase();
        String textoInvertido = new StringBuilder(normalizado).reverse().toString();

        return normalizado.equals(textoInvertido);

        // ! CON UN FOR:
        // for (int i = 0; i < texto.length() / 2; i++) {
        // int j = texto.length() - 1 - i;
        // if (texto.charAt(i) != texto.charAt(j)) {
        // return false;
        // }
        // }
        // return true;
    }

    @Override
    public Integer contarVocales(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException();
        }

        Integer contador = 0;
        String vocales = "aeiouAEIOU";

        for (int i = 0; i < texto.length(); i++) {
            if (vocales.indexOf(texto.charAt(i)) != -1) {
                contador++;
            }
            //! ALTERNATIVA
            // char c = Character.toLowerCase(texto.charAt(i));
            // if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
            // contador++;
            // }
        }
        return contador;

    }

    @Override
    public String extraerIniciales(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new IllegalArgumentException();
        }
        String[] palabras = nombreCompleto.split(" ");
        String resultado = "";
        for (String palabra : palabras) {
            resultado += palabra.charAt(0);
        }
        return resultado.toUpperCase();
    }

    @Override
    public String invertirTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException();
        }
        String textoInvertido = new StringBuilder(texto).reverse().toString();
        return textoInvertido;
    }

    @Override
    public Boolean contieneSoloLetras(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException();
        }

        String numeros = "0123456789";

        for (int i = 0; i < texto.length(); i++) {
            if (numeros.indexOf(texto.charAt(i)) != -1) {
                return false;
            }
        }
        return true;
    }

}