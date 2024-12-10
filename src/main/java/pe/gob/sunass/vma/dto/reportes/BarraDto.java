package pe.gob.sunass.vma.dto.reportes;

public class BarraDto {
	
    private String tipo;
    private Integer cantidadRegistradoPorEmpresa;
    private Integer cantidadTotalRegistradoPorEmpresa;

    public BarraDto(String tipo, Integer cantidadRegistradoPorEmpresa, Integer cantidadTotalRegistradoPorEmpresa) {
        this.tipo = tipo;
        this.cantidadRegistradoPorEmpresa = cantidadRegistradoPorEmpresa;
        this.cantidadTotalRegistradoPorEmpresa = cantidadTotalRegistradoPorEmpresa;
    }

    public BarraDto() {
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getCantidadRegistradoPorEmpresa() {
        return cantidadRegistradoPorEmpresa;
    }

    public void setCantidadRegistradoPorEmpresa(Integer cantidadRegistradoPorEmpresa) {
        this.cantidadRegistradoPorEmpresa = cantidadRegistradoPorEmpresa;
    }

    public Integer getCantidadTotalRegistradoPorEmpresa() {
        return cantidadTotalRegistradoPorEmpresa;
    }

    public void setCantidadTotalRegistradoPorEmpresa(Integer cantidadTotalRegistradoPorEmpresa) {
        this.cantidadTotalRegistradoPorEmpresa = cantidadTotalRegistradoPorEmpresa;
    }
}
