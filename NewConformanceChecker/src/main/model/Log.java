package main.model;
import java.util.List;

public class Log
{
	private List<Trace> traces;

	
	public Log(List<Trace> traces){
		this.traces = traces;
	}


	public List<Trace> getTraces( )
	{
		return traces;
	}


}
