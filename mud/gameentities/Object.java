package gameentities;

public class Object extends Stuff {

	boolean isBreakable;
	boolean isTransportable;
	int     mass;

	public Object() {
		super();
		isBreakable     = false;
		isTransportable = false;
		mass            = 1;
		line            = "Some sort of object is here.";
		/* name.clear() ? */
		name.add("object");
	}

}
