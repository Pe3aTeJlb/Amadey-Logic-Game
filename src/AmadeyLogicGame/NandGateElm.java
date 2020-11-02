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

    class NandGateElm extends AndGateElm {
    	
	public NandGateElm(int xx, int yy) { super(xx, yy); }
	
	public NandGateElm(int xa, int ya, int xb, int yb, int f,int inputcount) {
	    super(xa, ya, xb, yb, f, inputcount);
	}
	
	boolean isInverting() { return true; }
	
	String getGateName() { return "NAND gate"; }
		
}