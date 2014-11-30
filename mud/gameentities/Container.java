package gameentities;

import java.util.List;
import java.util.LinkedList;

public class Container extends Object {

	public int massLimit;
	public List<Stuff> contents = new LinkedList<Stuff>();

	public Container() {
		super();
		massLimit = 10;
		line            = "some sort of container is here";
		/* name.clear() ? */
		/*name.add("container");*/
		name = "container";
	}

}
