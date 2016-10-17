package sk.hackcraft.bwu;

/**
 * Definition of entities lending contract.
 * @param <E>
 */
public interface EntitiesContract<E>
{
	/**
	 * Set up listener for adding or removing entity.
	 * @param listener listener
	 */
	void setListener(ContractListener<E> listener);

	/**
	 * Called when entity is returned by owner.
	 * @param entity entity to return
	 */
	void returnEntity(E entity);
	
	/**
	 * Listener for notifying owner about lend change.
	 * @param <E>
	 */
	public interface ContractListener<E>
	{
		/**
		 * Called when entity is added to owner.
		 * @param entity added entity
		 */
		void entityAdded(E entity);
		
		/**
		 * Called when entity is removed from owner.
		 * @param entity removed entity
		 */
		void entityRemoved(E entity);
	}
}
