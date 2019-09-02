package team.reborn.energy;

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
