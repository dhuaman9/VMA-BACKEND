package pe.gob.sunass.vma.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import pe.gob.sunass.vma.constants.Constants;


final public class NumberUtil {
  private final static String HEX_DIGITS = "0123456789ABCDEF";

  public NumberUtil() {}

  public static Integer hex2decimal(String s) throws Exception {
    int value = 0;

    s = s.toUpperCase();
    if (s.indexOf("0X") > -1) s = s.replace("0X", "");

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      value = 16 * value + HEX_DIGITS.indexOf(c);
    }

    return new Integer(value);
  }

  public static Double toDouble(Object data){
  return toDouble(data, new Double(0));
  }
  public static Double toDouble(Object data, Double defa){
  return (data == null ? defa : new Double(((BigDecimal) data).doubleValue()));
  }

  public static Long toLong(Object data){
    return toLong(data, new Long(0));
  }
  public static Long toLong(Object data, long defa){
    return (data == null ? defa : new Long(((BigDecimal) data).longValue()));
  }

  public static Integer toInt(Object data){
    return toInt(data, new Integer(0));
  }
  public static Integer toInt(Object data, int defa){
    return (data == null ? defa : new Integer(((BigDecimal) data).intValue()));
  }

//  public static NumberFormat getNumberFormat() {
//    return NumberFormat.getInstance(Constants.Locale.Spanish);
//   }
//
//  public static String format(String pattern, Long number){
//    DecimalFormat df = format(pattern);
//    return(df.format(number.longValue()));
//  }
//
//  public static String format(String pattern, Integer number){
//    DecimalFormat df = format(pattern);
//    return(df.format(number.intValue()));
//  }
//
//  public static String format(String pattern, Double number){
//    DecimalFormat df = format(pattern);
//    return(df.format(number.doubleValue()));
//  }

//  private static DecimalFormat format(String pattern){
//    NumberFormat nf = getNumberFormat();
//    if(nf instanceof DecimalFormat){
//      DecimalFormat df = (DecimalFormat) nf;
//      df.applyPattern(pattern);
//      return(df);
//    }
//    return (null);
//  }

  public static int getRandom(int max){
  Random random = new Random();
  return (random.nextInt(max) + 1);
  }

  public static int getRandomRange(int min, int max){
  Random random = new Random();
  return (random.nextInt((max - min) + 1) + min);
  }

  public static Double round(Double value, int decimalPlace) {
    return new Double(round(value.doubleValue(), decimalPlace));
  }

  public static double round(double value, int decimalPlace) {
    double thisFunction;
    double power_of_ten = 1;

    while (decimalPlace-- > 0){
       power_of_ten *= 10.0;
    }

    thisFunction = Math.round(value * power_of_ten) / power_of_ten;

    return(thisFunction);
  }


 /* public static String formatStorageUnit(long fileSize){

  String sizeUnit = "";
  double size = 0;

    if (fileSize >= 0 && fileSize <= 1024){
      size = fileSize * 1.0;
      sizeUnit = "B";
    }
    else if (fileSize >= 1025 && fileSize <= 1048576){
      size = (double) ((1.0 * fileSize) / 1024);
      sizeUnit = "KB";
    }
    else if (fileSize >= 1048577 && fileSize <= 1073741824){
      size = (double) ((1.0 * fileSize) / (1024 * 1024) );
      sizeUnit = "MB";
    }
    else if (fileSize >= 1073741825){
      size = (double) ((1.0 * fileSize) / (1024 * 1024 * 1024) );
      sizeUnit = "GB";
    }

  return format("#,###,###,##0.00", size) + " " + sizeUnit;
  }*/

  final public static boolean validate(final String value){
    return value.matches("^\\d{1,}?$");
  }
}
