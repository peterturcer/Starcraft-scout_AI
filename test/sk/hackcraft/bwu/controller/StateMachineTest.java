package sk.hackcraft.bwu.controller;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sk.hackcraft.bwu.Updateable;
import sk.hackcraft.bwu.controller.StateMachine.State;
import sk.hackcraft.bwu.controller.StateMachine.StateTransition;

public class StateMachineTest
{
	private final StateMachine stateMachine;
	
	private final State idle, moving, burrowed, burrowing, unburrowing;
	
	private final Unit unit;
	
	public StateMachineTest()
	{
		unit = new Unit();

		idle = new State()
		{
			@Override
			public void update()
			{
				if (!unit.isIdle())
				{
					unit.stop();
				}
			}
			
			@Override
			public String toString()
			{
				return "IDLE";
			}
		};
		
		moving = new State()
		{
			@Override
			public void update()
			{
				if (!unit.isMoving())
				{
					unit.move();
				}
			}
			
			@Override
			public String toString()
			{
				return "MOVING";
			}
		};
		
		burrowed = new State()
		{
			@Override
			public void update()
			{
			}
			
			@Override
			public String toString()
			{
				return "BURROWED";
			}
		};
		
		burrowing = new State()
		{
			@Override
			public void update()
			{
				if (!unit.isBurrowed())
				{
					unit.burrow();
				}
			}
			
			@Override
			public String toString()
			{
				return "BURROWING";
			}
		};
		
		unburrowing = new State()
		{
			@Override
			public void update()
			{
				if (unit.isBurrowed())
				{
					unit.unburrow();
				}
			}
			
			@Override
			public String toString()
			{
				return "UNBURROWING";
			}
		};

		this.stateMachine = new StateMachine(idle);
		
		stateMachine
		.addState(idle)
		.addState(moving)
		.addState(burrowed)
		.addState(burrowing)
		.addState(unburrowing);
		
		stateMachine
		.addTransition(idle, moving, StateTransition.ALWAYS)
		.addTransition(moving, idle, StateTransition.ALWAYS)
		.addTransition(idle, burrowing, StateTransition.ALWAYS)
		.addTransition(burrowing, burrowed, new StateTransition()
		{
			@Override
			public boolean isPossible()
			{
				return unit.isBurrowed();
			}
		})
		.addTransition(burrowed, unburrowing, StateTransition.ALWAYS)
		.addTransition(unburrowing, idle, new StateTransition()
		{
			@Override
			public boolean isPossible()
			{
				return !unit.isBurrowed();
			}
		});
	}

	@Test
	public void testChangeState()
	{
		stateMachine.changeState(moving);
		stateMachine.update();
		unit.update();
		assertTrue(unit.isMoving());
		
		stateMachine.changeState(idle);
		stateMachine.update();
		unit.update();
		assertTrue(unit.isIdle());
		
		stateMachine.changeState(burrowing);
		stateMachine.update();
		assertTrue(unit.isBurrowing());
		
		stateMachine.changeState(burrowed);
		stateMachine.update();
		unit.update();
		stateMachine.update();
		assertTrue(unit.isBurrowed());
		
		stateMachine.changeState(unburrowing);
		stateMachine.update();
		assertTrue(unit.isUnburrowing());
		
		unit.update();
		stateMachine.update();
		assertTrue(unit.isIdle());
	}
	
	private class Unit implements Updateable
	{
		private boolean moving, burrowing, unburrowing, burrowed;
		
		private void stop()
		{
			this.moving = false;
		}
		
		private void move()
		{
			this.moving = true;
		}
		
		private void burrow()
		{
			burrowing = true;
		}
		
		private void unburrow()
		{
			unburrowing = true;
		}
		
		private boolean isMoving()
		{
			return moving;
		}
		
		private boolean isIdle()
		{
			return !moving;
		}
		
		private boolean isBurrowed()
		{
			return burrowed;
		}
		
		public boolean isBurrowing()
		{
			return burrowing;
		}
		
		public boolean isUnburrowing()
		{
			return unburrowing;
		}
		
		@Override
		public void update()
		{
			if (burrowing)
			{
				burrowing = false;
				burrowed = true;
			}
			
			if (unburrowing)
			{
				unburrowing = false;
				burrowed = false;
			}
		}
	}
}
