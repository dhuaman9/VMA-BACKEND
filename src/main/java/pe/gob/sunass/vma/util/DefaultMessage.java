package pe.gob.sunass.vma.util;

public class DefaultMessage {
	
	public static class Error {
        public static final String FINDALL = "Ocurrió un error al recuperar los elementos";
        public static final String FINDBY = "Ocurrió un error al recuperar los elementos por el/los filtro(s)";
        public static final String FINDONE = "Ocurrió un error al recuperar el elemento";
        public static final String SAVE = "Ocurrió un error al guardar el elemento";
        public static final String SAVEINSERT = "Ocurrió un error al registrar el elemento";
        public static final String SAVEUPDATE = "Ocurrió un error al actualizar el elemento";
        public static final String DELETE = "Ocurrió un error al eliminar el elemento";
        public static final String EXPORT = "Ocurrió un error al exportar";
        public static final String UPLOAD = "Ocurrió un error al subir el archivo";

        public static String customMessage(String value) {
            return value;
        }
    }

    public static class Success {
        public static final String FINDALL = "Se han recuperados los elementos satisfactoriamente";
        public static final String FINDBY = "Se ha recuperado los elementos por el/los filtro(s) satisfactoriamente";
        public static final String FINDONE = "Se ha recuperado el elemento satisfactoriamente";
        public static final String SAVEINSERT = "Se ha registrado el elemento satisfactoriamente";
        public static final String SAVEUPDATE = "Se ha registrado el elemento satisfactoriamente";
        public static final String DELETE = "Se ha eliminado el elemento satisfactoriamente";
        public static final String EXPORT = "Se ha exportado satisfactoriamente";
        public static final String UPLOAD = "Se ha subido correctamente el archivo";

        public static String customMessage(String value) {
            return value;
        }
    }

    public static class ServerStatus {
        public static final String S401 = "Ud. no tiene permiso";
        public static final String S400 = "Mala petición";
        public static final String S430 = "Acceso denegado";
        
        public static String customMessage(String value) {
            return value;
        }
    }
}
