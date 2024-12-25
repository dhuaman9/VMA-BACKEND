package pe.gob.sunass.vma.model;



public class UsuarioLdap {

	private String cuenta;
    //private String nombre;
    private String usuario;
    private String unidadOrganica;
    private String nombres;
	private String apellidos;
//	private String userName;
	private String correo;
	
    
    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

	public String getUnidadOrganica() {
		return unidadOrganica;
	}

	public void setUnidadOrganica(String unidadOrganica) {
		this.unidadOrganica = unidadOrganica;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}
    
    
    
}
