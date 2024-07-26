package pe.gob.sunass.vma.dto;

import java.util.List;

public class GraficoDto {
    private String titulo;
    private List<RegistroEmpresaChartDto> datos;

    public GraficoDto(String titulo, List<RegistroEmpresaChartDto> datos) {
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

    public List<RegistroEmpresaChartDto> getDatos() {
        return datos;
    }

    public void setDatos(List<RegistroEmpresaChartDto> datos) {
        this.datos = datos;
    }
}
