package pe.gob.sunass.vma.dto;

import java.util.List;

public class RegistroVMARequest {
    private Integer idEmpresa;
    private boolean registroValido;
    private List<RespuestaDTO> respuestas;
    private DatosUsuarioRegistradorDto datosUsuarioRegistradorDto;

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

    public boolean isRegistroValido() {
        return registroValido;
    }

    public void setRegistroValido(boolean registroValido) {
        this.registroValido = registroValido;
    }

    public DatosUsuarioRegistradorDto getDatosUsuarioRegistradorDto() {
        return datosUsuarioRegistradorDto;
    }

    public void setDatosUsuarioRegistradorDto(DatosUsuarioRegistradorDto datosUsuarioRegistradorDto) {
        this.datosUsuarioRegistradorDto = datosUsuarioRegistradorDto;
    }
}