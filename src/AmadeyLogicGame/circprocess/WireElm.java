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

package AmadeyLogicGame.circprocess;

import AmadeyLogicGame.Graphics;
import AmadeyLogicGame.circprocess.CircuitElm;

public class WireElm extends CircuitElm {

	boolean hasWireInfo; // used in CirSim to calculate wire currents

	public WireElm(int xa, int ya, int xb, int yb, int f) {
	    super(xa, ya, xb, yb, f);
	}
	
	static final int FLAG_SHOWCURRENT = 1;
	
	static final int FLAG_SHOWVOLTAGE = 2;

	public void draw(Graphics g) {
		
	    setVoltageColor(g, volts[0]);
	    drawThickLine(g, point1, point2);
	    //doDots(g);
	    setBbox(point1, point2, 3);
	    String s = "";

	    if (mustShowCurrent()) {
	        s = getShortUnitText(Math.abs(getCurrent()), "A");
	    } 
	    if (mustShowVoltage()) {
	        s = (s.length() > 0 ? s + " " : "") + getShortUnitText(volts[0], "V");
	    }

	    drawValues(g, s, 4);
	    //drawPosts(g);
	}
	
	void stamp() {
	    //sim.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
	}
	
	boolean mustShowCurrent() {
	    return (flags & FLAG_SHOWCURRENT) != 0;
	}
	
	boolean mustShowVoltage() {
	    return (flags & FLAG_SHOWVOLTAGE) != 0;
	}
		
	double getPower() { return 0; }
	
	double getVoltageDiff() { return volts[0]; }
	
	boolean isWire() { return true; }
	
}