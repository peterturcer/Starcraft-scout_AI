package sk.hackcraft.bwu.resource;

import java.util.Set;

/**
 * Pool of entities, providing request/return mechanism.
 * 
 * @param <E>
 *            entity type to manage
 */
public interface EntityPool<E>
{
	/**
	 * Adds entity to this pool. Nothing happens if entity is already managed by this pool.
	 * 
	 * @param entity
	 *            entity to add
	 */
	void add(E entity);

	/**
	 * Removes entity from this pool. Nothing happens if entity is not managed by this pool.
	 * 
	 * @param entity
	 *            entity to remove
	 */
	void remove(E entity);

	/**
	 * Returns set of all entities managed by this pool (free or owned).
	 * 
	 * @return set of entities managed by this pool
	 */
	Set<E> getAllEntities();

	/**
	 * Creates new contract.
	 * 
	 * @param name
	 *            name of the contract
	 * @return newly created contract
	 */
	Contract<E> createContract(String name);

	/**
	 * Contract listener for notifying conract.
	 * 
	 * @param <E>
	 *            entity type of contract
	 */
	public interface ContractListener<E>
	{
		/**
		 * Called when entity was externally removed from contract. Contract
		 * have to immediately release all references to entity and stop using
		 * it. It doesn't have to clean entity state.
		 * 
		 * @param entity
		 *            entity which is being removed
		 */
		void entityRemoved(E entity);
	}

	/**
	 * Contract representing ownership of entities. It also provides mechanism
	 * for querying available entities, requsting entity and returning entity.
	 * 
	 * @param <E>
	 *            entity type of contract
	 */
	public interface Contract<E>
	{
		/**
		 * Returns name of the owner.
		 * 
		 * @return name of the owner
		 */
		String getOwnerName();

		/**
		 * Returns all entities accessible to this contract. If urgent is
		 * <code>true</code>, pool will also returns units which are already
		 * owned, but not urgently.
		 * 
		 * @param urgent
		 *            if set should contains even not-urgently owned entities
		 * @return all entities accessible to this contract
		 */
		Set<E> getAcquirableEntities(boolean urgent);
		
		/**
		 * Checks if it's possible to acquire specified entity.
		 * 
		 * @param entity entity to acquire
		 * @param urgent if request is urgent
		 * @return <code>true</code> if it's possible to acquire entity, <code>false</code> otherwise
		 */
		boolean canAcquire(E entity, boolean urgent);

		/**
		 * Request specified entity. Caller can call this only with entities
		 * retrieved from {@link #getAcquirableEntities()} method and in the
		 * same frame in which he retrieved them. Urgent request should be made
		 * only if entity is needed for small amount of time, or is needed for
		 * critical task.
		 * 
		 * @param entity
		 *            requested entity
		 * @param listener
		 *            listener for notifying entity owner
		 * @param urgent
		 *            if request is urgent
		 */
		void requestEntity(E entity, ContractListener<E> listener, boolean urgent);

		/**
		 * Returns entity back to the pool.
		 * 
		 * @param entity
		 *            entity to return
		 */
		void returnEntity(E entity);
	}
}
