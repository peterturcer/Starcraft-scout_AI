package sk.hackcraft.bwu.moving;

import java.util.HashSet;
import java.util.Set;

import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Drawable;
import sk.hackcraft.bwu.Game;
import sk.hackcraft.bwu.Graphics;
import sk.hackcraft.bwu.Minimap;
import sk.hackcraft.bwu.Updateable;
import sk.hackcraft.bwu.Vector2D;
import sk.hackcraft.bwu.selection.UnitSelector;
import sk.hackcraft.bwu.selection.UnitSelector.BooleanSelector;
import sk.hackcraft.bwu.selection.UnitSet;
import sk.hackcraft.bwu.selection.aggregators.MeanVector2DAggregator;
import sk.hackcraft.bwu.util.Clustering;
import sk.hackcraft.bwu.util.Clustering.Cluster;

public class FlockingManager implements Updateable, Drawable
{
	public class Group
	{
		private Cluster cluster;
		
		private Vector2D meanTarget;
		
		private Group(Cluster cluster)
		{
			this.cluster = cluster;
			
			meanTarget = (new MeanVector2DAggregator(UnitSelector.TARGET_POSITION)).aggregate(cluster);
		}
	}
	
	private Game game;
	private UnitSet units;
	private BooleanSelector unitSelector;
	private Clustering clustering;
	
	private HashSet<Group> groups = new HashSet<>();
	
	public FlockingManager(Game game, BooleanSelector unitSelector)
	{
		this.unitSelector = unitSelector;
		this.game = game;
		this.units = new UnitSet();
		this.clustering = new Clustering(game.getMap(), 1);
	}
	
	@Override
	public void update()
	{
		units = game.getMyUnits().where(unitSelector);
		if(game.getFrameCount() % 15 == 7)
		{
			clustering.updateFor(units);
			
			groups.clear();
			for(Cluster cluster : clustering.getClusters())
			{
				groups.add(new Group(cluster));
			}
		}
	}
	
	public Set<Group> getGroups()
	{
		return new HashSet<>(groups);
	}

	@Override
	public void draw(Graphics graphics)
	{
		Minimap minimap = graphics.createMinimap(game.getMap(), new Vector2D(120, 50), new Vector2D(300, 200));
		minimap.setColor(BWColor.White);
		minimap.drawBounds();
		clustering.drawOn(minimap);
		
		minimap.setColor(BWColor.Yellow);
		for(Group group : groups)
		{
			minimap.drawLine(group.cluster.getPosition(), group.meanTarget);
			minimap.fillCircle(group.meanTarget, 3);
		}
	}
}
