package com.colorswatchgenerator;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Analyzes images to extract the most frequently used colors.
 */
public class ColorAnalyzer {
    
    /**
     * Analyzes an image and returns the top N most frequently used colors.
     * 
     * @param image The BufferedImage to analyze
     * @param topN The number of top colors to return
     * @return A list of ColorCount objects representing the top colors
     */
    public List<ColorCount> getTopColors(BufferedImage image, int topN) {
        Map<Integer, Integer> colorFrequency = new HashMap<>();
        
        // Count frequency of each color
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                colorFrequency.put(rgb, colorFrequency.getOrDefault(rgb, 0) + 1);
            }
        }
        
        // Sort colors by frequency
        List<Map.Entry<Integer, Integer>> sortedColors = new ArrayList<>(colorFrequency.entrySet());
        sortedColors.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        // Get top N colors
        List<ColorCount> topColors = new ArrayList<>();
        int count = Math.min(topN, sortedColors.size());
        for (int i = 0; i < count; i++) {
            Map.Entry<Integer, Integer> entry = sortedColors.get(i);
            Color color = new Color(entry.getKey());
            topColors.add(new ColorCount(color, entry.getValue()));
        }
        
        return topColors;
    }
    
    /**
     * Class to represent a color and its frequency count.
     */
    public static class ColorCount {
        private final Color color;
        private final int count;
        
        public ColorCount(Color color, int count) {
            this.color = color;
            this.count = count;
        }
        
        public Color getColor() {
            return color;
        }
        
        public int getCount() {
            return count;
        }
        
        /**
         * Returns the color as a hex string.
         */
        public String getHexString() {
            return String.format("#%02X%02X%02X", 
                color.getRed(), color.getGreen(), color.getBlue());
        }
        
        /**
         * Returns the color as RGB values.
         */
        public String getRgbString() {
            return String.format("RGB(%d, %d, %d)", 
                color.getRed(), color.getGreen(), color.getBlue());
        }
    }
}
