package pl.edu.icm.desir.data.model.utility;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import pl.edu.icm.desir.data.model.Entity;
import pl.edu.icm.desir.data.model.ScaledTime;
import pl.edu.icm.desir.data.model.ScaledTime.Scale;
import pl.edu.icm.desir.data.model.SpatiotemporalPoint;


public class TrajectoryHelper {

	public static Set<String> getPath(Entity entity) {
		return new HashSet<String>();
	}

	public static ScaledTime[] getCalendarTimeline(Entity entity) {
		return new ScaledTime[0];
	}

	public static ScaledTime[] getCalendarTimeline(Entity entity, Scale scale) {
		return new ScaledTime[0];
	}

	public static float[] getAbstractTimeline(Entity entity) {
		return new float[0];
	}

	public static Set<SpatiotemporalPoint> getTrajectory(boolean sortByCalendarTime,
			Comparator<SpatiotemporalPoint> comparator) {
		return new HashSet<SpatiotemporalPoint>();
	}

}
