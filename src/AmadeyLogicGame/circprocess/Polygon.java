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

class Polygon {
	
	private static final int MIN_LENGTH = 4;
	public int npoints;
	public int[] xpoints;
	public int[] ypoints;

	public Polygon(){
        xpoints = new int[MIN_LENGTH];
        ypoints = new int[MIN_LENGTH];
	}
	
    public void addPoint(int x, int y) {
    	
        if (npoints >= xpoints.length || npoints >= ypoints.length) {
            int newLength = npoints * 2;
            // Make sure that newLength will be greater than MIN_LENGTH and
            // aligned to the power of 2
            if (newLength < MIN_LENGTH) {
                newLength = MIN_LENGTH;
            } else if ((newLength & (newLength - 1)) != 0) {
                newLength = Integer.highestOneBit(newLength);
            }

            xpoints = expand(xpoints, newLength);
            ypoints = expand(ypoints, newLength);
        }
        xpoints[npoints] = x;
        ypoints[npoints] = y;
        npoints++;
    }
    
    private int[] expand(int[] in, int newlen) {
    	int[] out=new int[newlen];
    	
    	for(int i=0; i<in.length; i++) {
    		out[i]=in[i];
    	}
    	
    	return out;
    }
	
}