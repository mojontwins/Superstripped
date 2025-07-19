package net.minecraft.src;

import java.util.Random;

public class MathHelper {
	private static float[] SIN_TABLE = new float[65536];

	public static final float sin(float f0) {
		return SIN_TABLE[(int) (f0 * 10430.378F) & 65535];
	}

	public static final float cos(float f0) {
		return SIN_TABLE[(int) (f0 * 10430.378F + 16384.0F) & 65535];
	}

	public static final float sqrt_float(float f0) {
		return (float) Math.sqrt((double) f0);
	}

	public static final float sqrt_double(double d0) {
		return (float) Math.sqrt(d0);
	}

	public static int floor_float(float f0) {
		int i1 = (int) f0;
		return f0 < (float) i1 ? i1 - 1 : i1;
	}

	public static int func_40346_b(double d0) {
		return (int) (d0 + 1024.0D) - 1024;
	}

	public static int floor_double(double d0) {
		int i2 = (int) d0;
		return d0 < (double) i2 ? i2 - 1 : i2;
	}

	public static long floor_double_long(double d0) {
		long j2 = (long) d0;
		return d0 < (double) j2 ? j2 - 1L : j2;
	}

	public static float abs(float f0) {
		return f0 >= 0.0F ? f0 : -f0;
	}

	public static int abs(int i0) {
		return i0 >= 0 ? i0 : -i0;
	}

	public static int clamp_int(int i0, int i1, int i2) {
		return i0 < i1 ? i1 : (i0 > i2 ? i2 : i0);
	}

	public static float clamp_float(float f0, float f1, float f2) {
		return f0 < f1 ? f1 : (f0 > f2 ? f2 : f0);
	}

	public static double abs_max(double d0, double d2) {
		if (d0 < 0.0D) {
			d0 = -d0;
		}

		if (d2 < 0.0D) {
			d2 = -d2;
		}

		return d0 > d2 ? d0 : d2;
	}

	public static int bucketInt(int i0, int i1) {
		return i0 < 0 ? -((-i0 - 1) / i1) - 1 : i0 / i1;
	}

	public static boolean stringNullOrLengthZero(String string0) {
		return string0 == null || string0.length() == 0;
	}

	public static int getRandomIntegerInRange(Random random0, int i1, int i2) {
		return i1 >= i2 ? i1 : random0.nextInt(i2 - i1 + 1) + i1;
	}

	public static double average(long[] par0ArrayOfLong) {
		long var1 = 0L;
		long[] var3 = par0ArrayOfLong;
		int var4 = par0ArrayOfLong.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			long var6 = var3[var5];
			var1 += var6;
		}

		return (double) var1 / (double) par0ArrayOfLong.length;
	}

	/**
	 * the angle is reduced to an angle between -180 and +180 by mod, and a 360
	 * check
	 */
	public static float wrapAngleTo180_float(float par0) {
		par0 %= 360.0F;

		if (par0 >= 180.0F) {
			par0 -= 360.0F;
		}

		if (par0 < -180.0F) {
			par0 += 360.0F;
		}

		return par0;
	}

	/**
	 * the angle is reduced to an angle between -180 and +180 by mod, and a 360
	 * check
	 */
	public static double wrapAngleTo180_double(double par0) {
		par0 %= 360.0D;

		if (par0 >= 180.0D) {
			par0 -= 360.0D;
		}

		if (par0 < -180.0D) {
			par0 += 360.0D;
		}

		return par0;
	}

	static {
		for (int i0 = 0; i0 < 65536; ++i0) {
			SIN_TABLE[i0] = (float) Math.sin((double) i0 * Math.PI * 2.0D / 65536.0D);
		}

	}

	public static double denormalizeClamp(double a, double b, double c) {
		return c < 0.0D ? a : (c > 1.0D ? b : a + (b - a) * c);
	}

	public static int ceiling_float_int(float var0) {
		int var1 = (int) var0;
		return var0 > (float) var1 ? var1 + 1 : var1;
	}

	public static int ceiling_double_int(double var0) {
		int var2 = (int) var0;
		return var0 > (double) var2 ? var2 + 1 : var2;
	}

	public static float wrapDegrees(float value) {
		value = value % 360.0F;

		if (value >= 180.0F) {
			value -= 360.0F;
		}

		if (value < -180.0F) {
			value += 360.0F;
		}

		return value;
	}

	public static int abs_int(int i) {
		return i >= 0 ? i : -i;
	}

	public static int floor(double value) {
		int i = (int) value;
		return value < (double) i ? i - 1 : i;
	}
}
