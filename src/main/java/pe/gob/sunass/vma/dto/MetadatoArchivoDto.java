package pe.gob.sunass.vma.dto;

import pe.gob.sunass.vma.model.TipoArchivo;

import java.util.List;

public class MetadatoArchivoDto {
    private Integer id;
    private List<TipoArchivo> tipoArchivosPermitidos;
    private Long maxSizeInMB;

    public MetadatoArchivoDto(Integer id, List<TipoArchivo> tipoArchivosPermitidos, Long maxSizeInMB) {
        this.id = id;
        this.tipoArchivosPermitidos = tipoArchivosPermitidos;
        this.maxSizeInMB = maxSizeInMB;
    }

    public MetadatoArchivoDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<TipoArchivo> getTipoArchivosPermitidos() {
        return tipoArchivosPermitidos;
    }

    public void setTipoArchivosPermitidos(List<TipoArchivo> tipoArchivosPermitidos) {
        this.tipoArchivosPermitidos = tipoArchivosPermitidos;
    }

    public Long getMaxSizeInMB() {
        return maxSizeInMB;
    }

    public void setMaxSizeInMB(Long maxSizeInMB) {
        this.maxSizeInMB = maxSizeInMB;
    }
}
