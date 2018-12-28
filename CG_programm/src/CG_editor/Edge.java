package CG_editor;

import CG_editor.Interpolation;

public class Edge
{
	private float x;
	private float xStep;
	private int yStart;
	private int yEnd;
	private float texCoordX;
	private float texCoordXStep;
	private float texCoordY;
	private float texCoordYStep;
	private float oneOverZ;
	private float oneOverZStep;
	private float depth;
	private float depthStep;
	private float lightAmt;
	private float lightAmtStep;

	public float GetX() {  return x; }
	public int GetYStart() { return yStart; }
	public int GetYEnd() { return yEnd; }
	public float GetTexCoordX() { return texCoordX; }
	public float GetTexCoordY() { return texCoordY; }
	public float GetOneOverZ() { return oneOverZ; }
	public float GetDepth() { return depth; }
	public float GetLightAmt() { return lightAmt; }

	public Edge(Interpolation interp, Vertex minYVert, Vertex maxYVert, int minYVertIndex)
	{
		yStart = (int)Math.ceil(minYVert.GetY());
		yEnd = (int)Math.ceil(maxYVert.GetY());

		float yDist = maxYVert.GetY() - minYVert.GetY();
		float xDist = maxYVert.GetX() - minYVert.GetX();

		float yPrestep = yStart - minYVert.GetY();
		xStep = (float)xDist/(float)yDist;
		x = minYVert.GetX() + yPrestep * xStep;
		float xPrestep = x - minYVert.GetX();

		texCoordX = interp.GetTexCoordX(minYVertIndex) +
			interp.GetTexCoordXXStep() * xPrestep +
			interp.GetTexCoordXYStep() * yPrestep;
		texCoordXStep = interp.GetTexCoordXYStep() + interp.GetTexCoordXXStep() * xStep;

		texCoordY = interp.GetTexCoordY(minYVertIndex) +
			interp.GetTexCoordYXStep() * xPrestep +
			interp.GetTexCoordYYStep() * yPrestep;
		texCoordYStep = interp.GetTexCoordYYStep() + interp.GetTexCoordYXStep() * xStep;

		oneOverZ = interp.GetOneOverZ(minYVertIndex) +
			interp.GetOneOverZXStep() * xPrestep +
			interp.GetOneOverZYStep() * yPrestep;
		oneOverZStep = interp.GetOneOverZYStep() + interp.GetOneOverZXStep() * xStep;

		depth = interp.GetDepth(minYVertIndex) +
			interp.GetDepthXStep() * xPrestep +
			interp.GetDepthYStep() * yPrestep;
		depthStep = interp.GetDepthYStep() + interp.GetDepthXStep() * xStep;

		lightAmt = interp.GetLightAmt(minYVertIndex) +
			interp.GetLightAmtXStep() * xPrestep +
			interp.GetLightAmtYStep() * yPrestep;
		lightAmtStep = interp.GetLightAmtYStep() + interp.GetLightAmtXStep() * xStep;
	}

	public void Step()
	{
		x += xStep;
		texCoordX += texCoordXStep;
		texCoordY += texCoordYStep;
		oneOverZ += oneOverZStep;
		depth += depthStep;
		lightAmt += lightAmtStep;
	}
}
