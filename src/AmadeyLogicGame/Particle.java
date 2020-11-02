package AmadeyLogicGame;

import java.util.ArrayList;

import com.google.gwt.canvas.dom.client.Context2d;

public class Particle {
	
	Context2d c;
	int x,y;
	ArrayList<Point> points = new ArrayList<Point>();
	int width, height;
	double sizeX, sizeY;
	
	Particle(Context2d cn, int w, int h) {
		
		c = cn;
		width = w;
		height = h;
		
		Point p = new Point(random(0,width/5),random(50,height-50));
		points.add(p);
		
		points.add(new Point(p.x+1,p.y));
		
	}
	
	public void doStep() {
		
		for(int i = 0; i< points.size()-1; i++) {
			
			points.get(points.size()-1).x += 4;
			
			  c.beginPath();
			  c.moveTo(points.get(i).x, points.get(i).y);
			  c.lineTo(points.get(i+1).x, points.get(i+1).y);
			  c.stroke();
			
		}
		
	}
	
	void Restart() {
		points.clear();
		Point p = new Point(random(50,width/2),random(50,height-50));
		points.add(p);
	}
	
  	private int random(int min, int max)
  	{
  		max -= min;
  		return (int) (Math.random() * ++max) + min;
  	}
  	
  	private boolean RandomOf100(int percent) {
  		
  		int max = 100;
  		
  		int p = (int) (Math.random() * ++max) + 0;
  		
  		if(p<=percent) {
  			return true;
  		}else {return false;}
  		
  	}

}
