package pe.gob.sunass.vma.dto.reportes;

public class RegistroPromedioTrabajadorVMAChartDto {
    private String tipo;
    private Double promedio;
    private Integer cantidadTrabajadoresDecicados;
    private Integer cantidadEmpresasPorTipo;

    public RegistroPromedioTrabajadorVMAChartDto() {
    }

    public RegistroPromedioTrabajadorVMAChartDto(String tipo, Double promedio, Integer cantidadTrabajadoresDecicados, Integer cantidadEmpresasPorTipo) {
        this.tipo = tipo;
        this.promedio = promedio;
        this.cantidadTrabajadoresDecicados = cantidadTrabajadoresDecicados;
        this.cantidadEmpresasPorTipo = cantidadEmpresasPorTipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getPromedio() {
        return promedio;
    }

    public void setPromedio(Double promedio) {
        this.promedio = promedio;
    }

    public Integer getCantidadTrabajadoresDecicados() {
        return cantidadTrabajadoresDecicados;
    }

    public void setCantidadTrabajadoresDecicados(Integer cantidadTrabajadoresDecicados) {
        this.cantidadTrabajadoresDecicados = cantidadTrabajadoresDecicados;
    }

    public Integer getCantidadEmpresasPorTipo() {
        return cantidadEmpresasPorTipo;
    }

    public void setCantidadEmpresasPorTipo(Integer cantidadEmpresasPorTipo) {
        this.cantidadEmpresasPorTipo = cantidadEmpresasPorTipo;
    }
}
