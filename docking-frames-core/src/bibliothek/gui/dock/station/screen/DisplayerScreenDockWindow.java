/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.station.screen;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.BackgroundPaint;

/**
 * A window that uses a {@link DockableDisplayer} to show the {@link Dockable}.
 * @author Benjamin Sigg
 */
public abstract class DisplayerScreenDockWindow implements ScreenDockWindow {
    /** the owner of this station */
    private ScreenDockStation station;
    
    /** the dockable shown on this station */
    private StationChildHandle handle;
    
    /** all listeners known to this window */
    private List<ScreenDockWindowListener> listeners = new ArrayList<ScreenDockWindowListener>();
    
    /** a listener to the current {@link DockableDisplayer} */
    private DockableDisplayerListener displayerListener = new DockableDisplayerListener(){
    	public void discard( DockableDisplayer displayer ){
	    	discardDisplayer();	
    	}
    };
    
    /** the controller in whose realm this window works */
    private DockController controller;
    
    /** whether the {@link DockTitle} should be shown */
    private boolean showTitle = true;
    
    /** strategy for handling fullscreen mode */
    private ScreenDockFullscreenStrategy strategy;
    
    /** boundaries used in normal mode */
    private Rectangle normalBounds;
    
    /** the algorithm that paints the background */
    private Background background = new Background();
    
    /**
     * Creates a new window
     * @param station the owner of this window, not <code>null</code>
     */
    public DisplayerScreenDockWindow( ScreenDockStation station  ){
        if( station == null )
            throw new IllegalArgumentException( "station must not be null" );
        this.station = station;
    }
    
    public void addScreenDockWindowListener( ScreenDockWindowListener listener ){
    	listeners.add( listener );
    }
    
    public void removeScreenDockWindowListener( ScreenDockWindowListener listener ){
    	listeners.remove( listener );
    }
    
    /**
     * Gets a list of all listeners that are currently registered.
     * @return all listeners
     */
    protected ScreenDockWindowListener[] listeners(){
    	return listeners.toArray( new ScreenDockWindowListener[ listeners.size() ] );
    }
    
    /**
     * Informs all listeners that the fullscreen state changed
     */
    protected void fireFullscreenChanged(){
    	for( ScreenDockWindowListener listener : listeners() ){
    		listener.fullscreenStateChanged( this );
    	}
    }
    
    /**
     * Informs all listeners that the visibility state changed
     */
    protected void fireVisibilityChanged(){
    	for( ScreenDockWindowListener listener : listeners() ){
    		listener.visibilityChanged( this );
    	}
    }
    
    /**
     * Informs all listeners that the current size or position changed
     */
    protected void fireShapeChanged(){
    	for( ScreenDockWindowListener listener : listeners() ){
    		listener.shapeChanged( this );
    	}
    }
    
    /**
     * Forces the subclass of this window to show <code>displayer</code>. Only
     * one displayer should be shown at any time. A new displayer replaces
     * an old one. 
     * @param displayer the displayer to show or <code>null</code> to remove
     * the current displayer
     */
    protected abstract void showDisplayer( DockableDisplayer displayer );
    
    /**
     * Gets the component on which {@link ScreenDockWindow#setWindowBounds(java.awt.Rectangle, boolean)}
     * is applied.
     * @return the base component
     */
    protected abstract Component getWindowComponent();
    
    /**
     * Sets the algorithm that paints the background of this window.
     * @param background the algorithm, may be <code>null</code>
     */
    protected abstract void setBackground( BackgroundAlgorithm background );

    /**
     * Sets whether the {@link DockTitle} should be shown or not.
     * @param showTitle <code>true</code> if the title should be visible,
     * <code>false</code> otherwise
     */
    public void setShowTitle( boolean showTitle ) {
        if( this.showTitle != showTitle ){
            this.showTitle = showTitle;
            
            if( handle != null ){
            	if( showTitle ){
            		handle.setTitleRequest( station.getTitleVersion() );
            	}
            	else{
            		handle.setTitleRequest( null );
            	}
            }
        }
    }
    
    /**
     * Tells whether the {@link DockTitle} is generally shown.
     * @return <code>true</code> if the title is shown
     */
    public boolean isShowTitle() {
        return showTitle;
    }
    
    public Dockable getDockable() {
    	if( handle == null )
    		return null;
    	return handle.getDockable();
    }

    public DockableDisplayer getDockableDisplayer(){
	    if( handle == null ){
	    	return null;
	    }
	    return handle.getDisplayer();
    }
    
    public void setDockable( Dockable dockable ) {
    	// remove old displayer
        if( handle != null ){
        	DockableDisplayer displayer = handle.getDisplayer();
        	displayer.removeDockableDisplayerListener( displayerListener );
            handle.destroy();
            handle = null;
        }
        
        // add new displayer
        DockableDisplayer displayer = null;
        
        if( dockable != null ){
        	handle = new StationChildHandle( station, station.getDisplayers(), dockable, showTitle ? station.getTitleVersion() : null );
        	handle.updateDisplayer();
            displayer = handle.getDisplayer();
            displayer.addDockableDisplayerListener( displayerListener );
        }
        
        showDisplayer( displayer );
    }
    
