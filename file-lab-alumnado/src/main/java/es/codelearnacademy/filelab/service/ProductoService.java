package es.codelearnacademy.filelab.service;

import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.IProductoRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class ProductoService {

    private final IProductoRepository repository;

    public ProductoService(IProductoRepository repository) {
        this.repository = repository;
    }

    public Optional<Producto> maximoPrecio() {
       List<Producto> productos = repository.findAll();
       if (productos.isEmpty()) {
           return Optional.empty();
       }

            return productos.stream()
                    .max(Comparator.comparingDouble(Producto::precio));


//        List<Producto> lista = repository.findAll();
//
//        if (lista.isEmpty()) {
//            return Optional.empty();
//        }
//
//        Producto mejor = lista.get(0);
//
//        for (int i = 1; i < lista.size(); i++) {
//            Producto actual = lista.get(i);
//            if (actual.precio() > mejor.precio()) {
//                mejor = actual;
//            }
//        }
//
//        return Optional.of(mejor);
//    }
    }

    public Optional<Producto> minimoPrecio() {
        List<Producto> productos = repository.findAll();
        if (productos.isEmpty()) {
            return Optional.empty();
        }
        return productos.stream().min(Comparator.comparingDouble(Producto::precio));

    }

    public Optional<Producto> maximoStock() {

        List<Producto> productos = repository.findAll();
        if (productos.isEmpty()) {
            return Optional.empty();
        }
        return productos.stream().max(Comparator.comparingDouble(Producto::stock));
    }

    public Optional<Producto> minimoStock() {
        List<Producto> productos = repository.findAll();
        if (productos.isEmpty()) {
            return Optional.empty();
        }
        return productos.stream().min(Comparator.comparingDouble(Producto::stock));
    }

    public int stockTotal() {

        List<Producto> productos = repository.findAll();
        if (productos.isEmpty()) {
            return 0;
        }
        return productos.stream().mapToInt(Producto::stock).sum();
    }

    public double valorInventario() {

        List<Producto> productos = repository.findAll();
        if (productos.isEmpty()) {
            return 0;
        }
        return productos.stream()
                .mapToDouble(Producto -> Producto.precio() * Producto.stock())
                .sum();

/*        double total = 0.0;
        for (Producto p : productos) {
            total += p.precio() * p.stock();
        }
        return total;*/
    }

    public List<Producto> sinStock() {

        List<Producto> productos = repository.findAll();
        if (productos.isEmpty()) {
            return List.of();
        }
        return productos.stream().filter(Producto -> Producto.stock() == 0).toList();
    }

    public List<Producto> buscar(String texto) {
        List<Producto> productos = repository.findAll();
        if (productos.isEmpty()) {
            return Collections.emptyList();
        }
        return productos.stream()
                .filter(Producto -> Producto.nombre().toLowerCase().contains(texto.toLowerCase()))
                .toList();

    }
}
