package pe.gob.sunass.vma.dto;

import java.util.List;

public class GraficoDto {
    private String titulo;
    private List<BarraDto> datos;

    public GraficoDto(String titulo, List<BarraDto> datos) {
        this.titulo = titulo;
        this.datos = datos;
    }

    public GraficoDto() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<BarraDto> getDatos() {
        return datos;
    }

    public void setDatos(List<BarraDto> datos) {
        this.datos = datos;
    }
}
