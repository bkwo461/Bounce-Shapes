/*
 *	===============================================================================
 *	PathType represents the type of a path
 *  YOUR UPI: bkwo461 - Shinbeom Kwon
 *	=============================================================================== */

enum PathType { BOUNCE, FALL;
	public PathType next() {
		return values()[(ordinal() + 1) % values().length];
	}
}