    /**
     * Replaces the current {@link DockableDisplayer} with a new instance.
     */
    protected void discardDisplayer(){
    	DockableDisplayer displayer = handle.getDisplayer();
    	displayer.removeDockableDisplayerListener( displayerListener );
    	handle.updateDisplayer();
    	displayer = handle.getDisplayer();
    	displayer.addDockableDisplayerListener( displayerListener );
    	showDisplayer( displayer );
    }
    
    public void setFullscreenStrategy( ScreenDockFullscreenStrategy strategy ) {
	    this.strategy = strategy;	
    }
    
    public boolean isFullscreen() {
    	if( strategy == null ){
    		if( isVisible() ){
    			throw new IllegalStateException( "no strategy available" );
    		}
    		else{
    			return false;
    		}
    	}
    	return strategy.isFullscreen( this );
    }
    
    public void setFullscreen( boolean fullscreen ) {
    	if( strategy == null ){
    		throw new IllegalStateException( "no strategy available" );
    	}
    	boolean state = isFullscreen();
    	if( state != fullscreen ){
    		strategy.setFullscreen( this, fullscreen );
    		fireFullscreenChanged();
    	}
    }
    
    public void setNormalBounds( Rectangle bounds ) {
	    this.normalBounds = bounds;	
    }
    
    public Rectangle getNormalBounds() {
	    return normalBounds;
    }

    public void setController( DockController controller ) {
        // remove old DockTitle
    	if( handle != null ){
    		if( this.controller != null ){
    			handle.setTitleRequest( null );
    		}
    	}
    	
    	background.setController( controller );
        this.controller = controller;
        
        // create new DockTitle
        if( handle != null ){
            if( this.controller != null && isShowTitle() ){
            	handle.setTitleRequest( station.getTitleVersion() );
            }
        }
    }
    
    public Point getTitleCenter(){
    	if( handle == null ){
    		return null;
    	}
    	
    	DockableDisplayer displayer = handle.getDisplayer();
    	if( displayer == null ){
    		return null;
    	}
    	
    	DockTitle title = displayer.getTitle();
    	if( title == null ){
    		return null;
    	}
    	
    	Component base = getWindowComponent();
        if( base == null )
            return null;
        
        Component titleComponent = title.getComponent();
        
        Point result = new Point( 0, 0 );
        result = SwingUtilities.convertPoint( titleComponent, result, base );
        result.x += titleComponent.getWidth() / 2;
        result.y += titleComponent.getHeight() / 2;
        return result;
    }
    
    public Point getOffsetDrop() {
    	if( handle == null )
    		return null;
    	
    	DockableDisplayer displayer = handle.getDisplayer();
    	
    	if( displayer == null )
            return null;
        
        Insets insets = getDockableInsets();
        
        return new Point( insets.left, insets.top );
    }
    
    public Point getOffsetMove() {
    	if( handle == null )
    		return null;
    	
    	DockableDisplayer displayer = handle.getDisplayer();
    	
        if( displayer == null )
            return null;
        
        DockTitle title = displayer.getTitle();
        if( title == null )
            return null;
        
        Component base = getWindowComponent();
        if( base == null )
            return null;
        
        Point zero = new Point( 0, 0 );
        zero = SwingUtilities.convertPoint( title.getComponent(), zero, base );
        return zero;
    }
    
    public boolean inTitleArea( int x, int y ){
    	if( handle == null )
    		return false;
    	
    	DockableDisplayer displayer = handle.getDisplayer();
    	
        if( displayer == null )
            return false;
        
        Point point = new Point( x, y );
        SwingUtilities.convertPointFromScreen( point, displayer.getComponent() );
        return displayer.titleContains( point.x, point.y );
    }
    
    public boolean inCombineArea( int x, int y ) {
    	return inTitleArea( x, y );
    }
    
    public boolean contains( int x, int y ){
	    Component component = getWindowComponent();
	    Point point = new Point( x, y );
	    SwingUtilities.convertPointFromScreen( point, component );
	    return component.contains( point );
    }
    
    /**
     * Gets the controller in whose realm this window is used.
     * @return the controller, can be <code>null</code>
     */
    public DockController getController() {
        return controller;
    }
    
    public ScreenDockStation getStation(){
        return station;
    }
    
    /**
     * The algorithm that paints the background of this window.
     * @author Benjamin Sigg
     */
    protected class Background extends BackgroundAlgorithm implements ScreenDockWindowBackgroundComponent{
    	public Background(){
    		super( ScreenDockWindowBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".station.screen" );
    	}
    	
    	@Override
    	public void set( BackgroundPaint value ){
    		super.set( value );
    		if( getPaint() == null ){
    			setBackground( null );
    		}
    		else{
    			setBackground( this );
    		}
    	}
    	
		public ScreenDockWindow getWindow(){
			return DisplayerScreenDockWindow.this;
		}

		public DockStation getStation(){
			return getWindow().getStation();
		}

		public Component getComponent(){
			return getWindowComponent();
		}
    }
}
