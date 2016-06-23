/**
 * 
 */
package com.autoStock.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.autoStock.Co;
import com.autoStock.backtest.BacktestUtils.LookDirection;
import com.autoStock.tools.DateConditions.BaseDateCondition;
import com.autoStock.tools.DateConditions.QuoteAvailableDateCondition;
import com.autoStock.types.basic.Time;

/**
 * @author Kevin Kowalewski
 *
 */
public class DateTools {
	public static String getPrettyDate(long date){
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		//dateFormat.applyPattern("EEE, MMM d, yyyy hh:mm:ss a");
		dateFormat.applyPattern("yyyy/MM/dd hh:mm:ss a");
		
		return dateFormat.format(new Date(date));
	}
	
	public static String getPretty(Date date){
		return getPretty(date, false);
	}
	
	public static String getPretty(Date date, boolean includeSeconds){
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		if (includeSeconds){dateFormat.applyPattern("yyyy/MM/dd hh:mm:ss a");}
		else {dateFormat.applyPattern("yyyy/MM/dd hh:mm a");}
		return dateFormat.format(date);
	}
	
	public static String getSqlDate(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss.S");
		return dateFormat.format(date);
	}
	
	public static String getEncogDate(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.applyPattern("yyyy-MM-dd");
		return dateFormat.format(date);
	}
	
