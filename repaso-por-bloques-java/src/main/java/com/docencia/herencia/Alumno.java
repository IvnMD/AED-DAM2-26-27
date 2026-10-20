package com.docencia.herencia;

public class Alumno extends Persona {
    private final String expediente;

    public Alumno(String nombre, String apellidos, Integer edad, String expediente) {
        super(nombre, apellidos, edad);
        this.expediente = expediente;
    }

    public String getExpediente() { return expediente; }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((expediente == null) ? 0 : expediente.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Alumno other = (Alumno) obj;
        if (expediente == null) {
            if (other.expediente != null)
                return false;
        } else if (!expediente.equals(other.expediente))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ALUMNO: " +  getNombre() + " " + getApellidos() + " " + "(" + getEdad() + ")";
    }

}