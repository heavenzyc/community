/**
 * @(#)BigDecimalUtils.java
 * Copyright 2013 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.utils;

import java.math.BigDecimal;

/**
 * @author xu.jianguo
 * @date 2013-8-9 description
 */
public class BigDecimalUtils {
	private static final String[] CAPITAL = { "零", "壹", "贰", "叁", "肆", "伍",
			"陆", "柒", "捌", "玖" };

	/**
	 * 将金额转换为大写<br/>
	 * 支持一万亿以内的<br/>
	 * @param value
	 * @return
	 */
	public static String capitalize(BigDecimal value) {
		StringBuffer buffer = new StringBuffer();
		value = value.setScale(2, BigDecimal.ROUND_HALF_UP);
		String valueString = String.valueOf(value);
	 
		int dot = valueString.indexOf(".");
		String value0 = valueString.substring(0, dot);
		String value1 = valueString.substring(dot + 1);
		if (!value0.equals("0"))
			buffer.append(convert(Long.valueOf(value0).longValue()))
					.append("圆");
		if (!value1.equals("00")) {
			char c0 = value1.charAt(0);
			char c1 = value1.charAt(1);
			buffer.append(CAPITAL[Integer.parseInt(String.valueOf(c0))]);
			if (c0 != '0')
				buffer.append("角");
			if (c1 != '0') {
				buffer.append(CAPITAL[Integer.parseInt(String.valueOf(c1))]);
				buffer.append("分");
			}
		}
		return buffer.toString();
	}
 

	/**
	 * 转换为大写。@see {@link #capitalize(java.math.BigDecimal)}
	 * 
	 * @param value
	 * @return
	 */
	public static String convert(long value) {
		assertWholeNumber(value);
		Position[][] positions = createPosition(value);
		if (positions == null)
			return null;
		StringBuffer buffer = new StringBuffer();
		for (int i = positions.length - 1; i >= 0; i--) {
			Position[] positionArray = positions[i];
			buffer.append(createGroup(positionArray));
			if (i == 1)
				buffer.append("万");
			if (i == 2)
				buffer.append("亿");
		}
		while (buffer.charAt(0) == '零')
			buffer.deleteCharAt(0);
		return buffer.toString();
	}
	
	private static void assertWholeNumber(long value){
		if(value>999999999999L){
			throw new RuntimeException("整数部分不能大于一万亿");
		}
	}
	private static Position[][] createPosition(long value) {
		Position[][] positions = null;
		String valueString = String.valueOf(value);
		int length = valueString.length();
		int group = length / 4;
		int residue = length % 4;
		if (residue != 0)
			group++;
		positions = new Position[group][4];
		for (int i = length - 1; i >= 0; i--) {
			char c = valueString.charAt(i);
			int intValue = Integer.parseInt(String.valueOf(c));
			int index = (length - 1 - i) % 4;
			positions[(length - 1 - i) / 4][3 - index] = new Position(intValue,
					index);
		}
		return positions;
	}

	private static String calculateUnit(int index) {
		String result = "";
		int residue = (index + 1) % 4;
		if (residue == 2)
			result = "拾";
		else if (residue == 3)
			result = "佰";
		else if (residue == 0)
			result = "仟";
		return result;
	}

	private static String createGroup(Position[] positions) {
		if (positions == null)
			return null;
		StringBuffer buffer = new StringBuffer();
		boolean appendZore = false;
		for (int i = 0; i < positions.length; i++) {
			Position current = positions[i];
			if (current == null) {
				buffer.append(CAPITAL[0]);
				continue;
			}
			int currentValue = current.getValue();
			if (currentValue != 0) {
				buffer.append(CAPITAL[currentValue]).append(
						calculateUnit(current.getIndex()));
				appendZore = false;
			} else {
				for (int j = i + 1; j < positions.length; j++) {
					if (positions[j].getValue() != 0 && !appendZore) {
						buffer.append(CAPITAL[0]);
						appendZore = true;
						break;
					}
				}
			}
		}
		return buffer.toString();
	}

	static class Position {
		private int value;

		private int index;

		public Position(int value, int index) {
			this.value = value;
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + index;
			result = PRIME * result + value;
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Position other = (Position) obj;
			if (index != other.index)
				return false;
			if (value != other.value)
				return false;
			return true;
		}
	}
 
}
