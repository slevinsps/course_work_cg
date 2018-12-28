package CG_editor;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import CG_editor.Interpolation;

public class RenderSceneTriangle extends ImageCG
{
	private float[] zBuffer;

        public void NewZBuffer()
	{
		for(int i = 0; i < zBuffer.length; i++)
		{
			zBuffer[i] = Float.MAX_VALUE;
		}
	}

	public void DrawTriangle(Vertex v1, Vertex v2, Vertex v3, ImageCG texture, ColorCG color, boolean tex_paint, List<Source> light_array)
	{
            if(v1.IsInsideView() && v2.IsInsideView() && v3.IsInsideView())
            {
                    FillTriangle(v1, v2, v3, texture, color, tex_paint, light_array);
                    return;
            }

            List<Vertex> vertices = new ArrayList<>();
            List<Vertex> additionalList = new ArrayList<>();

            vertices.add(v1);
            vertices.add(v2);
            vertices.add(v3);

            if(ClipPolygon(vertices, additionalList, 0) &&
                            ClipPolygon(vertices, additionalList, 1) &&
                            ClipPolygon(vertices, additionalList, 2))
            {
                Vertex initialVertex = vertices.get(0);

                for(int i = 1; i < vertices.size() - 1; i++)
                {
                        FillTriangle(initialVertex, vertices.get(i), vertices.get(i + 1), texture, color, tex_paint, light_array);
                }
            }
	}

	private boolean ClipPolygon(List<Vertex> vertices, List<Vertex> addList,
			int componentIndex)
	{
		ClipPolygonComponent(vertices, componentIndex, 1.0f, addList);
		vertices.clear();

		if(addList.isEmpty())
		{
			return false;
		}

		ClipPolygonComponent(addList, componentIndex, -1.0f, vertices);
		addList.clear();

		return !vertices.isEmpty();
	}

	private void ClipPolygonComponent(List<Vertex> vertices, int componentIndex, 
			float componentFactor, List<Vertex> result)
	{
		Vertex previousVertex = vertices.get(vertices.size() - 1);
		float previousComponent = previousVertex.Get(componentIndex) * componentFactor;
		boolean previousInside = previousComponent <= previousVertex.GetPosition().GetW();

		Iterator<Vertex> it = vertices.iterator();
		while(it.hasNext())
		{
			Vertex currentVertex = it.next();
			float currentComponent = currentVertex.Get(componentIndex) * componentFactor;
			boolean currentInside = currentComponent <= currentVertex.GetPosition().GetW();

			if(currentInside ^ previousInside)
			{
				float lerpAmt = (previousVertex.GetPosition().GetW() - previousComponent) /
					((previousVertex.GetPosition().GetW() - previousComponent) - 
					 (currentVertex.GetPosition().GetW() - currentComponent));

				result.add(previousVertex.Lerp(currentVertex, lerpAmt));
			}

			if(currentInside)
			{
				result.add(currentVertex);
			}

			previousVertex = currentVertex;
			previousComponent = currentComponent;
			previousInside = currentInside;
		}
	}

	private void FillTriangle(Vertex v1, Vertex v2, Vertex v3, ImageCG texture, ColorCG color, boolean tex_paint, List<Source> light_array)
	{
		Matrix screenSpaceTransform = 
				new Matrix().CreateScreenSpace(GetWidth()/2, GetHeight()/2);
		Matrix identity = new Matrix().CreateIdentity();
		Vertex minYVert = v1.Transform(screenSpaceTransform, identity).PerspectiveDivide();
		Vertex midYVert = v2.Transform(screenSpaceTransform, identity).PerspectiveDivide();
		Vertex maxYVert = v3.Transform(screenSpaceTransform, identity).PerspectiveDivide();

		if(minYVert.TriangleAreaTimesTwo(maxYVert, midYVert) >= 0)
		{
			return;
		}

		if(maxYVert.GetY() < midYVert.GetY())
		{
			Vertex temp = maxYVert;
			maxYVert = midYVert;
			midYVert = temp;
		}

		if(midYVert.GetY() < minYVert.GetY())
		{
			Vertex temp = midYVert;
			midYVert = minYVert;
			minYVert = temp;
		}

		if(maxYVert.GetY() < midYVert.GetY())
		{
			Vertex temp = maxYVert;
			maxYVert = midYVert;
			midYVert = temp;
		}

		ScanTriangle(minYVert, midYVert, maxYVert, 
				minYVert.TriangleAreaTimesTwo(maxYVert, midYVert) >= 0,
				texture, color, tex_paint, light_array);
	}

