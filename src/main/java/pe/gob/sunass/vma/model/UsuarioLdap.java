package pe.gob.sunass.vma.model;



public class UsuarioLdap {

	private String cuenta;//cambiar por correo
    private String nombre;
    private String usuario;
    private String unidadOrganica;
//    private String nombres;
//	private String apellidos;
//	private String userName;
//	private String correo;
	
    
    

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
    
    
    
}
