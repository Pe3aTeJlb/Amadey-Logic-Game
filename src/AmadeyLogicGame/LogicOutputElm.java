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

import javafx.scene.text.Font;

class LogicOutputElm extends CircuitElm {

	final int FLAG_PULLDOWN = 4;
	double threshold;
	public String value;
	public Point InputPoint = new Point();
	
	public LogicOutputElm(int xa, int ya, int xb, int yb, int f) {
	    super(xa, ya, xb, yb, f);
	    InputPoint.x = xa;
	    InputPoint.y = ya;
		threshold = 2.5;
	}
	
	int getPostCount() { return 1; }
	
	boolean needsPullDown() { return (flags & FLAG_PULLDOWN) != 0; }
	
	void setPoints() {
	    super.setPoints();
	    lead1 = interpPoint(point1, point2, 1-12/dn);
	}
	
	void draw(Graphics g) {

		Font oldf = g.getFont();
		Font f = new Font("SansSerif",20);
	    g.setFont(f);
	    //g.setColor(needsHighlight() ? selectColor : lightGrayColor);
	    g.setColor(lightGrayColor);
	    String s = (volts[0] < threshold) ? "L" : "H";
	    
		s = (volts[0] < threshold) ? "0" : "1";
	    value = s;
	    
	    setBbox(point1, lead1, 0);
	    drawCenteredText(g, s, x2, y2, true);
	    setVoltageColor(g, volts[0]);
	    drawThickLine(g, point1, lead1);
	    drawPosts(g);
	    g.setFont(oldf);
	}
	
	void stamp() {
	    if (needsPullDown())
		sim.stampResistor(nodes[0], 0, 1e6);
	}
	
	double getVoltageDiff() { return volts[0]; }
	
}
