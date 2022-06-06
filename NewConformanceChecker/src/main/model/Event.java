package main.model;
public class Event {
	private String name;

	public Event(String name){
		this.name = name;
	}


	public String getEventName( )
	{
		return this.name;
	}

	@Override
	public String toString() {
		return name;
	}


}
