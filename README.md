# ColorSwatchGenerator

A Java GUI application that analyzes images and displays the top 20 most frequently used colors.

## Features

- **Upload Images**: Browse and select image files (JPG, PNG, GIF, BMP)
- **Paste from Clipboard**: Copy an image and paste it directly into the application
- **Drag and Drop**: Drag image files directly onto the application window
- **Color Analysis**: Automatically analyzes images and shows the top 20 colors
- **Detailed Information**: View color information in HEX, RGB format, and pixel count

## Requirements

- Java 11 or higher
- Maven 3.6+ (for building from source)

## Building from Source

```bash
# Clone the repository
git clone https://github.com/ElliottBolan/ColorSwatchGenerator.git
cd ColorSwatchGenerator

# Build with Maven
mvn clean package

# The JAR file will be created in target/ColorSwatchGenerator-1.0.0.jar
```

## Running the Application

```bash
# Run the JAR file
java -jar target/ColorSwatchGenerator-1.0.0.jar
```

Or simply double-click the JAR file if your system is configured to run Java applications.

## How to Use

1. **Launch the application**
2. **Load an image** using one of these methods:
   - Click "Upload Image" to browse and select an image file
   - Click "Paste from Clipboard" after copying an image
   - Drag and drop an image file onto the application window
3. **View the results**:
   - The image preview appears in the center panel
   - The top 20 colors are automatically analyzed and displayed in the right panel
   - Each color shows:
     - Color swatch
     - Rank (#1 to #20)
     - HEX value (e.g., #FF5733)
     - RGB value (e.g., RGB(255, 87, 51))
     - Pixel count

## Project Structure

```
ColorSwatchGenerator/
├── src/main/java/com/colorswatchgenerator/
│   ├── ColorSwatchGeneratorApp.java  # Main GUI application
│   └── ColorAnalyzer.java             # Color analysis logic
├── pom.xml                            # Maven configuration
└── README.md                          # This file
```

## Technology Stack

- **Java 11**: Core programming language
- **Swing**: GUI framework
- **Maven**: Build and dependency management

## License

This project is open source and available under the MIT License.