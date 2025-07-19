package com.mojontwins.noiseresearch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import net.minecraft.src.NoiseGeneratorOctaves2;

public class NoiseResearch {

	static NoiseGeneratorOctaves2 temperatureNoise;
	static NoiseGeneratorOctaves2 humidityNoise;
	static float[] temperature;
	static float[] humidity;
	
	public static void main(String[] args) {
		long seed = 329084029348L;
		
		temperatureNoise = new NoiseGeneratorOctaves2(new Random(seed * 9871L), 4);
		humidityNoise = new NoiseGeneratorOctaves2(new Random(seed * 39811L), 4);
		
		int w = 256;
		int h = 256;
		
		BufferedImage bi = new BufferedImage(w, 2 * h, BufferedImage.TYPE_INT_ARGB);

		int cx = 00, cz = 00;
		double tminValue = 0.0D;
		double tmaxValue = 0.0D;
		double hminValue = 0.0D;
		double hmaxValue = 0.0D;
		System.out.println ("Running " + (cx * cz) + " times to get min/max");
		System.out.flush();
		
		for(int xx = 0; xx < cx; xx ++) {
			System.out.print(".");
			for(int zz = 0; zz < cz; zz ++) {
				temperature = temperatureNoise.generateNoiseOctaves(temperature, xx, zz, w, h, 0.15, 0.15, 0.7);
				humidity = humidityNoise.generateNoiseOctaves(humidity, xx, zz, w, h, 0.3, 0.3, 0.7);
				
				int index = 0; 
				for(int x = 0; x < w; x ++) {
					for(int z = 0; z < h; z ++) {
						double tem = temperature[index];
						double hum = humidity[index];
						
						// Two decimals
						tem = (Math.floor(tem * 100)) / 100;
						hum = (Math.floor(hum * 100)) / 100;
						
						if(tem < tminValue) tminValue = tem;
						if(tem > tmaxValue) tmaxValue = tem;
						if(hum < hminValue) hminValue = hum;
						if(hum > hmaxValue) hmaxValue = hum;
						
						index ++;
					}
				}
			}
		}
		System.out.println();
		System.out.println ("T " + tminValue + "-" + tmaxValue + "; H " + hminValue + "-" + hmaxValue);

		temperature = temperatureNoise.generateNoiseOctaves(temperature, 100, 100, w, h, 0.25, 0.25, 0.7);
		humidity = humidityNoise.generateNoiseOctaves(humidity, 100, 100, w, h, 0.5, 0.5, 0.7);
			
		int index = 0;
		for(int x = 0; x < w; x ++) {
			for(int z = 0; z < h; z ++) {
				double tem = temperature[index];
				double hum = humidity[index];
				
				double tt = tem / 7;
				if(tt < -1) tt = -1;
				if(tt > 1) tt = 1;
				
				int tc = (int)((tt + 1) * 127);
								
				double hh = hum / 7;
				if(hh < -1) hh = -1;
				if(hh > 1) hh = 1;
				
				int hc = (int)((hh + 1) * 128);
				
				// Two decimals
				tem = (Math.floor(tem * 100)) / 100;
				hum = (Math.floor(hum * 100)) / 100;
				
				System.out.printf ("%+02.2f:%+02.2f ", tem, hum);
				
				bi.setRGB(x, z, 0xFF000000 | tc | (tc << 8) | (tc << 16));
				bi.setRGB(x, h + z, 0xFF000000 | hc | (hc << 8) | (hc << 16));
				
				index ++;
			}
			System.out.println ();
		}
		
		try {
			ImageIO.write(bi, "PNG", new File("d:/temp/noise.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
