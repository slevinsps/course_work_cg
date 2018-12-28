package CG_editor;

import java.util.Arrays;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageCG
{
    	private final int  width_image;
	private final int  height_image;
	private final byte m_components[];
        public float specular, refl, refr, opacity; 

	public int GetWidth() { return width_image; }
	public int GetHeight() { return height_image; }

	public byte GetComponent(int index) { 
            if (index >= 0 && index < m_components.length)
                return m_components[index]; 
            else 
                return m_components[0]; 
        }

	public ImageCG(int width, int height)
	{
		width_image      = width;
		height_image     = height;
		m_components = new byte[width_image * height_image * 4];
                refl = 0;
                refr = 0;
                opacity = 0;
                specular = 0;
	}

	public ImageCG(File fileName, float specular_v, float reflv, float refrv, float opacityv) throws IOException
	{
		int width = 0;
		int height = 0;
		byte[] components = null;

		BufferedImage image = ImageIO.read(fileName);

		width = image.getWidth();
		height = image.getHeight();

		int imgPixels[] = new int[width * height];
		image.getRGB(0, 0, width, height, imgPixels, 0, width);
		components = new byte[width * height * 4];

		for(int i = 0; i < width * height; i++)
		{
			int pixel = imgPixels[i];

			components[i * 4]     = (byte)((pixel >> 24) & 0xFF); // A
			components[i * 4 + 1] = (byte)((pixel      ) & 0xFF); // B
			components[i * 4 + 2] = (byte)((pixel >> 8 ) & 0xFF); // G
			components[i * 4 + 3] = (byte)((pixel >> 16) & 0xFF); // R
		}

		width_image = width;
		height_image = height;
		m_components = components;
                refl = reflv;
                refr = refrv;
                specular = specular_v;
                opacity = opacityv;
	}

	public void Clear(byte shade)
	{
		Arrays.fill(m_components, shade);
	}

	public void DrawPixel(int x, int y, byte a, byte b, byte g, byte r)
	{
		int index = (x + y * width_image) * 4;
		m_components[index    ] = a;
		m_components[index + 1] = b;
		m_components[index + 2] = g;
		m_components[index + 3] = r;
	}
        
        public void DrawPixelLight(int x, int y, byte a, byte b, byte g, byte r, float lightAmt)
	{
		int index = (x + y * width_image) * 4;
		m_components[index    ] = (byte)((a & 0xFF) * lightAmt);
		m_components[index + 1] = (byte)((b & 0xFF) * lightAmt);
		m_components[index + 2] = (byte)((g & 0xFF) * lightAmt);
		m_components[index + 3] = (byte)((r & 0xFF) * lightAmt);
	}

	public void CopyPixel(int destX, int destY, int srcX, int srcY, ImageCG src, float lightAmt)
	{
		int destIndex = (destX + destY * width_image) * 4;
		int srcIndex = (srcX + srcY * src.GetWidth()) * 4;
		
		m_components[destIndex    ] = (byte)((src.GetComponent(srcIndex) & 0xFF) * lightAmt);
		m_components[destIndex + 1] = (byte)((src.GetComponent(srcIndex + 1) & 0xFF) * lightAmt);
		m_components[destIndex + 2] = (byte)((src.GetComponent(srcIndex + 2) & 0xFF) * lightAmt);
		m_components[destIndex + 3] = (byte)((src.GetComponent(srcIndex + 3) & 0xFF) * lightAmt);
	}
        
        public float[] get_pixel_color(int srcX, int srcY) {
            
            int srcIndex = (srcX + srcY * this.GetWidth()) * 4;
            float r = (this.GetComponent(srcIndex + 3) & 0xFF) / 255.f;
            float g = (this.GetComponent(srcIndex + 2) & 0xFF) / 255.f;
            float b = (this.GetComponent(srcIndex + 1) & 0xFF) / 255.f;
            return new float[] {r, g, b};
        }
        
	public void CopyToByteArray(byte[] dest)
	{
            for(int i = 0; i < width_image * height_image; i++)
            {
                dest[i * 3    ] = m_components[i * 4 + 1];
                dest[i * 3 + 1] = m_components[i * 4 + 2];
                dest[i * 3 + 2] = m_components[i * 4 + 3];
            }
	}
}
