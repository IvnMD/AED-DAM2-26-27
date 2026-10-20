package es.codelearnacademy.filelab.validation;

import es.codelearnacademy.filelab.model.Producto;

public final class ProductoValidator {

    private ProductoValidator() {
    }

    public static void validar(Producto producto) {
            if (producto == null){
                throw new IllegalArgumentException();
            }
            if (producto.id() <=0){
                throw new IllegalArgumentException();
            }
            if (producto.nombre()==null || producto.nombre().isBlank()){
                throw new IllegalArgumentException();
            }
            if (producto.precio()<=0){
                throw new IllegalArgumentException();
            }
            if (producto.stock()<0){
                throw new IllegalArgumentException();
            }
    }
}
