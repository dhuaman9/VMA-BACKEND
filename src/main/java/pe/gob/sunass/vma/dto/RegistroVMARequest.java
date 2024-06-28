package pe.gob.sunass.vma.dto;

import java.util.List;

public class RegistroVMARequest {
    private Integer idEmpresa;
    private List<RespuestaDTO> respuestas;

    public RegistroVMARequest(Integer idEmpresa, List<RespuestaDTO> respuestas) {
        this.idEmpresa = idEmpresa;
        this.respuestas = respuestas;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public List<RespuestaDTO> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<RespuestaDTO> respuestas) {
        this.respuestas = respuestas;
    }
    
    
}
