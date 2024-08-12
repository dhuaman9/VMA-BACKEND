package pe.gob.sunass.vma.dto;

import java.math.BigDecimal;

public class CostoAnualIncurridoDTO {
    private String tipoEmpresa;
    private Integer totalEpEvaludadas;
    private Integer epRemitieronInformacion;
    private BigDecimal costoTotalReportado;
    private BigDecimal costoPromedioIncurridoPorEp;

    public CostoAnualIncurridoDTO() {
    }

    public CostoAnualIncurridoDTO(String tipoEmpresa, Integer totalEpEvaludadas, Integer epRemitieronInformacion, BigDecimal costoTotalReportado, BigDecimal costoPromedioIncurridoPorEp) {
        this.tipoEmpresa = tipoEmpresa;
        this.totalEpEvaludadas = totalEpEvaludadas;
        this.epRemitieronInformacion = epRemitieronInformacion;
        this.costoTotalReportado = costoTotalReportado;
        this.costoPromedioIncurridoPorEp = costoPromedioIncurridoPorEp;
    }

    public String getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(String tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public Integer getTotalEpEvaludadas() {
        return totalEpEvaludadas;
    }

    public void setTotalEpEvaludadas(Integer totalEpEvaludadas) {
        this.totalEpEvaludadas = totalEpEvaludadas;
    }

    public Integer getEpRemitieronInformacion() {
        return epRemitieronInformacion;
    }

    public void setEpRemitieronInformacion(Integer epRemitieronInformacion) {
        this.epRemitieronInformacion = epRemitieronInformacion;
    }

    public BigDecimal getCostoTotalReportado() {
        return costoTotalReportado;
    }

    public void setCostoTotalReportado(BigDecimal costoTotalReportado) {
        this.costoTotalReportado = costoTotalReportado;
    }

    public BigDecimal getCostoPromedioIncurridoPorEp() {
        return costoPromedioIncurridoPorEp;
    }

    public void setCostoPromedioIncurridoPorEp(BigDecimal costoPromedioIncurridoPorEp) {
        this.costoPromedioIncurridoPorEp = costoPromedioIncurridoPorEp;
    }
}