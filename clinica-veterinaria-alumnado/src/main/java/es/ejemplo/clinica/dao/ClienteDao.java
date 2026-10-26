package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ClienteDao implements IClienteDao {
 public ClienteDao(String url) { 
//    super(url);
 }

 @Override
 public List<Cliente> listar() {
    List<Cliente> lista = new ArrayList<>();
    String sql = "SELECT id, nombre, email, telefono from  clientes";
    try(Connection cn = ConexionBD.obtenerConexion();
        PreparedStatement ps = cn.prepareStatement(sql)) {
        while(ps.executeQuery().next()) {
            lista.add(new Cliente(ps));
        }
    }catch(Exception e){
        throw new RuntimeException(e);
    }
 }

 @Override
 public Optional<Cliente> buscarPorId(Integer id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'buscarPorId'");
 }

 @Override
 public int insertar(Cliente entidad) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'insertar'");
 }

 @Override
 public int actualizar(Cliente entidad) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
 }

 @Override
 public int eliminar(Integer id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'eliminar'");
 }
 
}
