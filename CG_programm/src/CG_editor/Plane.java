package CG_editor;

public class Plane extends PrimitiveObject{
    public Vector4 normal;
    public float distance;
    public ColorCG color;

    public Plane () {
        normal = new Vector4(1.f,0,0);
	distance = 0;
	color = new ColorCG(0.5f,0.5f,0.5f);
    }

    public Plane (Vector4 normalValue, float distanceValue, ColorCG colorValue) {
        normal = normalValue;
	distance = distanceValue;
	color = colorValue;
    }

    public Vector4 getPlaneNormal () { return normal; }
    public double getPlaneDistance () { return distance; }
    
    @Override
    public ColorCG getColor (Vector4 pos) { return color; }

    @Override
    public Vector4 getNormalAt(Vector4 point) {
        return normal;
    }

    @Override
    public float findIntersection(Ray ray) {
        Vector4 ray_direction = ray.getRayDirection();

        float a = ray_direction.Dot3(normal);

        if (a == 0) {
                return -1;
        }
        else {
            float b = normal.Dot3(ray.getRayOrigin().Add(normal.Mul(distance).Negative()));
            return -1*b/a;
        }
    }
}
