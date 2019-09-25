package pl.edu.icm.desir.data.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DataUtils {

	public static LocalDate convertToLocalDate(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static String createHashWithTimestamp(String text) {
		return text.hashCode() + System.currentTimeMillis() + "";
	}

}
