package com.colorswatchgenerator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main GUI application for Color Swatch Generator.
 * Allows users to upload or paste images and view the top 20 most used colors.
 */
public class ColorSwatchGeneratorApp extends JFrame {
    
    private static final int TOP_COLORS_COUNT = 20;
    
    private JPanel imagePanel;
    private JPanel colorListPanel;
    private JLabel imageLabel;
    private BufferedImage currentImage;
    private ColorAnalyzer colorAnalyzer;
    
    public ColorSwatchGeneratorApp() {
        colorAnalyzer = new ColorAnalyzer();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Color Swatch Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Main layout
        setLayout(new BorderLayout(10, 10));
        
        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton uploadButton = new JButton("Upload Image");
        JButton pasteButton = new JButton("Paste from Clipboard");
        JButton analyzeButton = new JButton("Analyze Colors");
        
        uploadButton.addActionListener(e -> uploadImage());
        pasteButton.addActionListener(e -> pasteImage());
        analyzeButton.addActionListener(e -> analyzeImage());
        
        topPanel.add(uploadButton);
        topPanel.add(pasteButton);
        topPanel.add(analyzeButton);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel for image display
        imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createTitledBorder("Image Preview"));
        imageLabel = new JLabel("No image loaded. Upload or paste an image to begin.", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(600, 500));
        imagePanel.add(new JScrollPane(imageLabel), BorderLayout.CENTER);
        
        // Enable drag and drop
        new DropTarget(imagePanel, new ImageDropTargetListener());
        
        add(imagePanel, BorderLayout.CENTER);
        
        // Right panel for color list
        colorListPanel = new JPanel();
        colorListPanel.setLayout(new BoxLayout(colorListPanel, BoxLayout.Y_AXIS));
        colorListPanel.setBorder(BorderFactory.createTitledBorder("Top 20 Colors"));
        JScrollPane colorScrollPane = new JScrollPane(colorListPanel);
        colorScrollPane.setPreferredSize(new Dimension(350, 500));
        
        add(colorScrollPane, BorderLayout.EAST);
    }
    
    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif", "bmp"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                currentImage = ImageIO.read(selectedFile);
                displayImage();
                analyzeImage();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error loading image: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void pasteImage() {
        try {
            Transferable transferable = Toolkit.getDefaultToolkit()
                .getSystemClipboard().getContents(null);
            
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                Image image = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
                currentImage = toBufferedImage(image);
                displayImage();
                analyzeImage();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No image found in clipboard. Copy an image first.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error pasting image: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayImage() {
        if (currentImage != null) {
            // Scale image to fit panel while maintaining aspect ratio
            int maxWidth = 600;
            int maxHeight = 500;
            
            int width = currentImage.getWidth();
            int height = currentImage.getHeight();
            
            double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);
            if (scale > 1) scale = 1; // Don't upscale
            
            int scaledWidth = (int) (width * scale);
            int scaledHeight = (int) (height * scale);
            
            Image scaledImage = currentImage.getScaledInstance(
                scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.setText("");
        }
    }
    
    private void analyzeImage() {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this,
                "Please load an image first.",
                "No Image", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show loading message
        colorListPanel.removeAll();
        colorListPanel.add(new JLabel("Analyzing image..."));
        colorListPanel.revalidate();
        colorListPanel.repaint();
        
        // Analyze in background thread to keep UI responsive
        SwingWorker<List<ColorAnalyzer.ColorCount>, Void> worker = 
            new SwingWorker<List<ColorAnalyzer.ColorCount>, Void>() {
            
            @Override
            protected List<ColorAnalyzer.ColorCount> doInBackground() {
                return colorAnalyzer.getTopColors(currentImage, TOP_COLORS_COUNT);
            }
            
            @Override
            protected void done() {
                try {
                    List<ColorAnalyzer.ColorCount> topColors = get();
                    displayColors(topColors);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ColorSwatchGeneratorApp.this,
                        "Error analyzing image: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void displayColors(List<ColorAnalyzer.ColorCount> topColors) {
        colorListPanel.removeAll();
        
        for (int i = 0; i < topColors.size(); i++) {
            ColorAnalyzer.ColorCount colorCount = topColors.get(i);
            JPanel colorItemPanel = createColorItemPanel(i + 1, colorCount);
            colorListPanel.add(colorItemPanel);
            colorListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        colorListPanel.revalidate();
        colorListPanel.repaint();
    }
    
    private JPanel createColorItemPanel(int rank, ColorAnalyzer.ColorCount colorCount) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        // Color swatch
        JPanel colorSwatch = new JPanel();
        colorSwatch.setBackground(colorCount.getColor());
        colorSwatch.setPreferredSize(new Dimension(50, 50));
        colorSwatch.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // Color info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        JLabel rankLabel = new JLabel(String.format("#%d", rank));
        rankLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel hexLabel = new JLabel(colorCount.getHexString());
        hexLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JLabel rgbLabel = new JLabel(colorCount.getRgbString());
        rgbLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JLabel countLabel = new JLabel(String.format("Count: %,d pixels", colorCount.getCount()));
        countLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        
        infoPanel.add(rankLabel);
        infoPanel.add(hexLabel);
        infoPanel.add(rgbLabel);
        infoPanel.add(countLabel);
        
        panel.add(colorSwatch, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        
        BufferedImage bufferedImage = new BufferedImage(
            img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        
        return bufferedImage;
    }
    
    /**
     * Listener for drag and drop functionality.
     */
    private class ImageDropTargetListener extends DropTargetAdapter {
        @Override
        public void drop(DropTargetDropEvent event) {
            try {
                event.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable transferable = event.getTransferable();
                
                if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    @SuppressWarnings("unchecked")
                    java.util.List<File> files = (java.util.List<File>) 
                        transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    
                    if (!files.isEmpty()) {
                        File file = files.get(0);
                        currentImage = ImageIO.read(file);
                        displayImage();
                        analyzeImage();
                        event.dropComplete(true);
                        return;
                    }
                }
                
                event.dropComplete(false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(ColorSwatchGeneratorApp.this,
                    "Error dropping image: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                event.dropComplete(false);
            }
        }
    }
    
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default if system look and feel fails
        }
        
        // Create and show GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            ColorSwatchGeneratorApp app = new ColorSwatchGeneratorApp();
            app.setVisible(true);
        });
    }
}
