package sk.hackcraft.bwu.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jnibwapi.Map;
import jnibwapi.Unit;
import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Minimap;
import sk.hackcraft.bwu.Vector2D;
import sk.hackcraft.bwu.Vector2DMath;
import sk.hackcraft.bwu.selection.UnitSet;

/**
 * Class for doing k-mean clustering among the units provided in each update.
 * The criterion for k-mean clustering is to minimize the sum of all
 * <code>1/(DISTANCE_FROM_CLUSTER_CENTER)^2</code> values for each unit in each cluster.
 * 
 * @author nixone
 *
 */
public class Clustering
{
	/**
	 * One cluster represents a group of units grouped together
	 * to fit the clustering criteria.
	 * 
	 * @author nixone
	 *
	 */
	@SuppressWarnings("serial")
	static public class Cluster extends UnitSet
	{
		private Vector2D position;
		private Object representative = new Object();

		private Cluster(Vector2D position)
		{
			this.position = position;
		}

		private void setPosition(Vector2D position)
		{
			this.position = position;
		}

		private void setPositionToSquareDistanceCenter()
		{
			if (size() > 0)
			{
				Vector2D position = Vector2D.ZERO;
				float cumulation = 0;

				for (Unit unit : this)
				{
					float distance = unit.getPositionVector().sub(this.position).length;

					float scale = 1 / (distance * distance);

					if (Float.isInfinite(scale) || Float.isNaN(scale))
					{
						continue;
					}

					position = position.add(unit.getPositionVector().scale(scale));

					cumulation += scale;
				}

				if (cumulation > 0.0000000001)
				{
					this.position = position.scale(1 / cumulation);
				}
			}
		}

		/**
		 * Optimized center of the cluster after doing the k-mean clustering.
		 * 
		 * @return center of cluster
		 */
		public Vector2D getPosition()
		{
			return position;
		}

		private double getSquareDistanceError()
		{
			if (this.isEmpty())
			{
				return 0;
			}

			double cumulative = 0;

			for (Unit unit : this)
			{
				double distance = unit.getPositionVector().sub(position).length;
				cumulative += distance * distance;
			}

			return cumulative / this.size();
		}

		@Override
		public int hashCode()
		{
			return representative.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof Clustering))
			{
				return false;
			}

			return ((Cluster) o).representative == representative;
		}
	}

	private Map map;
	private HashSet<Cluster> clusters = new HashSet<>();
	private double clusterPrice = 40;
	private double clusterCountChangePrice = 500000;

	private int clusterImprovementCount = 10;

	/**
	 * Creates a clusterer for a certain map size and with initial k for k-mean clustering set.
	 * 
	 * @param map map for clustering
	 * @param initialClusterCount initial cluster count (k in k-means).
	 */
	public Clustering(Map map, int initialClusterCount)
	{
		this.map = map;

		for (int i = 0; i < initialClusterCount; i++)
		{
			addCluster();
		}
	}
	
	private void moveClustersRandomly()
	{
		for (Cluster cluster : clusters)
		{
			cluster.setPosition(Vector2DMath.randomVector().scale(map));
		}
	}

	/**
	 * Updates the clustering (creates, destroys or moves the clusters around for best cluster positions)
	 * for certain units (other units are not considered than units provided in parameters).
	 * 
	 * @param units units that are to be considered in k-mean clustering
	 */
	public void updateFor(UnitSet units)
	{
		removeNonExistingUnitsFromClusters(units);

		doOneIteration(units);
		double costWithoutAnything = getCombinedCostOfClusters();

		addCluster();
		doOneIteration(units);

		double costWithNewCluster = getCombinedCostOfClusters() + clusterCountChangePrice;

		if (costWithNewCluster < costWithoutAnything)
		{
			return;
		}

		removeCluster();

		if (clusters.size() <= 1)
		{
			return;
		}
		removeCluster();

		doOneIteration(units);
		double costWithClusterRemoval = getCombinedCostOfClusters() + clusterCountChangePrice;

		if (costWithClusterRemoval > costWithoutAnything)
		{
			addCluster();
		}

		doOneIteration(units);
	}

	private void addCluster()
	{
		clusters.add(new Cluster(Vector2DMath.randomVector().scale(map)));
	}

	private void removeCluster()
	{
		if (!clusters.isEmpty())
		{
			Iterator<Cluster> it = clusters.iterator();
			it.next();
			it.remove();
		}
	}

	private double getCombinedCostOfClusters()
	{
		double combined = 0;
		for (Cluster cluster : clusters)
		{
			combined += cluster.getSquareDistanceError();
		}
		return combined + clusterPrice * Math.pow(2, clusters.size());
	}

	private void doOneIteration(UnitSet units)
	{
		moveClustersRandomly();
		doImprovements(units);
	}

	private void doImprovements(UnitSet units)
	{
		for (int i = 0; i < clusterImprovementCount; i++)
		{
			recomputeClustersCenters();
			clearClusters();
			assignUnitsToBestClusters(units);
		}
	}

	private void assignUnitsToBestClusters(UnitSet units)
	{
		for (Unit unit : units)
		{
			findBestClusterFor(unit).add(unit);
		}
	}

	private void recomputeClustersCenters()
	{
		for (Cluster cluster : clusters)
		{
			cluster.setPositionToSquareDistanceCenter();
		}
	}

	private void clearClusters()
	{
		for (Cluster cluster : clusters)
		{
			cluster.clear();
		}
	}

	private void removeNonExistingUnitsFromClusters(UnitSet units)
	{
		for (Cluster cluster : clusters)
		{
			Iterator<Unit> it = cluster.iterator();

			while (it.hasNext())
			{
				if (!units.contains(it.next()))
				{
					it.remove();
				}
			}
		}
	}

	private Cluster findBestClusterFor(Unit unit)
	{
		Cluster bestCluster = null;
		double bestClusterDistance = 0;

		for (Cluster cluster : clusters)
		{
			double clusterDistance = cluster.getPosition().sub(unit.getPositionVector()).length;

			if (bestCluster == null || bestClusterDistance > clusterDistance)
			{
				bestCluster = cluster;
				bestClusterDistance = clusterDistance;
			}
		}

		return bestCluster;
	}

	/**
	 * Get current clusters snapshot
	 * @return clusters snapshot
	 */
	public Set<Cluster> getClusters()
	{
		return new HashSet<>(clusters);
	}
	
	/**
	 * Draws the current clustering (with cluster centers and units correspondence) to the
	 * minimap.
	 * 
	 * @param minimap
	 * 			to be drawn to
	 */
	public void drawOn(Minimap minimap)
	{
		for (Cluster cluster : clusters)
		{
			if (cluster.isEmpty())
			{
				continue;
			}
			
			minimap.setColor(BWColor.Blue);
			
			for (Unit unit : cluster)
			{
				minimap.drawLine(unit.getPositionVector(), cluster.getPosition());
			}

			minimap.setColor(BWColor.Red);
			minimap.fillCircle(cluster.getPosition(), 5);
		}
	}
}
