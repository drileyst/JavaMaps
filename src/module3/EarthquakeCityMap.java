package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;
import processing.core.PShape;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Daniel Riley
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
    
    int[] marker_colors;
	
	public void setup() {
		size(950, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new OpenStreetMap.OpenStreetMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
	    List<Marker> markers = new ArrayList<Marker>();

	    //Earthquake locations
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    //Marker colors
	    int cyan = color(0,255,255);
	    int yellow = color(255, 255, 0);
	    int red = color(255,0,0);
	    marker_colors= new int[]{cyan,yellow,red};
	    for(int i = 0; i < earthquakes.size();i++)
	    {
	    	float mag = Float.parseFloat(earthquakes.get(i).getProperty("magnitude").toString());
	    	markers.add(createMarker(earthquakes.get(i),mag));
	    }
	    map.addMarkers(markers);

	    
	}
		

	private SimplePointMarker createMarker(PointFeature feature,float mag)
	//assigns each marker a color and size based on the magnitude of its earthquake
	{
		SimplePointMarker markReturn = new SimplePointMarker(feature.getLocation());
		if(mag < 4.0)
		{
			markReturn.setRadius(5);
			markReturn.setColor(marker_colors[0]);
		}
		else 
			if(mag<5.0)
			{
				markReturn.setColor(marker_colors[1]);
				markReturn.setRadius(9);
			}
			else
			{
				markReturn.setColor(marker_colors[2]);
				markReturn.setRadius(13);
			}
		return markReturn;
	}
	
	public void draw() 
	//display map,legend
	{
	    background(color(128,128,128));
	    map.draw();
	    addKey(marker_colors);
	}


	// helper method to draw key in GUI
	private void addKey(int[] marker_colors) 
	{   //text("word", 12, 45, -30);
		fill(color(255,255,255));
		rect(0,0,200,600);
		String[] key_strings = new String[]{"Magnitude < 4.0","4.0 < Magnitude < 5.0","Magnitude > 5.0"};
		for(int i=0;i<3;i++)
		{
			fill(color(0,0,0));
			text(key_strings[i],20,20 + 20*i,500,40 + 20*i);
			fill(marker_colors[i]);
			ellipse(170,28+i*20,5+4*i,5+4*i);
		}
	
	}
}