	private void ScanTriangle(Vertex minYVert, Vertex midYVert, 
			Vertex maxYVert, boolean handedness, ImageCG texture, ColorCG color, boolean tex_paint, List<Source> light_array)
	{
		Interpolation gradients = new Interpolation(minYVert, midYVert, maxYVert, light_array.get(0).getLightPosition());
		Edge topToBottom    = new Edge(gradients, minYVert, maxYVert, 0);
		Edge topToMiddle    = new Edge(gradients, minYVert, midYVert, 0);
		Edge middleToBottom = new Edge(gradients, midYVert, maxYVert, 1);

		ScanEdges(gradients, topToBottom, topToMiddle, handedness, texture, color, tex_paint);
		ScanEdges(gradients, topToBottom, middleToBottom, handedness, texture, color,  tex_paint);
	}

	private void ScanEdges(Interpolation gradients, Edge a, Edge b, boolean handedness, ImageCG texture, ColorCG color, boolean tex_paint)
	{
		Edge left = a;
		Edge right = b;
		if(handedness)
		{
			Edge temp = left;
			left = right;
			right = temp;
		}

		int yStart = b.GetYStart();
		int yEnd   = b.GetYEnd();
		for(int j = yStart; j < yEnd; j++)
		{
			DrawLine(gradients, left, right, j, texture, color, tex_paint);
			left.Step();
			right.Step();
		}
	}

	private void DrawLine(Interpolation gradients, Edge left, Edge right, int j, ImageCG texture, ColorCG color, boolean tex_paint)
	{
		int xMin = (int)Math.ceil(left.GetX());
		int xMax = (int)Math.ceil(right.GetX());
		float xDopStep = xMin - left.GetX();
		float texCoordXXStep = gradients.GetTexCoordXXStep();
		float texCoordYXStep = gradients.GetTexCoordYXStep();
		float oneOverZXStep = gradients.GetOneOverZXStep();
		float depthXStep = gradients.GetDepthXStep();
		float lightAmtXStep = gradients.GetLightAmtXStep();

		float texCoordX = left.GetTexCoordX() + texCoordXXStep * xDopStep;
		float texCoordY = left.GetTexCoordY() + texCoordYXStep * xDopStep;
		float oneOnZ = left.GetOneOverZ() + oneOverZXStep * xDopStep;
		float depth = left.GetDepth() + depthXStep * xDopStep;
		float lightAmt = left.GetLightAmt() + lightAmtXStep * xDopStep;
		for(int i = xMin; i < xMax; i++)
		{
                    int index = i + j * GetWidth();
                    if(depth < zBuffer[index])
                    {
                            zBuffer[index] = depth;
                            if (tex_paint) {
                                float z = 1.0f/oneOnZ;
                                int srcX = (int)((texCoordX * z) * (float)(texture.GetWidth() - 1) + 0.5f);
                                int srcY = (int)((texCoordY * z) * (float)(texture.GetHeight() - 1) + 0.5f);
                                CopyPixel(i, j, srcX, srcY, texture, lightAmt);
                            } else {
                                DrawPixelLight(i, j, (byte)0xFF, 
                                              (byte)((int)(color.blue * 255) & 0xFF),
                                              (byte)((int)(color.green * 255) & 0xFF),
                                              (byte)((int)(color.red * 255) & 0xFF),
                                              lightAmt);
                            }
                    }

                    oneOnZ += oneOverZXStep;
                    texCoordX += texCoordXXStep;
                    texCoordY += texCoordYXStep;
                    depth += depthXStep;
                    lightAmt += lightAmtXStep;
		}
	}
        public RenderSceneTriangle(int width, int height)
	{
		super(width, height);
		zBuffer = new float[width * height];
	}
}
