package bibliothek.gui.dock;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItemStrategyListener;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.property.DynamicPropertyFactory;

/**
 * An {@link ExpandableToolbarItemStrategy} is a strategy that allows to expand
 * and to shrink items of a toolbar.
 * 
 * @author Benjamin Sigg
 */
public interface ExpandableToolbarItemStrategy{

	/** an identifier to exchange the strategy */
	public static final PropertyKey<ExpandableToolbarItemStrategy> STRATEGY = new PropertyKey<ExpandableToolbarItemStrategy>(
			"expandable toolbar item strategy",
			new DynamicPropertyFactory<ExpandableToolbarItemStrategy>(){
				@Override
				public ExpandableToolbarItemStrategy getDefault(
						PropertyKey<ExpandableToolbarItemStrategy> key,
						DockProperties properties ){
					return new DefaultExpandableToolbarItemStrategy();
				}
			}, true);

	/**
	 * Called if this strategy is used by <code>controller</code>.
	 * 
	 * @param controller
	 *            the controller using this strategy
	 */
	public void install( DockController controller );

	/**
	 * Called if this strategy is no longer used by <code>controller</code>.
	 * 
	 * @param controller
	 *            the controller which is no longer using this strategy
	 */
	public void uninstall( DockController controller );

	/**
	 * Tells whether the {@link Dockable} <code>item</code> can have the state
	 * <code>state</code>.
	 * 
	 * @param item
	 *            the item to check
	 * @param state
	 *            the state that might be applied to <code>item</code>
	 * @return <code>true</code> if this strategy knows how to change the state
	 *         of <code>item</code> to <code>state</code>
	 */
	public boolean isEnabled( Dockable item, ExpandedState state );

	/**
	 * Gets the current state <code>item</code> has.
	 * 
	 * @param item
	 *            some {@link Dockable}
	 * @return the state or <code>null</code> if <code>item</code> is not
	 *         supported by this strategy.
	 */
	public ExpandedState getState( Dockable item );

	/**
	 * Changes the state of <code>item</code> to <code>state</code>.
	 * 
	 * @param item
	 *            the item whose state is changed
	 * @param state
	 *            the new state, this is a state which is
	 *            {@link #isEnabled(Dockable, ExpandedState) enabled}
	 */
	public void setState( Dockable item, ExpandedState state );

	/**
	 * Adds a listener to this strategy, the listener is to be informed if the
	 * state of an item changes.
	 * 
	 * @param listener
	 *            the new listener, not <code>null</code>
	 */
	public void addExpandedListener(
			ExpandableToolbarItemStrategyListener listener );

	/**
	 * Removes a listener from this strategy.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeExpandedListener(
			ExpandableToolbarItemStrategyListener listener );
}
