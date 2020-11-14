/*
Copyright (C) Pplos Studio

    This file is a part of Amadey Logic Game, which based on CircuitJS1
    https://github.com/Pe3aTeJlb/Amadey-Logic-Game

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

public class Student {

	private int practiseCompleteCount;
	private int testScore;
	private int testAttemptCount;
	private double TestTime;
	
	public Student(){
		
	}
	
	public void AdjustPractiseCount() { practiseCompleteCount += 1;}
	
	public void setScore(int Score) {testScore = Score;}
	
	public void AdjustTestAttemptCount() {testAttemptCount += 1;}
	
	public void setTestTime(double time) {TestTime = time;}
	
}
