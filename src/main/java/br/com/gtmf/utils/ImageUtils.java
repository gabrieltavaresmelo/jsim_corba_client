package br.com.gtmf.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

/**
 * Classe que contem algoritmos de processamento de imagem
 * 
 * @author: Gabriel Tavares
 *
 */
public class ImageUtils {

    public static final float BEST_QUALITY = 1.0f;
    public static final float HIGH_QUALITY = 0.75f;
    public static final float MEDIUM_QUALITY = 0.5f;
    public static final float LOW_QUALITY = 0.25f;
    public static final float LEAST_QUALITY = 0.0f;
    
	/**
     * This method flips the image horizontally
     * @param img --> BufferedImage Object to be flipped horizontally
     * @return
     */
    public static BufferedImage horizontalflip(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(w, h, img.getType());
        Graphics2D g = dimg.createGraphics();

        g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
        g.dispose();
        
        return dimg;
    }
 
    /**
     * This method flips the image vertically
     * @param img --> BufferedImage object to be flipped
     * @return
     */
    public static BufferedImage verticalflip(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(w, h, img.getColorModel()
                .getTransparency());
        Graphics2D g = dimg.createGraphics();
        g.drawImage(img, 0, 0, w, h, 0, h, w, 0, null);
        g.dispose();
        return dimg;
    }
    
    public static BufferedImage[] splitImage(BufferedImage img, int cols, int rows) {  
        int w = img.getWidth()/cols;  
        int h = img.getHeight()/rows;  
        int num = 0;  
        BufferedImage imgs[] = new BufferedImage[w*h];  
        for(int y = 0; y < rows; y++) {  
            for(int x = 0; x < cols; x++) {  
                imgs[num] = new BufferedImage(w, h, img.getType());  
                // Tell the graphics to draw only one block of the image  
                Graphics2D g = imgs[num].createGraphics();  
                g.drawImage(img, 0, 0, w, h, w*x, h*y, w*x+w, h*y+h, null);  
                g.dispose();  
                num++;  
            }  
        }  
        return imgs;  
    } 
    
    /**
     * Redimensiona uma BufferedImage, caso seja diferente de 0 ou diferente
     * do tamanho da imagem.
     * 
     * @param img
     * @param newW
     * @param newH
     * @return
     */
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {        
        if((newW == 0 && newH == 0) || (img.getWidth() == newW && img.getHeight() == newH)){
        	return img;
        }
        
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());  
        Graphics2D g = dimg.createGraphics();  
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
        g.drawImage(img, 0, 0, newW, newH, 0, 0, img.getWidth(), img.getHeight(), null);  
        g.dispose();  
        
