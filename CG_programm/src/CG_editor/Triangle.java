package CG_editor;

import java.util.List;

public class Triangle extends PrimitiveObject{
    public Vector4 A, B, C;
    public Vector4 An, Bn, Cn;
    public Vector4 At, Bt, Ct;
    public Vector4 normal;
    public ColorCG color;
    private ImageCG texture;
    private boolean is_texture;

    public Triangle () {
        A = new Vector4(0,0,1);
	B = new Vector4(0,1,0);
	C = new Vector4(1,0,0);
	color = new ColorCG(0.5f,0.5f,0.5f);
        is_texture = false;
    }

    public Triangle (Vector4 A_value, Vector4 B_value, Vector4 C_value, ColorCG colorValue) {
        A = A_value;
	B = B_value;
	C = C_value;
        color = colorValue;
        is_texture = false;
    }
    
    
    public Triangle (Vertex A_value, Vertex B_value, Vertex C_value, ImageCG texture_value) {

        A = A_value.GetPosition();
	B = B_value.GetPosition();
	C = C_value.GetPosition();
        
        An = A_value.GetNormal();
        Bn = B_value.GetNormal();
        Cn = C_value.GetNormal();
        
        At = A_value.GetTexCoords();
        Bt = B_value.GetTexCoords();
        Ct = C_value.GetTexCoords();
        
        texture = texture_value;
        is_texture = true;
    }
    
    public Triangle (Vertex A_value, Vertex B_value, Vertex C_value, ColorCG colorValue) {
        A = A_value.GetPosition();
	B = B_value.GetPosition();
	C = C_value.GetPosition();
        An = A_value.GetNormal();
        Bn = B_value.GetNormal();
        Cn = C_value.GetNormal();
        
        At = A_value.GetTexCoords();
        Bt = B_value.GetTexCoords();
        Ct = C_value.GetTexCoords();
        color = colorValue;
    }

    public static double GetCoord(double i1, double i2, double w1,
                                      double w2, double p)
    {
        return ((p - i1) / (i2 - i1)) * (w2 - w1) + w1;
    }
    @Override
    public ColorCG getColor (Vector4 pos) { 
        if (is_texture) {           
            Vector4 vAB = A.Sub(B);
            Vector4 vAC = A.Sub(C), vPA = pos.Sub(A), tAB =At.Sub(Bt),tAC =At.Sub(Ct) ;
            
            float A1 = vAB.Dot3(vAB);
            float A2 = vAC.Dot3(vAB);
            float A3 = vPA.Dot3(vAB);
            float B1 = vAB.Dot3(vAC);
            float B2 = vAC.Dot3(vAC);
            float B3 = vPA.Dot3(vAC);

            float a = (A3*B2 - A2*B3)/(A1*B2 - A2*B1); 
            float b = (A1*B3 - A3*B1)/(A1*B2 - A2*B1);
            
            float y1 = (tAB.GetY() * a + tAC.GetY() * b + At.GetY()) * texture.GetHeight();//GetCoord(0, dU, Ct.GetY() * dy, Bt.GetY() * dy, distY);
            float x1 = (tAB.GetX() * a + tAC.GetX() * b + At.GetX()) * texture.GetWidth() ;//GetCoord(0, dV, At.GetX() * dx, Bt.GetX() * dx, distX);

            int i1 = (int)x1, j1 = (int)y1;
            if (i1 >= 0 && j1 >= 0 &&
                i1 < texture.GetWidth() &&
                j1 < texture.GetHeight())
            {
                float colors[] = texture.get_pixel_color(i1, j1);
                ColorCG color_current = new ColorCG(colors[0], colors[1], colors[2], texture.specular, texture.refl, texture.refr, texture.opacity);
                return color_current;
            }
            return new ColorCG(0,0,0);
            
        }
        return color; 
    }

    @Override
    public Vector4 getNormalAt(Vector4 pos) {
        Vector4 vAB = A.Sub(B);
        
        
        Vector4 vAC = A.Sub(C), vPA = pos.Sub(A), tAB =An.Sub(Bn),tAC =An.Sub(Cn) ;
            
        float A1 = vAB.Dot(vAB);
        float A2 = vAC.Dot(vAB);
        float A3 = vPA.Dot(vAB);
        float B1 = vAB.Dot(vAC);
        float B2 = vAC.Dot(vAC);
        float B3 = vPA.Dot(vAC);

        float a = (A3*B2 - A2*B3)/(A1*B2 - A2*B1); 
        float b = (A1*B3 - A3*B1)/(A1*B2 - A2*B1);

        float y1 = (tAB.GetY() * a + tAC.GetY() * b + An.GetY());
        float x1 = (tAB.GetX() * a + tAC.GetX() * b + An.GetX());
        float z1 = (tAB.GetZ() * a + tAC.GetZ() * b + An.GetZ());
        
        
        return new Vector4(x1, y1, z1);//An.Add(Bn).Add(Cn).Normalized(); //new Vector4f(x1, y1, z1);
    }

    @Override
    public float findIntersection(Ray ray) {
        Vector4 ray_direction = ray.getRayDirection();
        Vector4 ray_origin = ray.getRayOrigin();

        Vector4 E1 = B.Sub(A);
        Vector4 E2 = C.Sub(A);
        Vector4 T = ray_origin.Sub(A);
        Vector4 P = ray_direction.Cross(E2);
        Vector4 Q = T.Cross(E1);
        float znam = P.Dot3(E1);
        float t = Q.Dot3(E2) / znam;
        float u = P.Dot3(T) / znam;
        float v = Q.Dot3(ray_direction) / znam;
        float t1 = 1 - u - v;
        if (u  < 1f && u  > 0f && v  < 1f && v  > 0f && t1 < 1f && t1  > 0f)
            return t;
        else 
            return -1;

    }
}   
    
 