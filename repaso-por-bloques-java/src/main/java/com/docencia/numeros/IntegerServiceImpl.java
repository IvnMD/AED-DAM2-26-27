package com.docencia.numeros;

public class IntegerServiceImpl implements IntegerService {

    @Override
    public Boolean esPar(Integer numero) {
        if (numero == null) {
            throw new IllegalArgumentException();
        }
        return !(numero % 2 != 0);
    }

    @Override
    public Integer sumarDigitos(Integer numero) {
        if (numero == null) {
            throw new IllegalArgumentException();
        }
        Integer suma = 0;
        if (numero < 0) {
            numero = numero * (-1);
        }
        while (numero > 0) {
            suma += numero % 10;
            numero /= 10;
        }
        return suma;
    }



    @Override
    public Integer convertirTextoAEntero(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException();
        }
        return Integer.valueOf(texto);
        // Integer.parseInt(texto);
    }

    @Override
    public Boolean esNumeroPrimo(Integer numero) {
        if (numero == null || numero < 2) {
            throw new IllegalArgumentException();
        }
        if (numero <= 1) {
            return false;
        }
        if (numero % 2 == 0) {
            return false;
        }
        if (numero == 2) {
            return true;
        }
        for (int i = 3; i * i <= numero; i += 2) {
            if (numero % i == 0)
                return false;
        }
        return true;
    }

    @Override
    public Integer calcularFactorial(Integer numero) {
        if (numero == null || numero < 0) {
            throw new IllegalArgumentException();
        }
        if (numero == 0){
            return 1;
        }
        Integer factorial = 0;
        for (int i = 0; i <= numero; i++) {
            numero *= numero;
        }
        return factorial;
    }
}