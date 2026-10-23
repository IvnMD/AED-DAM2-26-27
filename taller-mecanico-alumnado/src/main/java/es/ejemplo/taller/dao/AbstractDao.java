//package es.ejemplo.taller.dao;
//
//import es.ejemplo.taller.infra.ConexionBD;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//public abstract class AbstractDao<T> extends ConexionBD {
//
//    protected AbstractDao(String url) { super(url); }
//
//    protected abstract Optional<T> mapToClass(ResultSet rs);
//
//    protected List<T> buscar(PreparedStatement ps){
//        if (ps == null){
//            return List.of();
//        }
//        try (ResultSet rs = ps.executeQuery()){
//
//        }
//    }
//}
