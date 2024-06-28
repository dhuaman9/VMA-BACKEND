package pe.gob.sunass.vma.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pe.gob.sunass.vma.constants.Constants;


public class DateUtil {
  public static String format(String pattern){
    return(format(pattern, new Date()));
  }

  public static String format(String pattern, Date fecha){
    return(fecha == null ? "??/??/????" : (new SimpleDateFormat(pattern)).format(fecha));
  }

  public static Date getDate(String date){
  	return getDate(date, "DMY", "/");
  }

  public static Date getDate(String date, String type){
    return getDate(date, type, "/");
  }

  public static Date getDate(String date, String type, String sep){
    Date response = null;
    Calendar calendar = Calendar.getInstance();

    if (date.length() == 10){
      //     |  0  1  2
      //----------------
      // DMY | 21/08/2019
      // MDY | 08/21/2019

      if (type.equals("DMY")) {
        calendar.set(Integer.parseInt(date.split("[" + sep + "]")[2]),
                     Integer.parseInt(date.split("[" + sep + "]")[1]) - 1,
                     Integer.parseInt(date.split("[" + sep + "]")[0]));
      }
      else {
        calendar.set(Integer.parseInt(date.split("[" + sep + "]")[2]),
                     Integer.parseInt(date.split("[" + sep + "]")[0]) - 1,
                     Integer.parseInt(date.split("[" + sep + "]")[1]));
      }

      response = calendar.getTime();
    }

    return response;
  }

  public static Date getTimeStamp(Date date){
	  return getTimeStamp(format(Constants.Format.Date.DayMonthYear, date),
			                  format(Constants.Format.Time.HoursMinutesSeconds, date));
  }

  public static Date getTimeStamp(String date){
  	return getTimeStamp(date, "");
  }

  public static Date getTimeStamp(String date, String time){
  	Date returnValue = null;
  	Calendar calendar = Calendar.getInstance();

  	if (date.length() == 10 && (time.length() == 0 || time.length() == 5 || time.length() == 8)){
  		if (time.length() == 0) time += "00:00:00";
  		else if (time.length() == 5) time += ":00";

  		calendar.set(Integer.parseInt(date.split("[/]")[2]),
	                 Integer.parseInt(date.split("[/]")[1]) - 1,
	                 Integer.parseInt(date.split("[/]")[0]),
	                 Integer.parseInt(time.split("[:]")[0]),
	                 Integer.parseInt(time.split("[:]")[1]),
	                 Integer.parseInt(time.split("[:]")[2]));
  		returnValue = calendar.getTime();
  	}

  	return returnValue;
  }

  public static Date getDateISO(String dateISO){
	 Date returnValue = null;
	 Calendar calendar = Calendar.getInstance();

	 if (dateISO != null && dateISO.length() >= 19){
  		calendar.set(Integer.parseInt(dateISO.substring(0, 4 )),
	                 Integer.parseInt(dateISO.substring(5, 7)) - 1,
	                 Integer.parseInt(dateISO.substring(8, 10)),
	                 Integer.parseInt(dateISO.substring(11, 13)),
	                 Integer.parseInt(dateISO.substring(14, 16)),
	                 Integer.parseInt(dateISO.substring(17, 19)));
 		returnValue = calendar.getTime();
	 }

	 return returnValue;
  }

  public static boolean validateDateFormat(String value){
  	return value.matches("^\\d{2}/\\d{2}/\\d{4}$");
  }

  @SuppressWarnings("unused")
  public static boolean validateDateValue(String value){
  	boolean retValue = false;

		try {
			DateFormat dateFormat = new SimpleDateFormat(Constants.Format.Date.DayMonthYear);
			dateFormat.setLenient(false);
			String out = "" + dateFormat.parse(value);
			retValue = true;
		}
		catch (ParseException e) {}

  	return retValue;
  }

  public static boolean validateTimeFormat(String value){
  	if (value.length() == 5) value += ":00";
  	return value.matches("^\\d{2}:\\d{2}:\\d{2}$");
  }

  @SuppressWarnings("unused")
  public static boolean validateTimeValue(String value){
  	boolean retValue = false;

  	if (value.length() == 5) value += ":00";

		try {
			DateFormat dateFormat = new SimpleDateFormat(Constants.Format.Time.HoursMinutesSeconds);
			dateFormat.setLenient(false);
			String out = "" + dateFormat.parse(value);
			retValue = true;
		}
		catch (ParseException e) {}

  	return retValue;
  }

  public static String timeElapsed(long elapsed){
    double time = elapsed;
    String unit = "ms";

    if (time > 1000){
      time = (double) (time / 1000.0);
      unit = "secs";

      if (time > 60){
        time = (double) (time / 60.0);
        unit = "mins";

        if (time > 60){
          time = (double) (time / 60.0);
          unit = "hrs";
        }
      }
    }

    return NumberUtil.format("###,###,##0.00", new Double(time)) + " " + unit;
  }

  private static int getDaysOnFirstWeek(){
  	return getDaysOnFirstWeek(Integer.parseInt(format(Constants.Format.Date.Year)));
  }

  //private static int getDaysOnFirstWeek(String year){
  //	return getDaysOnFirstWeek(Integer.parseInt(year));
  //}

  private static int getDaysOnFirstWeek(int year){
		Calendar calendar = Calendar.getInstance();
		calendar.setLenient(false);
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		calendar.set(year, 0, 1);
		return (7 - calendar.get(Calendar.DAY_OF_WEEK) + 1);
  }

  public static Calendar getCalendarInstance(){
  	Calendar calendar = Calendar.getInstance();
		calendar.setLenient(false);
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		calendar.setMinimalDaysInFirstWeek(getDaysOnFirstWeek());
  	return calendar;
  }

  public static Calendar getCalendarInstance(int year){
  	Calendar calendar = Calendar.getInstance();
		calendar.setLenient(false);
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		calendar.setMinimalDaysInFirstWeek(getDaysOnFirstWeek(year));
  	return calendar;
  }

  public static Calendar getCalendarInstance(int year, int month, int day){
  	Calendar calendar = Calendar.getInstance();
		calendar.setLenient(false);
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
	    calendar.setMinimalDaysInFirstWeek(getDaysOnFirstWeek(year));
        calendar.set(year, month - 1, day);
  	return calendar;
  }

  public static Calendar getCalendarInstance(String date, String time){
  	Calendar calendar = Calendar.getInstance();
  	calendar.setLenient(false);
  	calendar.setFirstDayOfWeek(Calendar.SUNDAY);
  	calendar.setMinimalDaysInFirstWeek(getDaysOnFirstWeek(Integer.parseInt(date.split("[/]")[2])));

  	calendar.set(Integer.parseInt(date.split("[/]")[2]),
                   Integer.parseInt(date.split("[/]")[1]) - 1,
                   Integer.parseInt(date.split("[/]")[0]),
                   Integer.parseInt(time.split("[:]")[0]),
                   Integer.parseInt(time.split("[:]")[1]),
                   Integer.parseInt(time.split("[:]")[2]));
  	return calendar;
  }
}
