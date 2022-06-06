package main.model;
public class Place implements java.io.Serializable{
	private int currentAmountOfTokens;
	private String name;
	private boolean initialPlace;

	private boolean finalPlace;
	
	public Place(String name, boolean initialPlace, boolean finalPlace ) {
		this.currentAmountOfTokens = 0;
		this.name = name;
		this.initialPlace = initialPlace;

		this.finalPlace = finalPlace;
	}


	public String getPlaceName()
	{
		return this.name;
	}
	
	public boolean isInitialPlace()
	{
		return this.initialPlace;
	}
	
	public boolean isFinalPlace()
	{
		return this.finalPlace;
	}
	
	public int getAmountOfTokens( )
	{
		return this.currentAmountOfTokens;
	}
	
	public void setAmountOfTokens( int currentAmountOfTokens )
	{
		this.currentAmountOfTokens = currentAmountOfTokens;
	}
	
	public void consumeToken( )
	{
		--this.currentAmountOfTokens;
	}
	
	
	public void addToken( )
	{
		++this.currentAmountOfTokens;
	}
	
	
	public void produceToken() {
		++this.currentAmountOfTokens;
	}

	@Override
	public String toString() {
		return
				"currentAmountOfTokens=" + currentAmountOfTokens ;
	}
}
