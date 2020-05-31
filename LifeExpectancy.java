package selfmaptest;
//Java utilities libraries
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;
											// Visualizes life expectancy in different countries. 
/* It loads the country shapes from a GeoJSON file via a data reader, and loads the population density values from
another CSV file (provided by the World Bank). The data value is encoded to transparency via a simplistic linear  mapping. 
 
 */


public class LifeExpectancy extends PApplet {
	HashMap<String, Float> lifeExpMap;
	List<Feature> countries;	
	List<Marker> countryMarkers;
	private static final boolean offline =true;	//If you are working offline set offline to true
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	UnfoldingMap map;	//Unfolding map Library
	public void setup()
	{
		size(1000,600,OPENGL);	//size of the Applet Window
		if (offline) {	//If you are working offline 		
		    map = new UnfoldingMap(this, 250, 50, 700, 500, new MBTilesMapProvider(mbTilesString));	//for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());	//Constructor of UnfoldingMap Library
							//(pointer,   x,   y,width,height,   MapProviser)
			//working online 
		}
		 map.zoomToLevel(2);	//To set the Zoom level at which the Map will be zoomed and displayed in the applet
		MapUtils.createDefaultEventDispatcher(this, map);	//default constructor in Unfolding Map Library to deal with the users interaction with the Map
		lifeExpMap = ParseFeed.loadLifeExpectancyFromCSV(this,"LifeExpectancyWorldBank.csv"); //Loading data from LifeExpectancy Column Separated Values file
		countries = GeoJSONReader.loadData(this, "countries.geo.json");	//Fetching Countries id from countries.geo.json file
		countryMarkers = MapUtils.createSimpleMarkers(countries);	//creating markers for each countries and storing in the list countryMarkers
		map.addMarkers(countryMarkers);	//Adding each countries markers to the map
		shadeCountries();	//Helper Method to give shade to each country based on their LifeExpectancy Values
		
	}
	public void draw()	//Called Continuously
	{
		map.draw();		//Draw the map in the Applet 
		addkey();		//Helper Method to display rectangular box with keys to Understand the data 
	}
	
	//Helper method to color each country based on life expectancy
	//Red-orange indicates low (near 40)
	//Blue indicates high (near 100)
		private void shadeCountries() {		
		for (Marker marker : countryMarkers) {
			// Find data for country of the current marker
			String countryId = marker.getId();
			System.out.println(lifeExpMap.containsKey(countryId));	//To check if the countryId is present or not
			if (lifeExpMap.containsKey(countryId)) {		//If it is present then assign a color to it
				float lifeExp = lifeExpMap.get(countryId);	//Fetching the LifeExpectancy value from CSV file
				// Encode value as brightness (values range: 40-90)
				int colorLevel = (int) map(lifeExp, 40, 90, 10, 255);	//The LifeExpectancy Rate Range(40-90) is encoded(converted) to color values
				// Converting it to a range between(10,255)i.e 10-lowest and 255-Highest(max) value
				marker.setColor(color(255-colorLevel, 100, colorLevel)); //Set the color(R,G,B) 
				//The lower the LifeExpectancy Rate the Higher the Red value and lower the Blue value
				//Green value is fixed at 100
				//The Higher the LifeExpectancy Rate the Higher the Blue value and lower the Red value
			}
			else {		//If that country id is not found in CSV File or data is unavailable then shade that country to default color grey
				marker.setColor(color(150,150,150));	//code to set the color as grey for that Country
			}
		}
	}
	private void addkey()	//Helper Method to Draw key in GUI
	{
		fill(250,250,240);				//Set the color as RGB value(250,250,240)
		rect(20,50,200,225);			//to draw a rectangle starting at coordinate(x,y)=(20,50) and width=200 and height=225
		fill(0);						//set the color to black
		text("Life Expectancy Map",25,75);	//To display the text at coordinates x=25,y=75
		fill(240,100,100);					//To set the color as dark red
		rect(30,125,25,25);
		fill(180,140,255);					//To set the color as purple
		rect(30,175,25,25);
		fill(10,100,255);					//To set the color as Dark Blue 
		rect(30,225,25,25);
		fill(0);					
		text("Below 45 Years",100,140);			//To display the text at coordinates x=100 and y=140
		text("Above 45 Years",100,190);
		text("Above 70 Years",100,240);
	}

}
