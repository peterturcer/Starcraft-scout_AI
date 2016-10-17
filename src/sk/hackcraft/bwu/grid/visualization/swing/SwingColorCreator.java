package sk.hackcraft.bwu.grid.visualization.swing;

import java.awt.Color;

import sk.hackcraft.bwu.grid.visualization.ColorAssigner.ColorFactory;

public class SwingColorCreator implements ColorFactory<Color>
{
	@Override
	public Color create(int r, int g, int b, int a)
	{
		return new Color(r, g, b, a);
	}
}