	public static Date getDateFromString(String date){
		try {
			return new SimpleDateFormat("MM/dd/yyyy_HH:mm.a").parse(date);
		}catch (ParseException e1){
			try {
				return new SimpleDateFormat("yyyy/MM/dd.HH:mm:ss.a").parse(date);
			}catch (ParseException e2){
				try {
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(date);
				}catch (ParseException e3){
					try {
						return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);	
					}catch (ParseException e4){
						try {
							return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a").parse(date);			
						}catch(ParseException e5){
							}try {
								return new SimpleDateFormat("MM/dd/yyyy").parse(date);
							}catch (ParseException e6){
								Co.println("Could not parse: " + date);
								return null;	
							}
					}
				}
			}
		}
	}
	
	public static Time getTimeFromString(String timeString){
		Time time = new Time();
		int offset = 0;
		
		if (timeString.contains("+") || timeString.contains("-")){
			offset = 1;
		}
		
		time.hours = Integer.valueOf(timeString.substring(0 + offset, 2 + offset));
		time.minutes = Integer.valueOf(timeString.substring(3 + offset, 5 + offset));
		time.seconds = Integer.valueOf(timeString.substring(7 + offset, 8 + offset));
		
		if (timeString.contains("-")){
			time.hours *= -1;
		}
		
		return time;
	}
	
	public static Date getChangedBySubtracting(Date date, int minutes){
		return new Date(date.getTime() - (minutes * 60) * 1000);
	}
	
	public static Date getDateFromTime(Time time){
		Date date = new Date();
		date.setHours(time.hours);
		date.setMinutes(time.minutes);
		date.setSeconds(time.seconds);
		
		return date;
	}
	
	public static ArrayList<Date> getListOfDatesOnWeekdays(Date startDate, Date endDate, BaseDateCondition dateCondition){
		ArrayList<Date> listOfDate = new ArrayList<Date>();
		GregorianCalendar calendarAtCurrent = new GregorianCalendar();
		GregorianCalendar calendarAtEnd = new GregorianCalendar();
		
		calendarAtCurrent.setTime(startDate);
		calendarAtEnd.setTime(endDate);
		
		while (calendarAtCurrent.before(calendarAtEnd) || calendarAtCurrent.equals(calendarAtEnd)){
			if (dateCondition != null){dateCondition.setDate(calendarAtCurrent.getTime());}

			if (isWeekday(calendarAtCurrent) && (dateCondition == null || dateCondition.isValid())){
				listOfDate.add(new Date(calendarAtCurrent.getTimeInMillis()));
			}
			
			calendarAtCurrent.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		return listOfDate;
	}
	
	public static ArrayList<Date> getListOfDatesOnWeekdays(Date startDate, LookDirection direction, int days, BaseDateCondition dateCondition){
		ArrayList<Date> listOfDate = new ArrayList<Date>();
		GregorianCalendar calendarAtCurrent = new GregorianCalendar();
		
		calendarAtCurrent.setTime(startDate);
		
		int maxLookback = 16;
		
		while (listOfDate.size() < days){
			if (dateCondition != null){dateCondition.setDate(calendarAtCurrent.getTime());}
			
			if (isWeekday(calendarAtCurrent) && (dateCondition == null || dateCondition.isValid())){
				listOfDate.add(new Date(calendarAtCurrent.getTimeInMillis()));
			}
			
			if (direction == LookDirection.forward){
				calendarAtCurrent.add(Calendar.DAY_OF_MONTH, 1);
			}else if (direction == LookDirection.backward){
				calendarAtCurrent.add(Calendar.DAY_OF_MONTH, -1);
			}
			
			maxLookback--;
			if (maxLookback == 0){throw new IllegalStateException("Could not find lookback data for: " + startDate + ", " + direction.name() + ", " + dateCondition.date);}
		}
		
		return listOfDate;
	}
	
	public static boolean isWeekday(Calendar calendar){
		return calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY;
	}
	
	public static Date getFirstWeekdayAfter(Date date){
		return getListOfDatesOnWeekdays(new Date(date.getTime() + 1000 * 60 * 60 * 24 * 1), new Date(date.getTime() + 1000 * 60 * 60 * 24 * 7), null).get(0);
	}
	
	public static Date getFirstWeekdayBefore(Date date){
		GregorianCalendar calendarAtCurrent = new GregorianCalendar();
		calendarAtCurrent.setTime(date);
		
		calendarAtCurrent.add(Calendar.DAY_OF_MONTH, -1);
		
		while (isWeekday(calendarAtCurrent) == false){
			calendarAtCurrent.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		return calendarAtCurrent.getTime();
	}
	
	public static Date getRolledDate(Date date, int calendarField, int amount) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendarField, amount);
		return calendar.getTime();
	}
	
	public static Date getLocalDateFromForeignTime(Time time, String timeZone) {
		GregorianCalendar calendarForForeign = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		calendarForForeign.set(Calendar.HOUR_OF_DAY, time.hours);
		calendarForForeign.set(Calendar.MINUTE, time.minutes);
		calendarForForeign.set(Calendar.SECOND, time.seconds);

		Calendar calendarForLocal = new GregorianCalendar();
		calendarForLocal.setTimeInMillis(calendarForForeign.getTimeInMillis());

		return new Date(calendarForLocal.getTimeInMillis());
	}
	
	public static Date getForeignDateFromLocalTime(Time time, String timeZone) {
		GregorianCalendar calendarForForeign = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		Date date = new Date();
		
		date.setHours(calendarForForeign.get(Calendar.HOUR_OF_DAY));
		date.setMinutes(calendarForForeign.get(Calendar.MINUTE));
		date.setSeconds(calendarForForeign.get(Calendar.SECOND));
		
		return date;
	}
	
	public static Date getSameDateMaxTime(Date date){
		GregorianCalendar calendarAtCurrent = new GregorianCalendar();
		calendarAtCurrent.setTime(date);
		calendarAtCurrent.set(Calendar.HOUR_OF_DAY, 23);
		calendarAtCurrent.set(Calendar.MINUTE, 59);
		calendarAtCurrent.set(Calendar.SECOND, 59);
		calendarAtCurrent.set(Calendar.MILLISECOND, 999);
		
		return calendarAtCurrent.getTime();
	}
	
	public static Date getSameDateMinTime(Date date){
		GregorianCalendar calendarAtCurrent = new GregorianCalendar();
		calendarAtCurrent.setTime(date);
		calendarAtCurrent.set(Calendar.HOUR_OF_DAY, 0);
		calendarAtCurrent.set(Calendar.MINUTE, 0);
		calendarAtCurrent.set(Calendar.SECOND, 0);
		calendarAtCurrent.set(Calendar.MILLISECOND, 0);
		
		return calendarAtCurrent.getTime();
	}

	public static Time getTimeUntil(Date date) {
		Time time = new Time();
		Date localDate = new Date();

		long millisDiff = date.getTime() - localDate.getTime();

		time.hours = (int) (millisDiff / (1000*60*60));
		time.minutes = (int) ((millisDiff % (1000*60*60)) / (1000*60));
		time.seconds = (int) (((millisDiff % (1000*60*60)) % (1000*60)) / 1000);

		return time;
	}
	
	public static Time getTimeUntilDate(Date dateFirst, Date dateSecond){
		Time time = new Time();
		
		long millisDiff = dateFirst.getTime() - dateSecond.getTime();

		time.hours = (int) (millisDiff / (1000*60*60));
		time.minutes = (int) ((millisDiff % (1000*60*60)) / (1000*60));
		time.seconds = (int) (((millisDiff % (1000*60*60)) % (1000*60)) / 1000);
		
		return time;
	}
	
	public static Time getTimeUntilTime(Time timeFirst, Time timeSecond){
		Time time = new Time();
		
		long secDiff = timeFirst.asSeconds() - timeSecond.asSeconds();
		
		time.hours = (int) (secDiff / (60*60));
		time.minutes = (int) ((secDiff % (60*60)) / 60);
		time.seconds = (int) (((secDiff % (60*60)) % 60));
		
		return time;
	}
	
	public static Time getRolledTime(Time time, int seconds){
		int timeInSeconds = (time.hours*60*60) + (time.minutes *60) + time.seconds;
		Time timeForReturn = new Time();
		
		if (seconds >= 0){
			timeInSeconds += seconds;
		}else{
			timeInSeconds -= seconds;
		}
		
		timeForReturn.hours = (int) (timeInSeconds / (60*60));
		timeForReturn.minutes = (int) ((timeInSeconds % (60*60)) / 60);
		timeForReturn.seconds = (int) (((timeInSeconds % (60*60)) % 60));
		
		return timeForReturn;
	}
	
	public static Time getTimeFromDate(Date date){
		Time time = new Time();
		@SuppressWarnings("deprecation")
		long dateMills = date.getTime() - date.getTimezoneOffset() * 60 * 1000;

		time.seconds = (int) (dateMills / 1000) % 60 ;
		time.minutes = (int) ((dateMills / (1000*60)) % 60);
		time.hours  = (int) ((dateMills / (1000*60*60)) % 24);
		
		return time;
	}
	
	public static Date getEarliestDate(List<Date> listOfDate){
		Date date = null;
		
		for (Date dateItem : listOfDate){
			if (date == null){
				date = dateItem;
			}else{
				if (dateItem.before(date)){
					date = dateItem;
				}
			}
		}
		
		return date;
	}
	
	public static Date getLatestDate(List<Date> listOfDate){
		Date date = null;
		
		for (Date dateItem : listOfDate){
			if (date == null){
				date = dateItem;
			}else{
				if (dateItem.after(date)){
					date = dateItem;
				}
			}
		}
		
		return date;
	}
}
