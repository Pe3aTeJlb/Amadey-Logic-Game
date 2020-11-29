/*    
Copyright (C) Paul Falstad and Iain Sharp
	Modified by Pplos Studio
    
   This file is a part of Amadey Logic Game, which based on CircuitJS1
    https://github.com/Pe3aTeJlb/Amadey-Logic-Game
    
    CircuitJS1 was originally written by Paul Falstad.
	http://www.falstad.com/
	https://github.com/pfalstad/circuitjs1

	JavaScript conversion by Iain Sharp.
	http://lushprojects.com/
	https://github.com/sharpie7/circuitjs1
    
    Avrora Logic Game is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 1, 2 of the License, or
    (at your option) any later version.
    Avrora Logic Game is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License 
    along with Avrora Logic Game.  If not, see <http://www.gnu.org/licenses/>.
*/

package AmadeyLogicGame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Graphics {
	
	GraphicsContext context;
	int currentFontSize;
	Font currentFont = null;
	Color lastColor;
	
	  public Graphics(GraphicsContext context) {
		    this.context = context;
	}
	  
	  public void setColor(Color color) {

		    if (color != null) {
		      context.setStroke(color);
		      context.setFill(color);
		    } else {
		      //System.out.println("Ignoring null-Color");
		    }
		    lastColor=color;

	  }

	  public void fillRect(int x, int y, int width, int height) {
		//  context.beginPath();
		  context.fillRect(x, y, width, height);
		//  context.closePath();
	  }
	  
	  public void fillOval(double x, double y, double width, double height) {
		  context.fillOval(x,y,width,height);
	  }
	  
	  public void drawString(String s, int x, int y){
		//  context.beginPath();
		  context.fillText(s, x, y);
		//  context.closePath();
	  }
	  
	  public void setLineWidth(double width){
		  context.setLineWidth(width);
	  }
	  
	  public void drawLine(int x1, int y1, int x2, int y2) {
		  context.beginPath();
		  context.moveTo(x1, y1);
		  context.lineTo(x2, y2);
		  context.stroke();
	//	  context.closePath();
	  }
	  
	  public void drawPolyline(int[] xpoints, int[] ypoints, int n) {
		  int i;
		  context.beginPath();
		  for (i=0; i<n;i++){
			  if (i==0)
				  context.moveTo(xpoints[i],ypoints[i]);
			  else
				  context.lineTo(xpoints[i],ypoints[i]);
		  }
		  context.closePath();
		  context.stroke();
	  }
	  
	  public void setFont(Font f){
		  if (f!=null){
			  context.setFont(f);
			  currentFontSize=(int)f.getSize();
			  currentFont=f;
		  }
	  }
	  
	  Font getFont(){
		  return currentFont;
	  }
	  
	  static int distanceSq(int x1, int y1, int x2, int y2) {
	    	x2 -= x1;
	    	y2 -= y1;
	    	return x2*x2+y2*y2;
	   }
	   
}