package com.mrnobody.morecommands.util;

import net.minecraft.util.ChunkCoordinates;

/**
 * A wrapper for coordinates
 * 
 * @author MrNobody98
 */
public class Coordinate {
	private final double x;
	private final double y;
	private final double z;
	
	public Coordinate(ChunkCoordinates coord) {
		this.x = coord.posX;
		this.y = coord.posY;
		this.z = coord.posZ;
	}
   
	public Coordinate(net.minecraft.entity.Entity entity) {
		this(entity.posX, entity.posY, entity.posZ);
	}
	
	public Coordinate(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Coordinate(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
   
	/**
	 * @return The x coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return The block x coordinate
	 */
	public int getBlockX() {
		int x = (int) getX();
		return getX() < (double) x ? x - 1 : x;
	}

	/**
	 * @return The y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return The block y coordinate
	 */
	public int getBlockY() {
		return (int) getY();
	}

	/**
	 * @return The z coordinate
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @return The block z coordinate
	 */
	public int getBlockZ() {
		int z = (int) getZ();
		return getZ() < (double)z ? z - 1 : z;
	}

	/**
	 * @return The distance between two coordinates
	 */
	public double getDistanceBetweenCoordinates(Coordinate compare) {
		double diffX = Math.abs(getX() - compare.getX());
		double diffY = Math.abs(getY() - compare.getY());
		double diffZ = Math.abs(getZ() - compare.getZ());
		return Math.sqrt((diffX * diffX) + (diffY * diffY) + (diffZ * diffZ));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Coordinate) {
			Coordinate compare = (Coordinate)obj;
			return compare.getX() == getX() && compare.getY() == getY() && compare.getZ() == getZ();
		}
		return false;
	}

	@Override
	public String toString() {
		return x + "," + y + "," + z;
	}
}
