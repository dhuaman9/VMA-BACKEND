package pe.gob.sunass.vma.dto;

import java.util.List;

public class CostoAnualIncurridoCompletoDTO {
    private List<BarChartBasicoDto> barChartData;
    private List<CostoAnualIncurridoDTO> costoAnualIncurridoList;

    public CostoAnualIncurridoCompletoDTO() {
    }

    public CostoAnualIncurridoCompletoDTO(List<BarChartBasicoDto> barChartData, List<CostoAnualIncurridoDTO> costoAnualIncurridoList) {
        this.barChartData = barChartData;
        this.costoAnualIncurridoList = costoAnualIncurridoList;
    }

    public List<BarChartBasicoDto> getBarChartData() {
        return barChartData;
    }

    public void setBarChartData(List<BarChartBasicoDto> barChartData) {
        this.barChartData = barChartData;
    }

    public List<CostoAnualIncurridoDTO> getCostoAnualIncurridoList() {
        return costoAnualIncurridoList;
    }

    public void setCostoAnualIncurridoList(List<CostoAnualIncurridoDTO> costoAnualIncurridoList) {
        this.costoAnualIncurridoList = costoAnualIncurridoList;
    }
}