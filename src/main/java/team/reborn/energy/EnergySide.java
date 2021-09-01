package team.reborn.energy;

/**
 * @deprecated Use the new energy API instead. (Under the team/reborn/energy/api package).
 */
@Deprecated(forRemoval = true)
public enum  EnergySide {
	DOWN,
	UP,
	NORTH,
	SOUTH,
	WEST,
	EAST,
	UNKNOWN;

	private static final EnergySide[] VALUES = values();

	public static EnergySide fromMinecraft(Enum<?> e){
		if(e == null){
			return UNKNOWN;
		}
		return VALUES[e.ordinal()];
	}
}
