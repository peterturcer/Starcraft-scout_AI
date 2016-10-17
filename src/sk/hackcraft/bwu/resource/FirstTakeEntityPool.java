package sk.hackcraft.bwu.resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FirstTakeEntityPool<E> implements EntityPool<E>
{
	private final Set<E> entities;
	private final Set<E> freeEntities;
	private final Set<E> urgentlyFreeEntities;
	private final Map<E, Record<E>> records;
	
	public FirstTakeEntityPool()
	{
		entities = new HashSet<>();
		freeEntities = new HashSet<>();
		urgentlyFreeEntities = new HashSet<>();
		records = new HashMap<E, Record<E>>();
	}
	
	@Override
	public void add(E entity)
	{
		if (entities.add(entity))
		{
			freeEntities.add(entity);
			urgentlyFreeEntities.add(entity);
		}
	}

	@Override
	public void remove(E entity)
	{
		if (entities.contains(entity))
		{
			releaseEntity(entity, true);
			
			freeEntities.remove(entity);
			urgentlyFreeEntities.remove(entity);
			entities.remove(entity);
		}
	}
	
	@Override
	public Set<E> getAllEntities()
	{
		return entities;
	}
	
	private void releaseEntity(E entity, boolean callListener)
	{
		Record<E> record = records.remove(entity);
		
		if (callListener && record != null)
		{
			record.getListener().entityRemoved(entity);
		}
		
		freeEntities.add(entity);
		urgentlyFreeEntities.add(entity);
	}
	
	@Override
	public Contract<E> createContract(final String name)
	{
		return new Contract<E>()
		{
			@Override
			public String getOwnerName()
			{
				return name;
			}

			@Override
			public void returnEntity(E entity)
			{
				if (!entities.contains(entity))
				{
					throw new IllegalStateException("Entity is managed by this pool.");
				}

				Record<E> record = records.get(entity);
				
				if (record == null)
				{
					throw new IllegalStateException("Entity is not owned by anyone.");
				}
				
				if (record.getContract() != this)
				{
					throw new IllegalStateException("Entity is not owned by this owner.");
				}
				
				releaseEntity(entity, false);
			}
			
			@Override
			public void requestEntity(E entity, ContractListener<E> listener, boolean urgent)
			{
				if (!entities.contains(entity))
				{
					throw new IllegalStateException("Entity is managed by this pool.");
				}
				
				Record<E> record = records.get(entity);
				if (record != null)
				{
					if (!urgent)
					{
						throw new IllegalStateException("Entity is already owned.");
					}
					
					if (record.isUrgent())
					{
						throw new IllegalStateException("Entity is already urgently owned.");
					}
					
					releaseEntity(entity, true);
				}

				Record<E> newRecord = new Record<>(this, listener, urgent);
				
				records.put(entity, newRecord);
				freeEntities.remove(entity);
				
				if (urgent)
				{
					urgentlyFreeEntities.remove(entity);
				}
			}
			
			@Override
			public Set<E> getAcquirableEntities(boolean urgent)
			{
				if (urgent)
				{
					return urgentlyFreeEntities;
				}
				else
				{
					return freeEntities;
				}
			}

			@Override
			public boolean canAcquire(E entity, boolean urgent)
			{
				if (urgent)
				{
					return urgentlyFreeEntities.contains(entity);
				}
				else
				{
					return freeEntities.contains(entity);
				}
			}
		};
	}
	
	private static class Record<E>
	{
		private final Contract<E> contract;
		private final ContractListener<E> listener;
		private final boolean urgent;
		
		public Record(sk.hackcraft.bwu.resource.EntityPool.Contract<E> contract, sk.hackcraft.bwu.resource.EntityPool.ContractListener<E> listener, boolean urgent)
		{
			this.contract = contract;
			this.listener = listener;
			this.urgent = urgent;
		}
		
		public Contract<E> getContract()
		{
			return contract;
		}
		
		public ContractListener<E> getListener()
		{
			return listener;
		}
		
		public boolean isUrgent()
		{
			return urgent;
		}
		
		@Override
		public String toString()
		{
			return String.format("Record %s %s", contract, urgent ? "urgent" : "");
		}
	}
}
