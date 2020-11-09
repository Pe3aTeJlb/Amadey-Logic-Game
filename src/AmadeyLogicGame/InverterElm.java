/*    
Copyright (C) Paul Falstad and Iain Sharp
	Modified by Pplos Studio
    
    This file is a part of Avrora Logic Game, which based on CircuitJS1
    https://github.com/Pe3aTeJlb/Avrora-logic-game
    
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

class InverterElm extends CircuitElm {
    	
	double slewRate; // V/ns
	
	double highVoltage;
	
	double timeStep = 5e-6;
	
	public InverterElm(int xx, int yy) {
	    super(xx, yy);
	    noDiagonal = true;
	    slewRate = .5;
	    
	    // copy defaults from last gate edited
	    highVoltage = GateElm.lastHighVoltage;
	}
	
	public InverterElm(int xa, int ya, int xb, int yb, int f) {
		
	    super(xa, ya, xb, yb, f);
	    noDiagonal = true;
	    slewRate = .5;
	    highVoltage = 5;
	    
	}
	
	Point center;
	
	void draw(Graphics g) {
	    //drawPosts(g);
	    draw2Leads(g);
	    g.setColor(needsHighlight() ? selectColor : lightGrayColor);
	    drawThickPolygon(g, gatePoly);
	    if (GateElm.useEuroGates())
		drawCenteredText(g, "1", center.x, center.y-6, true);
	    drawThickCircle(g, pcircle.x, pcircle.y, 3);
	    curcount = updateDotCount(current, curcount);
	    //drawDots(g, lead2, point2, curcount);
	}
	
	Polygon gatePoly;
	Point pcircle;
	
	void setPoints() {
	    super.setPoints();
	    int hs = 16;
	    int ww = 16;
	    if (ww > dn/2)
		ww = (int) (dn/2);
	    lead1 = interpPoint(point1, point2, .5-ww/dn);
	    lead2 = interpPoint(point1, point2, .5+(ww+2)/dn);
	    pcircle = interpPoint(point1, point2, .5+(ww-2)/dn);
	    
	    if (GateElm.useEuroGates()) {
		Point[] pts = newPointArray(4);
		Point l2 = interpPoint(point1, point2, .5+(ww-5)/dn);   // make room for circle
		interpPoint2(lead1, l2, pts[0], pts[1], 0, hs);
		interpPoint2(lead1, l2, pts[3], pts[2], 1, hs);
		gatePoly = createPolygon(pts);
		center = interpPoint(lead1, l2, .5);
	    } else {
		Point[] triPoints = newPointArray(3);
		interpPoint2(lead1, lead2, triPoints[0], triPoints[1], 0, hs);
		triPoints[2] = interpPoint(point1, point2, .5+(ww-5)/dn);
		gatePoly = createPolygon(triPoints);
	    }
	    setBbox(point1, point2, hs);
	}
	
	int getVoltageSourceCount() { return 1; }
	
	void stamp() {
	    sim.stampVoltageSource(0, nodes[1], voltSource);
	}
	
	void doStep() {
		
	    double v0 = volts[1];
	    double out = volts[0] >= highVoltage*.5? 0 : highVoltage;
	    double maxStep = slewRate * timeStep * 1e9;
	    out = Math.max(Math.min(v0+maxStep, out), v0-maxStep);
	    sim.updateVoltageSource(0, nodes[1], voltSource, out);
	}
	
	double getVoltageDiff() { return volts[0]; }
	
	// there is no current path through the inverter input, but there
	// is an indirect path through the output to ground.
	boolean getConnection(int n1, int n2) { return false; }
	
	boolean hasGroundConnection(int n1) {
	    return (n1 == 1);
	}
	
	@Override double getCurrentIntoNode(int n) {
	    if (n == 1)
		return current;
	    return 0;
	}

}