package pl.edu.icm.desir.data.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import pl.edu.icm.jscic.dataarrays.DataObjectInterface;

public class Interaction implements DataObjectInterface, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6619663468898757172L;
	
	List<String> actors;
	List<String> names;
	
	
	
	public Interaction(String ... actor) {
		this.actors = Arrays.asList(actor);
	}
	
	public List<String> getActors() {
		return actors;
	}
	public void setActors(List<String> actors) {
		this.actors = actors;
	}
	public List<String> getNames() {
		return names;
	}
	public void setNames(List<String> names) {
		this.names = names;
	}

    @Override
    public boolean equals(Object aThat)
    {
        if (this == aThat) return true;
        if (!(aThat instanceof Interaction)) return false;

        Interaction that = (Interaction) aThat;
        if (this.actors.size() != that.getActors().size()) {
        	return false;
        }
        for (String actor:this.actors) {
        	if (!that.getActors().contains(actor)) {
        		return false;
        	}
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        for (String actor:this.actors) {
            hash = 17 * hash + Objects.hashCode(actor);
        }
        return hash;
    }

    @Override
    public float toFloat()
    {
        return this.names.size();
    }

	public void addName(String name) {
		if (names == null) {
			names = new ArrayList<String>();
		}
		names.add(name);
	}

}
