package sblectricbot.util;

/** Permission levels */
public enum PermissionLevel {
	BROADCASTER, MODERATOR, VIEWER;
	
	public static final PermissionLevel DEFAULT = VIEWER;
	
	/** Compare permission levels */
	public boolean isAtLeast(PermissionLevel toCompare) {
		if(this == BROADCASTER) return true;
		if(toCompare == BROADCASTER) return false;
		if(this == MODERATOR) return true;
		if(toCompare == MODERATOR) return false;
		return true;
	}
	
	/** Convert from a string */
	public static PermissionLevel fromString(String perms) {
		for(PermissionLevel p : values()) {
			if(p.toString().equals(perms)) return p;
		}
		return DEFAULT;
	}
	
}
