package com.pigletlogic.spaceoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.tablelayout.Cell;
import com.pigletlogic.util.Constants;

public class PagedScrollPane extends ScrollPane
{
	private final String TAG = PagedScrollPane.class.getName();

	private boolean wasPanDragFling = false;

	private float pageSpacing;
	
	private float currentScrollX = 0;

	private Table content;

	public PagedScrollPane()
	{
		super(null);
		setup();
	}

	public PagedScrollPane(Skin skin)
	{
		super(null, skin);
		setup();
	}

	public PagedScrollPane(Skin skin, String styleName)
	{
		super(null, skin, styleName);
		setup();
	}

	public PagedScrollPane(Actor widget, ScrollPaneStyle style)
	{
		super(null, style);
		setup();
	}

	private void setup()
	{
		content = new Table();
		content.defaults().space(50);
		super.setWidget(content);
	}

	public void addPages(Actor... pages)
	{
		for (Actor page : pages)
		{
			content.add(page).expandY().fillY();
		}
	}

	public void addPage(Actor page)
	{
		content.add(page).expandY().fillY();
	}

	@Override
	public void act(float delta)
	{
		super.act(delta);
		if (wasPanDragFling && ! isPanning() && ! isDragging() && ! isFlinging())
		{
			wasPanDragFling = false;
			scrollToPage();
		}
		else
		{
			if (isPanning() || isDragging() || isFlinging())
			{
				wasPanDragFling = true;
			}
		}
	}

	@Override
	public void setWidget(Actor widget)
	{
		// throw new
		// UnsupportedOperationException("Use PagedScrollPane#addPage.");
	}

	@Override
	public void setWidth(float width)
	{
		super.setWidth(width);
		if (content != null)
		{
			for (Cell cell : content.getCells())
			{
				cell.width(width);
			}
			content.invalidate();
		}
	}

	public void setPageSpacing(float pageSpacing)
	{
		if (content != null)
		{
			content.defaults().space(pageSpacing);
			for (Cell cell : content.getCells())
			{
				cell.space(pageSpacing);
			}
			content.invalidate();
		}
	}

	private void scrollToPage()
	{
		final float width = getWidth();
		final float scrollX = getScrollX();
		final float maxX = getMaxX();
		
		Gdx.app.debug(TAG, "velX: + " + getVelocityX());
		Gdx.app.debug(TAG, "velX: + " + getVisualScrollX());
		
		//Gdx.app.debug(TAG, "scrollX: " + scrollX);

		if (scrollX >= maxX || scrollX <= 0) return;

		Array<Actor> pages = content.getChildren();
		float pageX = 0;
		float pageWidth = 0;
		if (pages.size > 0)
		{
			for (Actor a : pages)
			{
				pageX = a.getX();
				pageWidth = a.getWidth();
				
				Gdx.app.debug(TAG, "scrollX: " + scrollX + " pageX: " + pageX + " pageWidth: " + pageWidth + " < " + (pageX + pageWidth * 0.1));
				//if (scrollX < (pageX + pageWidth * 0.1) || (Constants.VIEWPORT_WIDTH - scrollX) > (pageX + pageWidth * 0.1))
				//if (Math.abs(scrollX - currentScrollX) > (Constants.VIEWPORT_WIDTH * 0.25))
				if (scrollX < (pageX + pageWidth * 0.5))
				{
					break;
				}
			}
			currentScrollX = scrollX;
			setScrollX(MathUtils.clamp(pageX - (width - pageWidth) / 2, 0, maxX));
		}
	}

}
