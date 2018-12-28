package CG_editor;



public class Interpolation
{
	private float[] texCoordX;
	private float[] texCoordY;
	private float[] oneOverZ;
	private float[] depth;
	private float[] lightAmt;

	private float texCoordXXStep;
	private float texCoordXYStep;
	private float texCoordYXStep;
	private float texCoordYYStep;
	private float oneOverZXStep;
	private float oneOverZYStep;
	private float depthXStep;
	private float depthYStep;
	private float lightAmtXStep;
	private float lightAmtYStep;

	public float GetTexCoordX(int index) { return texCoordX[index]; }
	public float GetTexCoordY(int index) { return texCoordY[index]; }
	public float GetOneOverZ(int index) { return oneOverZ[index]; }
	public float GetDepth(int index) { return depth[index]; }
	public float GetLightAmt(int index) { return lightAmt[index]; }

	public float GetTexCoordXXStep() { return texCoordXXStep; }
	public float GetTexCoordXYStep() { return texCoordXYStep; }
	public float GetTexCoordYXStep() { return texCoordYXStep; }
	public float GetTexCoordYYStep() { return texCoordYYStep; }
	public float GetOneOverZXStep() { return oneOverZXStep; }
	public float GetOneOverZYStep() { return oneOverZYStep; }
	public float GetDepthXStep() { return depthXStep; }
	public float GetDepthYStep() { return depthYStep; }
	public float GetLightAmtXStep() { return lightAmtXStep; }
	public float GetLightAmtYStep() { return lightAmtYStep; }

	private float FindStepX(float[] values, Vertex minYVert, Vertex midYVert,
			Vertex maxYVert, float oneOverdX)
	{
            return
                (((values[1] - values[2]) *
                (minYVert.GetY() - maxYVert.GetY())) -
                ((values[0] - values[2]) *
                (midYVert.GetY() - maxYVert.GetY()))) * oneOverdX;
	}

	private float FindStepY(float[] values, Vertex minYVert, Vertex midYVert,
			Vertex maxYVert, float oneOverdY)
	{
            return
                (((values[1] - values[2]) *
                (minYVert.GetX() - maxYVert.GetX())) -
                ((values[0] - values[2]) *
                (midYVert.GetX() - maxYVert.GetX()))) * oneOverdY;
	}

	private float Limit(float val)
	{
            if(val > 1.0f)
            {
                    return 1.0f;
            }
            if(val < 0.0f)
            {
                    return 0.0f;
            }
            return val;
	}

	public Interpolation(Vertex minYVert, Vertex midYVert, Vertex maxYVert, Vector4 lightDir)
	{
		float oneOverdX = 1.0f /
			(((midYVert.GetX() - maxYVert.GetX()) *
			(minYVert.GetY() - maxYVert.GetY())) -
			((minYVert.GetX() - maxYVert.GetX()) *
			(midYVert.GetY() - maxYVert.GetY())));

		float oneOverdY = -oneOverdX;

		oneOverZ = new float[3];
		texCoordX = new float[3];
		texCoordY = new float[3];
		depth = new float[3];
		lightAmt = new float[3];

		depth[0] = minYVert.GetPosition().GetZ();
		depth[1] = midYVert.GetPosition().GetZ();
		depth[2] = maxYVert.GetPosition().GetZ();
                
		lightAmt[0] = Limit(minYVert.GetNormal().Dot(lightDir)) * 0.5f + 0.1f;
		lightAmt[1] = Limit(midYVert.GetNormal().Dot(lightDir)) * 0.5f + 0.1f;
		lightAmt[2] = Limit(maxYVert.GetNormal().Dot(lightDir)) * 0.5f + 0.1f;

		oneOverZ[0] = 1.0f/minYVert.GetPosition().GetW();
		oneOverZ[1] = 1.0f/midYVert.GetPosition().GetW();
		oneOverZ[2] = 1.0f/maxYVert.GetPosition().GetW();

		texCoordX[0] = minYVert.GetTexCoords().GetX() * oneOverZ[0];
		texCoordX[1] = midYVert.GetTexCoords().GetX() * oneOverZ[1];
		texCoordX[2] = maxYVert.GetTexCoords().GetX() * oneOverZ[2];

		texCoordY[0] = minYVert.GetTexCoords().GetY() * oneOverZ[0];
		texCoordY[1] = midYVert.GetTexCoords().GetY() * oneOverZ[1];
		texCoordY[2] = maxYVert.GetTexCoords().GetY() * oneOverZ[2];

		texCoordXXStep = FindStepX(texCoordX, minYVert, midYVert, maxYVert, oneOverdX);
		texCoordXYStep = FindStepY(texCoordX, minYVert, midYVert, maxYVert, oneOverdY);
		texCoordYXStep = FindStepX(texCoordY, minYVert, midYVert, maxYVert, oneOverdX);
		texCoordYYStep = FindStepY(texCoordY, minYVert, midYVert, maxYVert, oneOverdY);
		oneOverZXStep = FindStepX(oneOverZ, minYVert, midYVert, maxYVert, oneOverdX);
		oneOverZYStep = FindStepY(oneOverZ, minYVert, midYVert, maxYVert, oneOverdY);
		depthXStep = FindStepX(depth, minYVert, midYVert, maxYVert, oneOverdX);
		depthYStep = FindStepY(depth, minYVert, midYVert, maxYVert, oneOverdY);
		lightAmtXStep = FindStepX(lightAmt, minYVert, midYVert, maxYVert, oneOverdX);
		lightAmtYStep = FindStepY(lightAmt, minYVert, midYVert, maxYVert, oneOverdY);
	}
}
