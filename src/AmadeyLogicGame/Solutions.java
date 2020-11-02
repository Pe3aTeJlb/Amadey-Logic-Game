/*    
Copyright (C) Luis Paulo Laus
    
    This file is a part of Avrora Logic Game, which based on CircuitJS1
    https://github.com/Pe3aTeJlb/Avrora-logic-game
    
    JQM Java Quine McCluskey
    https://sourceforge.net/projects/jqm-java-quine-mccluskey/
    
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

public class Solutions {

    ArrayList<Implicant> essentialsPI;
    ArrayList<ArrayList<Implicant>> primeI;
    
    Solutions(){
        essentialsPI = null;
        primeI = null;
    }

    public void setEssentialPI(ArrayList<Implicant> essentialsPI) {
        this.essentialsPI = essentialsPI;
    }

    public void setPIsize(int s) {
        primeI = new ArrayList <>(s);
    }

    public boolean addSolution(int size) {
        return primeI.add(new ArrayList<>(size));
    }

    boolean addPI(int lst, Implicant p){
        return primeI.get(lst).add(p);
    }

    public ArrayList<Implicant> getEssentialsPI(){
        return essentialsPI;
    }

    public ArrayList <ArrayList<Implicant>> getPrimeI(){
        return primeI;
    }
}