package pl.edu.icm.desir.data.model;

import java.util.Calendar;
import java.util.Date;

import pl.edu.icm.desir.data.model.ScaledTime.Scale;

/**
 * Spatiotemporal point is a class that encapsulates information on a certain 
 * point in time and space. Thus, it contains separate information on time 
 * and on location.
 * Is used in entities and relations to define their start/end time and location.
 * Time can be defined as date (real) or as a continuous variable (abstract).
 * Functionality (methods)
 * Provide information if time is real or abstract (e.g. isReal/isAbstract).
 * If time is real provide information on date/time in a given time scale 
 * (e.g. getTime(scale), where scale can be e.g. century, year, month, week, day, …).
 * If time is abstract provide information of continuous time value (float).
 * Provide information on location (TBD)
 *
 * @author blazejc
 *
 */
public class SpatiotemporalPoint {


	ScaledTime calendarTime;
	
	float abstractTime; 
	String abstractTimeUnit; 
    String name;
    String description;
    
    //time can be set as either calendarTime or abstractTime, or as both at he same time
    //zmiana na nową wersję klasy LocalDate / LocalDateTime bez użycia Calendar
    
    //nowa klasa ScaledTime
    //miejsce na trzymanie LocalDate + enum skala (statyczny w klasie)
    //w przyszłości miejsce na niepewność
    
    //should be comparable to enable sorting - sorting by time, which time? argument choice calendar or abstract time
	
	String location;

	public ScaledTime getCalendarTime() {
		return calendarTime;
	}

    public ScaledTime getCalendarTime(Scale scale) {
        //skala nie może być mniejsza niż validity scale
		return calendarTime;
	}
    
	public void setCalendarTime(ScaledTime calendarTime) {
		this.calendarTime = calendarTime;
	}


	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean hasRealTime() {
		return calendarTime != null;
	}

	public float getAbstractTime() {
		return abstractTime;
	}

	public void setAbstractTime(float abstractTime) {
		this.abstractTime = abstractTime;
	}

	public String getAbstractTimeUnit() {
		return abstractTimeUnit;
	}

	public void setAbstractTimeUnit(String abstractTimeUnit) {
		this.abstractTimeUnit = abstractTimeUnit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
