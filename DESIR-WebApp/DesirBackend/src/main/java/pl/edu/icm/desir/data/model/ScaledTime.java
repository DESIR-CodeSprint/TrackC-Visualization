package pl.edu.icm.desir.data.model;

import java.time.LocalDate;

public class ScaledTime {

	public enum Scale {
		DEFAULT
	}
	//nowa klasa ScaledTime
    //miejsce na trzymanie LocalDate + enum skala (statyczny w klasie)
    //w przyszłości miejsce na niepewność
	
	LocalDate localDate;
	Scale scale;
	
	public LocalDate getLocalDate() {
		return localDate;
	}
	public void setLocalDate(LocalDate localDate) {
		this.localDate = localDate;
	}
	public Scale getScale() {
		return scale;
	}
	public void setScale(Scale scale) {
		this.scale = scale;
	}
	
}