        return dimg;  
    }
	
    /**
     * Converte a imagem para tons de cinza
     * @return new gray image.
     */
    public static BufferedImage toGrayScale(BufferedImage image){
 		BufferedImage imageGray = new BufferedImage(image.getWidth(), image.getHeight(),
 				BufferedImage.TYPE_BYTE_GRAY);
 		Graphics g = imageGray.getGraphics();
 		g.drawImage(image, 0, 0, null);
 		g.dispose();
 		
 		return imageGray;
    }

    /**
     * Muda o brilho da imagem de acordo com o offset
     * @return image brightness.
     */
    public static BufferedImage changeBrightness(BufferedImage image, int offset) {	   
        float factor = 1.4f;
        RescaleOp op = new RescaleOp(factor , offset, null);
        BufferedImage brighter = op.filter(image, null);
        return brighter;
    }
    
    public static Image makeTranslucentImage(Image image, float transperancy) {  
    	BufferedImage bi = SwingFXUtils.fromFXImage(image, null);
		bi = ImageUtils.makeTranslucentImage(bi, 0.60f);
		
		return SwingFXUtils.toFXImage(bi, null);	
    }
    
    public static BufferedImage makeTranslucentImage(BufferedImage image, float transperancy) {  
        // Create the image using the   
        BufferedImage aimg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TRANSLUCENT);  
        // Get the images graphics  
        Graphics2D g = aimg.createGraphics();  
        // Set the Graphics composite to Alpha  
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transperancy));  
        // Draw the LOADED img into the prepared reciver image  
        g.drawImage(image, null, 0, 0);  
        // let go of all system resources in this Graphics  
        g.dispose();  
        // Return the image  
        return aimg;  
    }
    
    public static BufferedImage makeColorTransparent(BufferedImage image, Color color) { 
        BufferedImage dimg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);  
        
        Graphics2D g = dimg.createGraphics();  
        g.setComposite(AlphaComposite.Src);  
        g.drawImage(image, null, 0, 0);  
        g.dispose();  
        for(int i = 0; i < dimg.getHeight(); i++) {  
            for(int j = 0; j < dimg.getWidth(); j++) {  
                if(dimg.getRGB(j, i) == color.getRGB()) {  
                dimg.setRGB(j, i, 0x8F1C1C);  
                }  
            }  
        }  
        return dimg;  
    }
    
    /**
     * Desenha uma borda ao redor da imagem
     * 
     * @param image
     * @param color
     * @param borderWidth
     * 
     * @return imagem com borda
     */
    public static Image drawBorder(Image image, Color color, int borderWidth) { 
    	BufferedImage bi = SwingFXUtils.fromFXImage(image, null);
    	bi = ImageUtils.drawBorder(bi, color, borderWidth);    	
    	return SwingFXUtils.toFXImage(bi, null);
    }
    
    public static BufferedImage drawBorder(BufferedImage image, Color color, int borderWidth) { 
    	
    	BufferedImage dimg = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_INT_ARGB);  
        
    	if(borderWidth > dimg.getHeight()/2 || borderWidth > dimg.getWidth()/2){
			throw new IllegalArgumentException(
					"Tamanho de borda muito grande: borderWidth=" + borderWidth);
    	}
    	
        Graphics2D g = dimg.createGraphics();  
        g.setComposite(AlphaComposite.Src);  
        g.drawImage(image, null, 0, 0);  
        g.dispose();  
        
        for(int i = 0; i < dimg.getHeight(); i++) {  
            for(int j = 0; j < dimg.getWidth(); j++) {  
            	if( (i < borderWidth || i > dimg.getHeight() - (borderWidth+1)) ||
            			(j < borderWidth || j > dimg.getWidth() - (borderWidth+1)) ){
            		
            		dimg.setRGB(j, i, color.getRGB());  
            	}
            }  
        }
        
        return dimg;  
    }

    /**
     * Save JPEG image with the compression specified.
     * @param image Image to be saved to file.
     * @param imageFile file to save image.
     * @param quality must be in 0..1 range. 0.0 - highest compression is important, 1.0 - high image quality is important.
     * @throws IOException if the image can't be saved or file is not found.
     */
    public static void saveImageJPGCompressed(final BufferedImage image, final File imageFile, final float quality) throws IOException {
        if (image == null || imageFile == null){throw new IllegalArgumentException("\"image\" or \"imageFile\" params cannot be null.");}
        if (quality < 0 || quality >1){throw new IllegalArgumentException("Quality parameter must be in from 0 to 1 range.");}
        Iterator writers = ImageIO.getImageWritersBySuffix("jpeg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No writers found.");
        }
        // get the writer
        ImageWriter writer = (ImageWriter) writers.next();
        ImageWriteParam wParams = writer.getDefaultWriteParam();
        wParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        wParams.setCompressionQuality(quality);

        //writing image
		FileImageOutputStream output = new FileImageOutputStream(imageFile);
        writer.setOutput(output);
        writer.write(null, new IIOImage(image, null, null), wParams);
        writer.dispose();
    }

    /**
     * Rotates given image to given angle.
     * @param image image to be rotated.
     * @param angle angle to rotate image to
     * @return rotated image.
     */
    public static BufferedImage rotate(final BufferedImage image, final int angle) {
        if (image == null){throw new IllegalArgumentException("\"image\" param cannot be null.");}
        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        final Map<String, Integer> boundingBoxDimensions = calculateRotatedDimensions(imageWidth, imageHeight, angle);

        final int newWidth = boundingBoxDimensions.get("width");
        final int newHeight = boundingBoxDimensions.get("height");

        final BufferedImage newImage = new BufferedImage(newWidth, newHeight, image.getType());
        final Graphics2D newImageGraphic = newImage.createGraphics();

        final AffineTransform transform = new AffineTransform();
        transform.setToTranslation((newWidth-imageWidth)/2, (newHeight-imageHeight)/2);
        transform.rotate(Math.toRadians(angle), imageWidth/2, imageHeight/2);
        newImageGraphic.drawImage(image, transform, null);
        newImageGraphic.dispose();

        return newImage;
    }

    private static Map<String, Integer> calculateRotatedDimensions(final int imageWidth, final int imageHeight, final int angle) {
        final Map<String, Integer> dimensions = new HashMap<String, Integer>();
        // coordinates of our given image
        final int[][] points = {
                {0, 0},
                {imageWidth, 0},
                {0, imageHeight},
                {imageWidth, imageHeight}
        };

        final Map<String, Integer> boundBox = new HashMap<String, Integer>(){{
            put("left", 0);
            put("right", 0);
            put("top", 0);
            put("bottom", 0);
        }};

        final double theta = Math.toRadians(angle);

        for (final int[] point : points) {
            final int x = point[0];
            final int y = point[1];
            final int newX = (int) (x * Math.cos(theta) + y * Math.sin(theta));
            final int newY = (int) (x * Math.sin(theta) + y * Math.cos(theta));

            //assign the bounds
            boundBox.put("left", Math.min(boundBox.get("left"), newX));
            boundBox.put("right", Math.max(boundBox.get("right"), newX));
            boundBox.put("top", Math.min(boundBox.get("top"), newY));
            boundBox.put("bottom", Math.max(boundBox.get("bottom"), newY));
        }

        // now get the dimensions of the new box.
        dimensions.put("width", Math.abs(boundBox.get("right") - boundBox.get("left")));
        dimensions.put("height", Math.abs(boundBox.get("bottom") - boundBox.get("top")));
        return dimensions;
    }
	
	/**
	 * Watermarks the given image with given text.
	 * @param image Image to be watermarked
	 * @param text text to be used as a watermark.
	 * @return new watermarked image.
	 */
	public static BufferedImage waterMarkImage(final BufferedImage image, final String text) {
	    if (image == null){throw new IllegalArgumentException("\"image\" param cannot be null.");}
	    // create a new image
	    final BufferedImage waterMarked = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
	    final Graphics2D imageG = waterMarked.createGraphics();
	    // draw original image.
	    imageG.drawImage(image, null, 0, 0);
	    imageG.dispose();
	
	    final Graphics2D g  = waterMarked.createGraphics();
	    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // 50% transparency
	    g.setColor(Color.white);
	    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    g.setFont(new Font("Arial", Font.BOLD, 30));
	    final FontMetrics fontMetrics = g.getFontMetrics();
	    final Rectangle2D rect = fontMetrics.getStringBounds(text, g);
	    final int centerX = (image.getWidth() - (int) rect.getWidth()) /2;
	    final int centerY = (image.getHeight() - (int) rect.getHeight()) /2;
	    g.drawString(text, centerX, centerY);
	    g.dispose();
	    return waterMarked;
	}
	
	public static BufferedImage copyImage(BufferedImage image, int width, int height) {
        int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage copiedImage = new BufferedImage(width, height, type);
        Graphics2D g = copiedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return copiedImage;
    }

}