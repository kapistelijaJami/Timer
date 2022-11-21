package timer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class DurationFormat {
	public enum TimePart {
		MILLISECONDS, SECONDS, MINUTES, HOURS;
	}
	
	private boolean hours = false, minutes = false, seconds = false, milliseconds = false;
	private int hoursDigits, minutesDigits, secondsDigits, millisecondsDigits; //kaikki oli alus 2, poistin defaultit
	private ArrayList<Character> delims = new ArrayList<>();
	
	private final String possibleDelims = ":.,";
	
	/**
	 * Creates settings object.
	 * If you don't know the format before having the String you can create it with createFormat(str),
	 * where str is the duration String.
	 * @param format hh:mm:ss.lll etc
	 */
	public DurationFormat(String format) { //TODO: error checking
		for (int i = 0; i < format.length(); i++) {
			char c = format.charAt(i);
			if (possibleDelims.contains("" + c)) {
				delims.add(c);
			}
		}
		
		/*for (Character c : splitters) {
			System.out.println(c);
		}*/
		
		String[] parts = format.split("[" + possibleDelims + "]+", -1);
		
		for (String part : parts) {
			if (part.toLowerCase().contains("h")) {
				hours = true;
				hoursDigits = part.length();
			}
			if (part.toLowerCase().contains("m")) {
				minutes = true;
				minutesDigits = part.length();
			}
			if (part.toLowerCase().contains("s")) {
				seconds = true;
				secondsDigits = part.length();
			}
			if (part.toLowerCase().contains("l")) {
				milliseconds = true;
				millisecondsDigits = part.length();
			}
		}
		
		fixDelims();
	}
	
	public DurationFormat(boolean hours, boolean minutes, boolean seconds, boolean milliseconds) {
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.milliseconds = milliseconds;
	}
	
	/**
	 * Creates a format automatically from the duration string.
	 * @param dur Duration string. For example "12:53:07.124"
	 * @return 
	 */
	public static String createFormat(String dur) { //TODO: error checking for bad inputs
		String out;
		
		int i = countOccurances(dur, ":");
		switch (i) {
			case 1:
				out = "mm:ss"; //prioritizes seconds over hours.
				break;
			case 2:
				out = "hh:mm:ss";
				break;
			default:
				out = "ss";
				break;
		}
		
		if (dur.contains(".")) {
			out += ".";
			int j = dur.substring(dur.lastIndexOf(".")).length();
			for (; j > 0; j--) {
				out += "l";
			}
		}
		
		return out;
	}
	
	private static int countOccurances(String s, String c) {
		return s.length() - s.replaceAll(c, "").length();
	}
	
	/**
	 * String representation of the format.
	 * @return 
	 */
	@Override
	public String toString() {
		return getTimePartString(TimePart.HOURS) + getTimePartString(TimePart.MINUTES) + getTimePartString(TimePart.SECONDS) + getTimePartString(TimePart.MILLISECONDS);
	}
	
	private String getTimePartString(TimePart part) {
		switch (part) {
			case HOURS:
				return hours ? repeatChar('h', hoursDigits) + (minutesDigits == 0 ? "" : ":") : "";
			case MINUTES:
				return minutes ? repeatChar('m', minutesDigits) + (secondsDigits == 0 ? "" : ":") : "";
			case SECONDS:
				return seconds ? repeatChar('s', secondsDigits) + (millisecondsDigits == 0 ? "" : ".") : "";
			case MILLISECONDS:
				return milliseconds ? repeatChar('l', millisecondsDigits) : "";
		}
		return "";
	}
	
	private static String repeatChar(char c, int times) {
		String res = "";
		for (int i = 0; i < times; i++) {
			res += c;
		}
		return res;
	}
	
	private void fixDelims() {
		ArrayList<Character> newDelims = new ArrayList<>();
		
		if (hours) {
			newDelims.add(delims.get(0));
			delims.remove(0);
		} else {
			newDelims.add(null);
		}
		
		if (minutes) {
			newDelims.add(delims.get(0));
			delims.remove(0);
		} else {
			newDelims.add(null);
		}
		
		if (seconds && milliseconds) {
			newDelims.add(delims.get(0));
			delims.remove(0);
		} else {
			newDelims.add(null);
		}
		
		delims = newDelims;
	}
	
	public String format(Duration dur) {
		String h = getFormattedTimePart(dur.toHoursPart(), TimePart.HOURS);
		String m = getFormattedTimePart(dur.toMinutesPart(), TimePart.MINUTES);
		String s = getFormattedTimePart(dur.toSecondsPart(), TimePart.SECONDS);
		String mill = getFormattedTimePart(dur.toMillisPart(), TimePart.MILLISECONDS);
		
		return h + m + s + mill;
	}
	
	private String getFormattedTimePart(int num, TimePart timePart) {
		String endingChar = "";
		String numS = "";
		
		switch (timePart) {
			case HOURS:
				if (delims.get(0) == null) {
					return "";
				}
				endingChar = "" + delims.get(0);
				numS = preZeros(num, hoursDigits);
				break;
			case MINUTES:
				if (delims.get(1) == null) {
					return "";
				}
				endingChar =  "" + delims.get(1);
				numS = preZeros(num, minutesDigits);
				break;
			case SECONDS:
				if (delims.get(2) == null) {
					return "";
				}
				endingChar = "" + delims.get(2);
				numS = preZeros(num, secondsDigits);
				break;
			case MILLISECONDS:
				numS = preZeros(num, 3).substring(0, millisecondsDigits);
				break;
		}
		
		return numS + endingChar;
	}
	
	/**
	 * Parses a duration string into a Duration object.
	 * Duration must include seconds, others are optional, and be in a basic format of "hh:mm:ss.lll" or "hh:mm:ss,lll".
	 * @param dur Duration string. For example "12:53:07.124"
	 * @return 
	 */
	public Duration parse(String dur) { //format "hh:mm:ss.lll" or "hh:mm:ss,lll" etc
		try {
			DateFormat f = new SimpleDateFormat(toString().replaceAll("l", "S").replaceAll(",", ".")); //milliseconds are represented with S in SimpleDateFormat
			f.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date d = f.parse(dur.replaceAll(",", ".")); //Also they have to use . for separating them
			
			return Duration.ofMillis(d.getTime());
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Parses the time without creating DurationFormat manually.
	 * Format is created automatically based on the input time.
	 * @param dur
	 * @return 
	 */
	public static Duration parseSimple(String dur) {
		return new DurationFormat(DurationFormat.createFormat(dur)).parse(dur);
	}
	
	private static String preZeros(int i, int numberOfDigits) {
		return String.format("%0" + numberOfDigits + "d", i);
	}
}
