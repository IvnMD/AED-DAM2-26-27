package com.docencia.condicionales;

public class SwitchServiceImpl implements SwitchService {

    @Override
    public String obtenerNombreDia(Integer numeroDia) {
        if (numeroDia == null || numeroDia < 1 || numeroDia > 7) {
            throw new IllegalArgumentException();
        }
        switch (numeroDia) {
            case 1:
                return "LUNES";
            case 2:
                return "MARTES";
            case 3:
                return "MIERCOLES";
            case 4:
                return "JUEVES";
            case 5:
                return "VIERNES";
            case 6:
                return "SABADO";
            case 7:
                return "DOMINGO";
            default:
                throw new IllegalArgumentException("Numero de dia invalido: " + numeroDia);
        }
        // ! CON STRING
        // String[] dias = {"LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES",
        // "SÁBADO","DOMINGO"};
        // return dias[numeroDia-1];
    }

    @Override
    public Double calcularDescuentoPorTipo(String tipoCliente, Double importe) {
        if (tipoCliente == null || tipoCliente.isBlank() ||
                importe == null || importe < 0) {
            throw new IllegalArgumentException();
        }
        switch (tipoCliente) {
            case "VIP":
                return 20.00;
            case "NORMAL":
                return 0.00;
            default:
                throw new IllegalArgumentException("Tipo cliente invalido");
        }

    }

    @Override
    public String obtenerMensajeEstado(String estado) {
        return switch (estado.toUpperCase()) {
            case "ACTIVO" -> "Elemento activo";
            default -> "Elemento inactivo";
        };
    }

    @Override
    public Integer obtenerDiasDelMes(Integer mes) {
        if (mes == null || mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mes invalido: " + mes);
        }

        return switch (mes) {
            case 2 -> 28;
            case 4, 6, 9, 11 -> 30;
            default -> 31;
        };

    }

    @Override
    public String obtenerCategoriaProducto(String codigo) {
        if (codigo == null) {
            throw new IllegalArgumentException();
        }

        return switch (codigo.toUpperCase()) {
            case "T001" -> "TECNOLOGIA";
            default -> "Desconocida";
        };
    }

}