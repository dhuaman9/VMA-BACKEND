package pe.gob.sunass.vma.dto.reportes;

public class BarChartBasicoDto {
	
    private String label;
    private Double value;

    public BarChartBasicoDto(String label, Double value) {
        this.label = label;
        this.value = value;
    }

    public BarChartBasicoDto() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
