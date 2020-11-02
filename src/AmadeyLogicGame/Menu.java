/*    
Copyright (C) Pplos Studio
    
    This file is a part of Avrora Logic Game, which based on CircuitJS1
    https://github.com/Pe3aTeJlb/Avrora-logic-game
    
    CircuitJS1 was originally written by Paul Falstad.
	http://www.falstad.com/

	JavaScript conversion by Iain Sharp.
	http://lushprojects.com/
    
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

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Menu{

	static Menu menu;
	RootLayoutPanel root;
    LayoutPanel layoutPanel;
    DockLayoutPanel docklayoutPanel;
    VerticalPanel vp;
    
    Canvas cv;
    Context2d cvcontext;
    Canvas backcv;
    Context2d backcontext;
    double transform[];
    Rectangle circuitArea; 
    
    private final int REFRESH_RATE = 15;
    
    static CirSim mysim;
    
    int width,height;
    
    ArrayList<Particle> particles = new ArrayList<Particle>();
 
	Menu(){ menu = this;}
	
	public void setCanvasSize(){
		  
	  	width  = (int)RootLayoutPanel.get().getOffsetWidth();
	  	height = (int)RootLayoutPanel.get().getOffsetHeight();
  	
		if (cv != null) {
			cv.setWidth(width + "PX");
			cv.setHeight(height + "PX");
			cv.setCoordinateSpaceWidth(width);
			cv.setCoordinateSpaceHeight(height);
		}
		if (backcv != null) {
			backcv.setWidth(width + "PX");
			backcv.setHeight(height + "PX");
			backcv.setCoordinateSpaceWidth(width);
			backcv.setCoordinateSpaceHeight(height);
		}
		circuitArea = new Rectangle(0, 0, width, height);	
  }
	
	public void init() {
		
		transform = new double[6];
		
		Constants constants = (Constants) GWT.create(Constants.class);
		
		cv = Canvas.createIfSupported();
		if (cv==null) {
			RootPanel.get().add(new Label("Not working. You need a browser that supports the CANVAS element."));
			  return;
		}
		backcv = Canvas.createIfSupported();
		backcontext = backcv.getContext2d();
		
		root = RootLayoutPanel.get();
		layoutPanel = new LayoutPanel();
		vp = new VerticalPanel();
		DecoratorPanel dp = new DecoratorPanel();
		
		layoutPanel.add(cv);
		
		Button casual = new Button(constants.Casual(), new ClickHandler() {
		      public void onClick(ClickEvent event) {
		    	  
		    	  timer.cancel();
		    	  root.clear();
		    	  
		    	  mysim = new CirSim();
		    	  mysim.init("Pracrise");

		    	    Window.addResizeHandler(new ResizeHandler() {
		    	    	 
		                public void onResize(ResizeEvent event)
		                {               
		                	mysim.setCanvasSize();
		                }
		            });
		    	  
		      }
		    });
		    
		Button test = new Button(constants.Test(), new ClickHandler() {
		      public void onClick(ClickEvent event) {
		    	  
		    	  timer.cancel();
		    	  root.clear();
		    	  
		    	  mysim = new CirSim();
		    	  mysim.init("Test");

		    	    Window.addResizeHandler(new ResizeHandler() {
		    	    	 
		                public void onResize(ResizeEvent event)
		                {               
		                	mysim.setCanvasSize();
		                }
		            });
		    	  
		      }
		    });
		 
		Button rules = new Button(constants.Rules(), new ClickHandler() {
		      public void onClick(ClickEvent event) {
					AboutBox b = new AboutBox(constants.Rules());
					dp.add(b.vp);
		      }
		    });
		 
		casual.setStyleName("menuButton");
		test.setStyleName("menuButton");
		rules.setStyleName("menuButton");
		
		vp.add(casual);
		vp.add(test);
		vp.add(rules);
	
		vp.setCellHorizontalAlignment(casual, HasHorizontalAlignment.ALIGN_JUSTIFY);
		vp.setCellHorizontalAlignment(test, HasHorizontalAlignment.ALIGN_JUSTIFY);
		vp.setCellHorizontalAlignment(rules, HasHorizontalAlignment.ALIGN_JUSTIFY);
		vp.setStyleName("vpTable");
		
		dp.add(vp);
		
		
		layoutPanel.add(dp);
		layoutPanel.setWidgetLeftRight(dp, 30, Style.Unit.PCT, 30, Style.Unit.PCT);
		layoutPanel.setWidgetTopBottom(dp, 30, Style.Unit.PCT, 30, Style.Unit.PCT);
		
	    root.add(layoutPanel);
	    
	    setCanvasSize();	
	    
	    for(int i = 0; i < 5; i++) {
	    	
	    	Particle p = new Particle(backcontext, width, height);
	    	particles.add(p);
	    }
	    
	    timer.scheduleRepeating(REFRESH_RATE);
	   
	}

	
	final Timer timer = new Timer() {
	      public void run() {
	       Update();
	      }
	 };
	
	public void Update() {
		
		setCanvasSize();
	
		Graphics g = new Graphics(backcontext);
		g.setColor(Color.black);
		g.fillRect(0, 0, g.context.getCanvas().getWidth(), g.context.getCanvas().getHeight());
		
		//g.setColor(Color.green);
		
		//for(int i = 0; i < particles.size(); i++) {
			//particles.get(i).doStep();
		//}
					
		cv.getContext2d().drawImage(backcontext.getCanvas(), 0.0, 0.0);
	}

  	private int random(int min, int max)
  	{
  		max -= min;
  		return (int) (Math.random() * ++max) + min;
  	}
	
}
