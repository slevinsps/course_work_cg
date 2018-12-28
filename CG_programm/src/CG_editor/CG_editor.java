package CG_editor;
import java.io.IOException;


public class CG_editor {
    static public Launcher display;
   
    
    static public void Render(Launcher display, int width, int height) throws IOException, InterruptedException {
        display.Run(width, height);
    }
    
    public static void main(String[] args) throws IOException, InterruptedException
	{
            int width = 800;
            int height = 600;
            display = new Launcher(width, height, "3D Rendering", 1);
            Render(display, width, height);
	}
    
}
