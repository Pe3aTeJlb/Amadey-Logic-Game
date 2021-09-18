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

import AmadeyLogicGame.Util.IconsManager;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;

public class Gif {
	
	public String fileName;
	public int frameCount;
	public int currFrame, frameWidth;
	int width, height;
	public Image img;
	public boolean gifEnded = false;
	public boolean isPlaying = false;
	public int currX, currY, nextX, nextY;

	Gif(String file, int w, int h, int frameW, int fCount){

		fileName = file;
		
		width = w;
		height = h;
		frameWidth = frameW;
		
		currFrame = 0;
		frameCount = fCount;
		
		nextX = currX + frameWidth;

		img = IconsManager.getImage(fileName+ ".png");
		
	}

	public AnimationTimer timer = new AnimationTimer() {
		@Override
		public void handle(long now) {
			if (currX < width && currFrame < frameCount) {

				nextX += frameWidth;

			}else {
				timer.stop();
				gifEnded = true;
			}

			if(nextX == width) {
				nextX = 0;
				nextY += frameWidth+1;
			}

			currFrame += 1;

			currX = nextX;
			currY = nextY;
		}
	};

	
	public void Play() {
		
		if(!isPlaying) {
			isPlaying = true;
			gifEnded = false;
			timer.start();
		}
		
	}
	
	public void RestartGif(int fCount) {
		frameCount = fCount;
		currFrame = 0;
		
		currX = 0;
		currY = 0;
		
		nextX = currX + frameWidth;
		nextY = 0;
		
		isPlaying = false;
		gifEnded = false;
		
	}
	
}
