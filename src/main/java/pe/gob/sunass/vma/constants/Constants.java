package pe.gob.sunass.vma.constants;



public class Constants {

	 final public static String CRLF     = "\r\n";
	  final public static String Quotes   = Character.toString((char) 34);

	  final public static class Locale{
	    final private static String Language_Spanish = "es";
	    final private static String Country_Peru   = "PE";
	    final public static java.util.Locale Spanish = new java.util.Locale(Language_Spanish, Country_Peru);
	  }

	  final public static class Logger{
	    final public static class Class{
	      final public static String Initialize  = "Class_Initialize";
	      final public static String Finalize    = "Class_Finalize";
	    }

	    final public static class Method{
	      final public static String Initialize = "Method_Initialize";
	      final public static String Finalize   = "Method_Finalize";
	    }
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

	  final public static class Security{
	    final public static String IssuerInfo           = "https://www.empresa.com/";
	    final public static String UriPathRegExpPattern = "([/|?]?[a-zA-Z0-9\\-._~%!$&'()*+,;=@]+)*";

	    final public static class Keys{
	      final public static String Signing     = "company365/key@2021";
	      final public static String Authorities = "CLAIM_TOKEN";
	    }

	    final public static class Headers{
	      final public static String Authorization                  = "Authorization";
	      final public static String AuthorizationExpirationSeconds = "Authorization-Expiration-Seconds";
	    }

	    final public static class Token{
	      final public static long ExpirationTime = 1800;  // 30 mins
	      final public static String BearerPrefix = "Bearer";
	    }
	  }
	
	
	
}
