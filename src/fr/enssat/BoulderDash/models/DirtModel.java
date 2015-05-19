package fr.enssat.BoulderDash.models;

import fr.enssat.BoulderDash.bridges.PublisherBridge;

public class DirtModel extends ElementDisplayableModel implements PublisherBridge {

	private static String pathToSprite = "insert/path/down/here";
	private static boolean isDestructible = true;
	private static boolean canMove = false;
	private static boolean impactExplosive = false;
	private static boolean animate = false;
	private static int priority = 3;

	public DirtModel(int x, int y) {
		super(isDestructible, canMove, x, y, pathToSprite, priority,
				impactExplosive, animate);
	}
}
