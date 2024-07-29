package pe.gob.sunass.vma.dto;

import java.util.List;

public class GraficoDto {
    private String label;
    private List<Double> datos;

    public GraficoDto(String label, List<Double> datos) {
        this.label = label;
        this.datos = datos;
    }

    public GraficoDto() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Double> getDatos() {
        return datos;
    }

    public void setDatos(List<Double> datos) {
        this.datos = datos;
    }
}
