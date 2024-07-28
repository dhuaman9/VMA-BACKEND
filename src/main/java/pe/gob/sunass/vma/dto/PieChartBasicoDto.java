package pe.gob.sunass.vma.dto;

public class PieChartBasicoDto {
    private String label;
    private double cantidad;

    public PieChartBasicoDto(String label, double cantidad) {
        this.label = label;
        this.cantidad = cantidad;
    }

    public PieChartBasicoDto() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
}
