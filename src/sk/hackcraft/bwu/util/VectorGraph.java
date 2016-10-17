package sk.hackcraft.bwu.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Minimap;
import sk.hackcraft.bwu.Vector2D;
import sk.hackcraft.bwu.Vector2DMath;

public class VectorGraph
{
	public interface InformationSystem
	{
		public double getValueFor(Vector2D position);

		public double combineSelfAndSiblings(VertexValue me, VertexValue[] siblings);
	}

	public class VertexValue
	{
		final public Vector2D point;
		final public double value;

		private VertexValue(Vector2D point, double value)
		{
			this.point = point;
			this.value = value;
		}
	}

	private class Vertex
	{
		private Map<InformationSystem, Double> systemValues = new HashMap<>();
		private Vector2D point;
		private Set<Vertex> edges = new HashSet<>();
		private Map<Vertex, Double> distances = new HashMap<>();
		private Map<Vertex, Integer> successors = new HashMap<>();

		public Vertex(Vector2D point)
		{
			this.point = point;
		}
	}

	private List<Vertex> vertices = new ArrayList<>();
	private boolean refreshed = false;

	public VectorGraph(Vector2D... points)
	{
		for (Vector2D point : points)
		{
			vertices.add(new Vertex(point));
		}
	}

	public void connect(int indexA, int indexB)
	{
		vertices.get(indexA).edges.add(vertices.get(indexB));
	}

	public void connectBothWays(int indexA, int indexB)
	{
		connect(indexA, indexB);
		connect(indexB, indexA);
	}

	private void runFloyd()
	{
		for (int i = 0; i < vertices.size(); i++)
		{
			for (int j = 0; j < vertices.size(); j++)
			{
				double distance = Double.POSITIVE_INFINITY;
				if (i == j)
				{
					distance = 0;
				}
				else if (vertices.get(i).edges.contains(vertices.get(j)))
				{
					distance = vertices.get(i).point.sub(vertices.get(j).point).length;
				}

				vertices.get(i).distances.put(vertices.get(j), distance);
				vertices.get(i).successors.put(vertices.get(j), -1);
			}
		}

		for (int k = 0; k < vertices.size(); k++)
		{
			for (int i = 0; i < vertices.size(); i++)
			{
				for (int j = 0; j < vertices.size(); j++)
				{
					double newDistance = vertices.get(i).distances.get(vertices.get(k)) + vertices.get(k).distances.get(vertices.get(j));
					if (newDistance < vertices.get(i).distances.get(vertices.get(j)))
					{
						vertices.get(i).distances.put(vertices.get(j), newDistance);
						vertices.get(i).successors.put(vertices.get(j), k);
					}
				}
			}
		}
	}

	public List<Vector2D> getShortestPath(Vector2D from, Vector2D to)
	{
		if (!refreshed)
		{
			runFloyd();
			refreshed = true;
		}

		int startingIndex = getClosestIndexFor(from);
		int endingIndex = getClosestIndexFor(to);

		if (startingIndex == endingIndex)
		{
			LinkedList<Vector2D> result = new LinkedList<>();
			result.add(to);

			return result;
		}

		List<Vector2D> subResult = getShortestPath(startingIndex, endingIndex);
		subResult.add(to);
		return subResult;
	}

	private List<Vector2D> getShortestPath(int indexFrom, int indexTo)
	{
		if (vertices.get(indexFrom).distances.get(vertices.get(indexTo)) == Double.POSITIVE_INFINITY)
		{
			return null;
		}

		int intermediate = vertices.get(indexFrom).successors.get(vertices.get(indexTo));
		if (intermediate == -1)
		{
			return new LinkedList<>();
		}
		else
		{
			LinkedList<Vector2D> result = new LinkedList<>();

			result.addAll(getShortestPath(indexFrom, intermediate));
			result.add(vertices.get(intermediate).point);
			result.addAll(getShortestPath(intermediate, indexTo));

			return result;
		}
	}

	private int getClosestIndexFor(Vector2D position)
	{
		int index = -1;
		double shortest = Double.MAX_VALUE;

		for (int i = 0; i < vertices.size(); i++)
		{
			double distance = position.sub(vertices.get(i).point).length;

			if (distance < shortest)
			{
				index = i;
				shortest = distance;
			}
		}

		return index;
	}

	public void update(InformationSystem system, int repeatings)
	{
		// ensure that every vertex has a value for this system
		for (Vertex vertex : vertices)
		{
			vertex.systemValues.put(system, system.getValueFor(vertex.point));
		}

		// repeat many times
		for (int repeating = 0; repeating < repeatings; repeating++)
		{
			for (Vertex vertex : vertices)
			{
				VertexValue myValue = new VertexValue(vertex.point, vertex.systemValues.get(system));
				VertexValue[] siblingValues = new VertexValue[vertex.edges.size()];

				Iterator<Vertex> it = vertex.edges.iterator();
				for (int edgeIndex = 0; it.hasNext(); edgeIndex++)
				{
					Vertex edgeVertex = it.next();
					siblingValues[edgeIndex] = new VertexValue(edgeVertex.point, edgeVertex.systemValues.get(system));
				}
				vertex.systemValues.put(system, system.combineSelfAndSiblings(myValue, siblingValues));
			}
		}
	}

	public List<Vector2D> getUphillPath(InformationSystem system, Vector2D origin)
	{
		LinkedList<Vector2D> result = new LinkedList<>();

		Vertex currentVertex = vertices.get(getClosestIndexFor(origin));
		result.add(currentVertex.point);
		double currentValue = currentVertex.systemValues.get(system);

		while (true)
		{
			Vertex betterVertex = null;
			double betterValue = currentValue;
			for (Vertex edge : currentVertex.edges)
			{
				double edgeValue = edge.systemValues.get(system);
				if (edgeValue > betterValue)
				{
					betterVertex = edge;
					betterValue = edgeValue;
				}
			}

			if (betterVertex == null)
			{
				break;
			}
			else
			{
				currentVertex = betterVertex;
				currentValue = betterValue;
				result.add(currentVertex.point);
			}
		}

		removeFirstPathPartIfWrongDirection(result, origin);

		return result;
	}

	private void removeFirstPathPartIfWrongDirection(LinkedList<Vector2D> path, Vector2D origin)
	{
		if (path.size() >= 2)
		{
			Vector2D directionToFirst = path.get(0).sub(origin);
			Vector2D directionFromFirstToSecond = path.get(1).sub(path.get(0));

			double dotProduct = Vector2DMath.dotProduct(directionToFirst, directionFromFirstToSecond);

			if (dotProduct < 0)
			{
				path.removeFirst();
			}
		}
	}

	public void renderGraph(Minimap minimap)
	{
		minimap.setColor(BWColor.White);
		for (Vertex v : vertices)
		{
			for (Vertex e : v.edges)
			{
				minimap.drawLine(v.point, e.point);
			}
		}
	}

	public void renderSystem(Minimap minimap, InformationSystem system)
	{
		for (Vertex v : vertices)
		{
			minimap.drawText(v.point, String.format("%.2f", v.systemValues.get(system)));
		}
	}
}
