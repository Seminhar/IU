package com.myim.Game.data;

import android.graphics.RectF;


public class ColorData {

    // 最小X坐标
	public int mMinX;

    // 最小Y坐标
	public int mMinY;

    // 最大X坐标
	public int mMaxX;

    // 最大Y坐标
	public int mMaxY;

    // 背景颜色
	private int mBgColor;
    // 字体颜色
	private int mTextColor;
    // 文本
	private int mText;

	public RectF getRectF() {
		return new RectF(mMinX, mMinY, mMaxX, mMaxY);
	}

	public int getMBgColor() {
		return mBgColor;
	}

	public void setMBgColor(int bgColor) {
		mBgColor = bgColor;
	}

	public int getMTextColor() {
		return mTextColor;
	}

	public void setMTextColor(int textColor) {
		mTextColor = textColor;
	}

	public int getMText() {
		return mText;
	}

	public void setMText(int text) {
		mText = text;
	}
}
