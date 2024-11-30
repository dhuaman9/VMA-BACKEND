package pe.gob.sunass.vma.constants;



public class Constants {

	final public static String EMPRESA_EPS = "EPS";
	final public static String EMPRESA_SUNASS = "SUNASS";
	final public static String TIPO_EMPRESA_NINGUNO = "NINGUNO"; // En caso de empresa Sunass, el tipo es : NINGUNO 
	final public static String EPS_SEDAPAL = "SEDAPAL";
	final public static String ESTADO_INCOMPLETO = "INCOMPLETO";
	final public static String ESTADO_COMPLETO = "COMPLETO";
	final public static String ESTADO_SIN_REGISTRO = "SIN REGISTRO";
	final public static String LABEL_PROMEDIO = "PROMEDIO";
	final public static String LABEL_TOTAL = "TOTAL";
	
	final public static class Alternativas{
		final public static String Opcion_SI = "SI";
		final public static String Opcion_NO = "NO";
	}
	
	 final public static String CRLF     = "\r\n";
	 final public static String Quotes   = Character.toString((char) 34);

	 final public static class Locale{
	   final public static String Language_Spanish = "es";
	   final public static String Country_Peru   = "PE";
	   final public static java.util.Locale Spanish = new java.util.Locale(Language_Spanish, Country_Peru);
	 }


	  final public static class Format{
	    final public static class Date{
	      final public static String Year              = "yyyy";
	      final public static String Month             = "MM";
	      final public static String Month4            = "MMMM";
	      final public static String Day               = "dd";
	      final public static String YearMonthDay      = "yyyyMMdd";
	      final public static String YearMonthDaySlash = "yyyy/MM/dd";
	      final public static String YearMonth         = "yyyyMM";
	      final public static String DayMonthYear      = "dd/MM/yyyy";
	      final public static String MonthDayYear      = "MM/dd/yyyy";
	    }

	    final public static class Time{
	      final public static String Hours               = "HH";
	      final public static String Minutes             = "mm";
	      final public static String HoursMinutes        = "HH:mm";
	      final public static String HoursMinutesSeconds = "HH:mm:ss";
	      final public static String Time                = "HH:mm:ss,SSS";
	    }

	    final public static class DateTime{
	      final public static String DateTime  = "dd/MM/yyyy HH:mm:ss";
	      final public static String ISO8601   = "yyyy-MM-dd'T'HH:mm:ss";
	      final public static String TimeStamp = "dd/MM/yyyy HH:mm:ss,SSS";
	      final public static String Stamp     = "yyyyMMddHHmmssSSS";
	    }
	  }
	  
	
	  
	  final public static class Regimen{
		  
		  final public static String RAT = "RAT";
		  final public static String NO_RAT = "NO RAT"; 
		  
		  
	  }
		    
	
	  final public static class Security{
		    
		    final public static class Headers{
		      final public static String Authorization                  = "Authorization";
		      final public static String AuthorizationExpirationSeconds = "Authorization-Expiration-Seconds";
		    }

		    final public static class Token{
		      final public static long ExpirationTime = 3600;  // 60 mins
		      final public static String BearerPrefix = "Bearer ";
		    }
		    
		    final public static class Roles{
				  
		    	final public static String ROLE_ADMINISTRADOR_OTI = "ADMINISTRADOR OTI";
		    	final public static String ROLE_ADMINISTRADOR_DF = "ADMINISTRADOR DF";
		    	final public static String ROLE_REGISTRADOR = "REGISTRADOR";
		    	final public static String ROLE_CONSULTOR = "CONSULTOR";
		    	
				final public static int ID_AdministradorOTI = 1;
				final public static int ID_AdministradorDF = 2; 
				final public static int ID_Registrador = 3; 
				final public static int ID_Consultor = 4; 
				  
			}
	  }
	  
	 /* final public static class Archivos{
		  
		  final public static String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
	      final public static String EXCEL_FILENAME = "attachment; filename=registros_vma.xlsx";
		  
	  }*/

	
}
