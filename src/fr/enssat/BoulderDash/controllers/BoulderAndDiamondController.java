package fr.enssat.BoulderDash.controllers;

import fr.enssat.BoulderDash.models.BoulderModel;
import fr.enssat.BoulderDash.models.DiamondModel;
import fr.enssat.BoulderDash.models.EmptyModel;
import fr.enssat.BoulderDash.models.LevelModel;
import fr.enssat.BoulderDash.models.DisplayableElementModel;
import fr.enssat.BoulderDash.helpers.AudioLoadHelper;
import sun.jvm.hotspot.memory.Space;

/**
 * ElementPositionUpdateHelper
 *
 * Updates position of all elements displayed on the map, according to their
 * next potential position. Each object has a weight, which is used to compare
 * their power to destroy in the food chain. Sorry for that Darwinism.
 *
 * @author Colin Leverger <me@colinleverger.fr>
 * @since 2015-06-19
 */
public class BoulderAndDiamondController implements Runnable {
	private LevelModel levelModel;
    private AudioLoadHelper audioLoadHelper;
	private Thread elementMovingThread;

	/**
	 * Class constructor
	 *
	 * @param levelModel
	 *            Level model
	 */
	public BoulderAndDiamondController(LevelModel levelModel, AudioLoadHelper audioLoadHelper) {
		this.levelModel = levelModel;
        this.audioLoadHelper = audioLoadHelper;

        // Start thread
		this.elementMovingThread = new Thread(this);
		this.elementMovingThread.start();
	}

	/**
	 * Watches for elements to be moved
	 */
	public void run() {
		while (this.levelModel.isGameRunning()) {
			this.manageFallingObject();

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Scan the ground to detect the boulders & the diamonds, then make them
	 * fall if necessary
     * Note: scan of the ground upside down: we want things to fall slowly !
	 */
	private void manageFallingObject() {
		for (int x = this.levelModel.getSizeWidth() - 1; x >= 0; x--) {
			for (int y = this.levelModel.getSizeHeight() - 1; y >= 0; y--) {
				// Gets the spriteName of actual DisplayableElementModel object
				// scanned
				String spriteName = this.levelModel.getGroundLevelModel()[x][y].getSpriteName();
				
				// If it is a boulder or a diamond...
				if (spriteName == "boulder" || spriteName == "diamond") {
					this.manageFall(x, y);
				}
			}
		}
	}

	/**
	 * Manages the fall of elements
	 *
	 * @param  x  Horizontal position
	 * @param  y  Vertical position
	 */
	private void manageFall(int x, int y) {
		// Get informed about Rockford surroundings
        DisplayableElementModel elementAbove = this.levelModel.getGroundLevelModel()[x][y - 1];
        DisplayableElementModel elementBelow = this.levelModel.getGroundLevelModel()[x][y + 1];
        DisplayableElementModel elementLeft = this.levelModel.getGroundLevelModel()[x - 1][y];
        DisplayableElementModel elementRight = this.levelModel.getGroundLevelModel()[x + 1][y];

        String spriteNameAbove = elementAbove.getSpriteName();
        String spriteNameBelow = elementBelow.getSpriteName();
		String spriteNameLeft = elementLeft.getSpriteName();
		String spriteNameRight = elementRight.getSpriteName();

		// Then, process in case of the surrounding
		if (spriteNameBelow == "black") {
			this.levelModel.makeThisDisplayableElementFall(x, y);
		} else if (spriteNameBelow == "boulder") {
			// Boulders have to roll if they hit another boulder
			if (this.levelModel.getGroundLevelModel()[x - 1][y + 1].getSpriteName() == "black") {
				this.levelModel.makeThisBoulderSlideLeft(x, y);
			} else if (this.levelModel.getGroundLevelModel()[x + 1][y + 1].getSpriteName() == "black") {
				this.levelModel.makeThisBoulderSlideRight(x, y);
			}
		} else if (spriteNameBelow == "rockford" && this.levelModel.getGroundLevelModel()[x][y].isFalling()) {
			this.levelModel.exploseGround(x, y + 1);

            this.audioLoadHelper.playSound("die");

			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			this.levelModel.setGameRunning(false);
		} else if (spriteNameBelow == "magicwall") {
			if (this.levelModel.getGroundLevelModel()[x][y].getSpriteName() == "boulder") {
				if(this.levelModel.getGroundLevelModel()[x][y].isConvertible()) {
					this.levelModel.transformThisBoulderIntoADiamond(x, y);
				} else {
					this.levelModel.deleteThisBoulder(x, y);
				}
			}
		} else if (spriteNameLeft == "rockford" && this.levelModel.getRockford().isRunningRight() && this.levelModel.getGroundLevelModel()[x + 1][y].getSpriteName() == "black") {
			this.levelModel.moveThisBoulderToRight(x, y);
		} else if (spriteNameRight == "rockford" && this.levelModel.getRockford().isRunningLeft() && this.levelModel.getGroundLevelModel()[x - 1][y].getSpriteName() == "black") {
			this.levelModel.moveThisBoulderToLeft(x, y);
		} else {
			this.levelModel.getGroundLevelModel()[x][y].setFalling(false);
		}
	}
}
