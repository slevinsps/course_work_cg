package CG_editor;

public class Matrix
{
	private float[][] matrix;

	public Matrix()
	{
		matrix = new float[4][4];
	}
        public float[][] GetM()
	{
		float[][] res = new float[4][4];

		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
				res[i][j] = matrix[i][j];

		return res;
	}

	public float Get(int x, int y)
	{
		return matrix[x][y];
	}

	public void SetM(float[][] m)
	{
		this.matrix = m;
	}

	public void Set(int x, int y, float value)
	{
		matrix[x][y] = value;
	}

	public Matrix CreateIdentity()
	{
		matrix[0][0] = 1;	matrix[0][1] = 0;	matrix[0][2] = 0;	matrix[0][3] = 0;
		matrix[1][0] = 0;	matrix[1][1] = 1;	matrix[1][2] = 0;	matrix[1][3] = 0;
		matrix[2][0] = 0;	matrix[2][1] = 0;	matrix[2][2] = 1;	matrix[2][3] = 0;
		matrix[3][0] = 0;	matrix[3][1] = 0;	matrix[3][2] = 0;	matrix[3][3] = 1;

		return this;
	}

	public Matrix CreateScreenSpace(float halfWidth, float halfHeight)
	{
		matrix[0][0] = halfWidth;	matrix[0][1] = 0;	matrix[0][2] = 0;	matrix[0][3] = halfWidth - 0.5f;
		matrix[1][0] = 0;	matrix[1][1] = -halfHeight;	matrix[1][2] = 0;	matrix[1][3] = halfHeight - 0.5f;
		matrix[2][0] = 0;	matrix[2][1] = 0;	matrix[2][2] = 1;	matrix[2][3] = 0;
		matrix[3][0] = 0;	matrix[3][1] = 0;	matrix[3][2] = 0;	matrix[3][3] = 1;

		return this;
	}

	public Matrix CreateMovement(float x, float y, float z)
	{
		matrix[0][0] = 1;	matrix[0][1] = 0;	matrix[0][2] = 0;	matrix[0][3] = x;
		matrix[1][0] = 0;	matrix[1][1] = 1;	matrix[1][2] = 0;	matrix[1][3] = y;
		matrix[2][0] = 0;	matrix[2][1] = 0;	matrix[2][2] = 1;	matrix[2][3] = z;
		matrix[3][0] = 0;	matrix[3][1] = 0;	matrix[3][2] = 0;	matrix[3][3] = 1;

		return this;
	}

	public Matrix CreateRotation(float x, float y, float z, float angle)
	{
		float sin = (float)Math.sin(angle);
		float cos = (float)Math.cos(angle);

		matrix[0][0] = cos+x*x*(1-cos); matrix[0][1] = x*y*(1-cos)-z*sin; matrix[0][2] = x*z*(1-cos)+y*sin; matrix[0][3] = 0;
		matrix[1][0] = y*x*(1-cos)+z*sin; matrix[1][1] = cos+y*y*(1-cos);	matrix[1][2] = y*z*(1-cos)-x*sin; matrix[1][3] = 0;
		matrix[2][0] = z*x*(1-cos)-y*sin; matrix[2][1] = z*y*(1-cos)+x*sin; matrix[2][2] = cos+z*z*(1-cos); matrix[2][3] = 0;
		matrix[3][0] = 0;	matrix[3][1] = 0;	matrix[3][2] = 0;	matrix[3][3] = 1;

		return this;
	}



	public Matrix CreateScale(float x, float y, float z)
	{
		matrix[0][0] = x;	matrix[0][1] = 0;	matrix[0][2] = 0;	matrix[0][3] = 0;
		matrix[1][0] = 0;	matrix[1][1] = y;	matrix[1][2] = 0;	matrix[1][3] = 0;
		matrix[2][0] = 0;	matrix[2][1] = 0;	matrix[2][2] = z;	matrix[2][3] = 0;
		matrix[3][0] = 0;	matrix[3][1] = 0;	matrix[3][2] = 0;	matrix[3][3] = 1;

		return this;
	}

	public Matrix CreatePerspective(float fov, float aspectRatio, float zNear, float zFar)
	{
		float tanHalfFOV = (float)Math.tan(fov / 2);
		float zRange = zNear - zFar;

		matrix[0][0] = 1.0f / (tanHalfFOV * aspectRatio);	        matrix[0][1] = 0;				matrix[0][2] = 0;	                   matrix[0][3] = 0;
		matrix[1][0] = 0;						matrix[1][1] = 1.0f / tanHalfFOV;	matrix[1][2] = 0;	                   matrix[1][3] = 0;
		matrix[2][0] = 0;						matrix[2][1] = 0;			matrix[2][2] = (-zNear -zFar)/zRange;   matrix[2][3] = 2 * zFar * zNear / zRange;
		matrix[3][0] = 0;						matrix[3][1] = 0;			matrix[3][2] = 1;	                   matrix[3][3] = 0;


		return this;
	}

	public Matrix CreateRotation(Vector4 forward, Vector4 up)
	{
		Vector4 f = forward.Normalized();

		Vector4 r = up.Normalized();
		r = r.Cross(f);

		Vector4 u = f.Cross(r);

		return CreateRotation(f, u, r);
	}

	public Matrix CreateRotation(Vector4 forward, Vector4 up, Vector4 right)
	{
		Vector4 f = forward;
		Vector4 r = right;
		Vector4 u = up;

		matrix[0][0] = r.GetX();	matrix[0][1] = r.GetY();	matrix[0][2] = r.GetZ();	matrix[0][3] = 0;
		matrix[1][0] = u.GetX();	matrix[1][1] = u.GetY();	matrix[1][2] = u.GetZ();	matrix[1][3] = 0;
		matrix[2][0] = f.GetX();	matrix[2][1] = f.GetY();	matrix[2][2] = f.GetZ();	matrix[2][3] = 0;
		matrix[3][0] = 0;		matrix[3][1] = 0;		matrix[3][2] = 0;		matrix[3][3] = 1;

		return this;
	}

	public Vector4 Mul(Vector4 r)
	{
		return new Vector4(matrix[0][0] * r.GetX() + matrix[0][1] * r.GetY() + matrix[0][2] * r.GetZ() + matrix[0][3] * r.GetW(),
		                    matrix[1][0] * r.GetX() + matrix[1][1] * r.GetY() + matrix[1][2] * r.GetZ() + matrix[1][3] * r.GetW(),
		                    matrix[2][0] * r.GetX() + matrix[2][1] * r.GetY() + matrix[2][2] * r.GetZ() + matrix[2][3] * r.GetW(),
				    matrix[3][0] * r.GetX() + matrix[3][1] * r.GetY() + matrix[3][2] * r.GetZ() + matrix[3][3] * r.GetW());
	}

	public Matrix Mul(Matrix r)
	{
		Matrix res = new Matrix();

		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < 4; j++)
			{
				res.Set(i, j, matrix[i][0] * r.Get(0, j) +
						matrix[i][1] * r.Get(1, j) +
						matrix[i][2] * r.Get(2, j) +
						matrix[i][3] * r.Get(3, j));
			}
		}

		return res;
	}

	
}
