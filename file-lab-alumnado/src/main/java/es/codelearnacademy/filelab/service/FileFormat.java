package es.codelearnacademy.filelab.service;

public enum FileFormat {
    CSV,
    JSON,
    XML;

    public static FileFormat from(String value) {

                if (value == null) {
                    throw new IllegalArgumentException("El formato no puede ser nulo");
                }
                try {
                    return FileFormat.valueOf(value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Formato desconocido: " + value);
                }
            }
        }



