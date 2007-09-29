/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.DockStation;

/**
 * @author Janni Kovacs
 */
public class EclipseStationPaint implements StationPaint {
	
	public void drawInsertionLine(Graphics g, DockStation station, int x1, int y1, int x2, int y2) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(2f));
		g.drawLine(x1, y1, x2, y2);
	}

	public void drawDivider(Graphics g, DockStation station, Rectangle bounds) {
		if (station instanceof SplitDockStation && !((SplitDockStation) station).isContinousDisplay()) {
			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}

	public void drawInsertion(Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(2f));
		g.drawRect(dockableBounds.x, dockableBounds.y, dockableBounds.width, dockableBounds.height);
	}
}
