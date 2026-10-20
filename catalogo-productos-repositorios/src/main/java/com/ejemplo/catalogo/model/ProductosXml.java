package com.ejemplo.catalogo.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;


@JacksonXmlRootElement(localName = "productos")
public class ProductosXml {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "producto")
    private List productos;

    public ProductosXml() {
        productos = new ArrayList<>();
    }

    public void setProductos(List productos) {
        this.productos = productos;
    }

    public List<Producto> getProductos() {
        return productos;
    }



}