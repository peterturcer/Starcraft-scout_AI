package sk.hackcraft.bwu.grid.grids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sk.hackcraft.bwu.Updateable;
import sk.hackcraft.bwu.grid.GridDimension;
import sk.hackcraft.bwu.grid.GridPoint;
import sk.hackcraft.bwu.grid.processors.FloodFillProcessor;

/**
 * Matrix-based dynamic potential fields layer. After adding
 * {@link FieldEmitter} objects, they will generate potential fields around
 * them. These potential fields are dynamic, that means, they will be updated
 * with every emitter position change. Generated potential fields are always positive.
 * 
 * @param <E> type of potential field emitter
 */
public class PotentialFieldMatrixLayer<E> extends MatrixGrid implements Updateable
{
	private final List<FieldEmitter<E>> fieldEmitters;
	private final Map<FieldEmitter<E>, CacheFieldEmitter> cacheEmitters;

	/**
	 * Constructs new layer with given dimension.
	 * @param dimension dimension of layer
	 */
	public PotentialFieldMatrixLayer(GridDimension dimension)
	{
		super(dimension);

		this.fieldEmitters = new ArrayList<>();
		this.cacheEmitters = new HashMap<>();
	}

	/**
	 * Ads {@link FieldEmitter} for emitting potential field.
	 * @param emitter emitter to add
	 */
	public void addFieldEmitter(FieldEmitter<E> emitter)
	{
		fieldEmitters.add(emitter);

		CacheFieldEmitter cacheFieldEmitter = new CacheFieldEmitter(emitter);

		cacheFieldEmitter.update(emitter);

		cacheEmitters.put(emitter, cacheFieldEmitter);
	}

	/**
	 * Removes {@link FieldEmitter} from this layer.
	 * @param emitter emitter to remove
	 */
	public void removeFieldEmitter(FieldEmitter<E> emitter)
	{
		fieldEmitters.remove(emitter);
		CacheFieldEmitter cacheEmitter = cacheEmitters.remove(emitter);

		Set<GridPoint> defillPoints = new HashSet<>();
		defillPoints.add(cacheEmitter.getPosition());
		FloodFillProcessor.defillGradient(this, 0, defillPoints);
	}

	@Override
	public void update()
	{
		Set<GridPoint> defillPoints = new HashSet<>();
		Set<GridPoint> fillPoints = new HashSet<>();

		for (FieldEmitter<E> emitter : fieldEmitters)
		{
			CacheFieldEmitter cacheFieldEmitter = cacheEmitters.get(emitter);

			if (!cacheFieldEmitter.equals(emitter))
			{
				GridPoint fillPoint = emitter.getPosition();
				set(fillPoint, emitter.getValue());
				fillPoints.add(fillPoint);

				defillPoints.add(cacheFieldEmitter.getPosition());

				cacheFieldEmitter.update(emitter);
			}
		}

		Set<GridPoint> borderPoints = FloodFillProcessor.defillGradient(this, 0, defillPoints);

		fillPoints.addAll(borderPoints);

		FloodFillProcessor.fillGradient(this, fillPoints);
	}

	/**
	 * Definition of field emitter.
	 * @param <E> emitting object
	 */
	public interface FieldEmitter<E>
	{
		/**
		 * Returns source of this potential field.
		 * @return
		 */
		E getSource();

		/**
		 * Returns value of potential field at emitter position. This value is base for potential field and is lowered with distance from emitter center.
		 * @return value of potential field at emitter position
		 */
		int getValue();

		/**
		 * Returns position of this emitter relative to layer. 
		 * @return positon of this emitter
		 */
		GridPoint getPosition();
	}

	private class CacheFieldEmitter implements FieldEmitter<E>
	{
		private final E source;

		private int value = 0;
		private GridPoint position = GridPoint.ORIGIN;

		public CacheFieldEmitter(FieldEmitter<E> emitter)
		{
			this.source = emitter.getSource();

			update(emitter);
		}

		public void update(FieldEmitter<E> sourceEmitter)
		{
			this.value = sourceEmitter.getValue();
			this.position = sourceEmitter.getPosition();
		}

		@Override
		public E getSource()
		{
			return source;
		}

		@Override
		public int getValue()
		{
			return value;
		}

		@Override
		public GridPoint getPosition()
		{
			return position;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof FieldEmitter))
			{
				return false;
			}

			@SuppressWarnings("unchecked")
			FieldEmitter<E> emitter = (FieldEmitter<E>) obj;

			return emitter.getValue() == value && emitter.getPosition().equals(position);
		}

		@Override
		public int hashCode()
		{
			return value * 31 + position.hashCode();
		}
	}
}
