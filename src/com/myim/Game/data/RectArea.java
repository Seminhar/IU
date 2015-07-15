package com.myim.Game.data;

import android.graphics.RectF;


public class RectArea {

    // 最小X坐标
	public int mMinX;

    // 最小Y坐标
	public int mMinY;

    // 最大X坐标
	public int mMaxX;

    // 最大Y坐标
	public int mMaxY;

	public RectF getRectF() {
		return new RectF(mMinX, mMinY, mMaxX, mMaxY);
	}

	public RectArea(int minX, int minY, int size) {
		this.mMinX = minX;
		this.mMinY = minY;
		this.mMaxX = minX + size;
		this.mMaxY = minY + size;
	}

	public RectArea(int minX, int minY, int maxX, int maxY) {
		this.mMinX = minX;
		this.mMinY = minY;
		this.mMaxX = maxX;
		this.mMaxY = maxY;
	}
}
