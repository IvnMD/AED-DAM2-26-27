package es.ejemplo.taller.orm.mapper;

import es.ejemplo.taller.modelo.Cliente;
import es.ejemplo.taller.modelo.Vehiculo;
import es.ejemplo.taller.orm.entity.ClienteEntity;
import es.ejemplo.taller.orm.entity.VehiculoEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class VehiculoMapper {

    public static VehiculoEntity toEntity(Vehiculo vehiculo) {
        return new VehiculoEntity(vehiculo.getMatricula(), vehiculo.getMarca(), vehiculo.getModelo(),
                vehiculo.getAnio(), vehiculo.getIdCliente());
    }

    public static Vehiculo toDomain(VehiculoEntity entidad) {
        return new Vehiculo(entidad.getMatricula(), entidad.getMarca(), entidad.getModelo(),
                entidad.getAnio(), entidad.getIdCliente());
    }

}
