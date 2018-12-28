package CG_editor;

public class Vertex
{
	private Vector4 pos;
	private Vector4 texpos;
	private Vector4 normal;

	public float GetX() { return pos.GetX(); }
	public float GetY() { return pos.GetY(); }
        public float GetZ() { return pos.GetZ(); }

	public Vector4 GetPosition() { return pos; }
	public Vector4 GetTexCoords() { return texpos; }
	public Vector4 GetNormal() { return normal; }

	public Vertex(Vector4 pos_v, Vector4 texCoords_v, Vector4 normal_v)
	{
		pos = pos_v;
		texpos = texCoords_v;
		normal = normal_v;
	}

	public Vertex Transform(Matrix transform, Matrix normalTransform)
	{
		return new Vertex(transform.Mul(pos), texpos, 
				normalTransform.Mul(normal).Normalized());
	}
        
        public Vertex TransformPos(Matrix transform)
	{
		return new Vertex(transform.Mul(pos), texpos, normal);
	}

	public Vertex PerspectiveDivide()
	{
            //System.out.println(pos.GetZ() + " " + pos.GetW());
            //float z_on_w =  pos.GetZ()/pos.GetW();
		return new Vertex(new Vector4((pos.GetX()/pos.GetW()), (pos.GetY()/pos.GetW()), 
						(pos.GetZ()/pos.GetW()), pos.GetW()),	
				texpos, normal);
	}

	public float TriangleAreaTimesTwo(Vertex b, Vertex c)
	{
		float x1 = b.GetX() - pos.GetX();
		float y1 = b.GetY() - pos.GetY();

		float x2 = c.GetX() - pos.GetX();
		float y2 = c.GetY() - pos.GetY();

		return (x1 * y2 - x2 * y1);
	}

	public Vertex Lerp(Vertex other, float lerpAmt)
	{
		return new Vertex(
				pos.Lerp(other.GetPosition(), lerpAmt),
				texpos.Lerp(other.GetTexCoords(), lerpAmt),
				normal.Lerp(other.GetNormal(), lerpAmt)
				);
	}

	public boolean IsInsideView()
	{
		return 
			Math.abs(pos.GetX()) <= Math.abs(pos.GetW()) &&
			Math.abs(pos.GetY()) <= Math.abs(pos.GetW()) &&
			Math.abs(pos.GetZ()) <= Math.abs(pos.GetW());
	}

	public float Get(int index)
	{
		switch(index)
		{
			case 0:
				return pos.GetX();
			case 1:
				return pos.GetY();
			case 2:
				return pos.GetZ();
			case 3:
				return pos.GetW();
			default:
				throw new IndexOutOfBoundsException();
		}
	}
}
