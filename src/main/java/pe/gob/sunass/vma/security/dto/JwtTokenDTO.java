package pe.gob.sunass.vma.security.dto;

import javax.validation.constraints.NotEmpty;

public class JwtTokenDTO {
	@NotEmpty
	private String jwt;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
