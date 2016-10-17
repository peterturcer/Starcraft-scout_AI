package sk.hackcraft.bwu;

import java.util.Collection;

public abstract class EntitiesServerContract<E> implements EntitiesContract<E>
{
	private ContractListener<E> listener;
	
	public void addEntity(E entity)
	{
		listener.entityAdded(entity);
	}
	
	public void addEntities(Collection<E> entities)
	{
		for (E entity : entities)
		{
			listener.entityAdded(entity);
		}
	}
	
	public void removeEntity(E entity)
	{
		listener.entityRemoved(entity);
	}
	
	public void removeEntities(Collection<E> entities)
	{
		for (E entity : entities)
		{
			listener.entityRemoved(entity);
		}
	}
	
	public abstract void entityReturned(E entity);
	
	@Override
	public void returnEntity(E entity)
	{
		entityReturned(entity);
	}

	@Override
	public void setListener(ContractListener<E> listener)
	{
		this.listener = listener;
	}
}
