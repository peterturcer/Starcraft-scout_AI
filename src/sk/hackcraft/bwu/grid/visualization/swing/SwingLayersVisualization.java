package sk.hackcraft.bwu.grid.visualization.swing;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.Timer;

import sk.hackcraft.bwu.grid.Grid;
import sk.hackcraft.bwu.grid.visualization.LayerDrawable;

public class SwingLayersVisualization
{
	private final JFrame window;
	
	private final List<Grid> layers;
	private final List<Grid> activeLayers;
	
	private final Map<Grid, LayerDrawable> layersDrawables;
	private final Map<Grid, String> layersNames;
	
	private final LayersCanvas canvas;
	private final JList<String> layersList;
	private final JList<String> activeLayersList;
	
	public SwingLayersVisualization(LayersPainter layersPainter)
	{
		window = new JFrame("Map layers");
		
		window.setSize(300, 300);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		layers = new ArrayList<>();
		activeLayers = new ArrayList<>();
		
		layersDrawables = new HashMap<>();
		layersNames = new HashMap<>();
		
		Container contentPane = window.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		
		canvas = new LayersCanvas(layersPainter);
		layersList = new JList<>();
		activeLayersList = new JList<>();
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		
		contentPane.add(canvas, c);
		//contentPane.add(comp)
		
		new Timer(20, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				canvas.repaint();
			}
		}).start();
	}
	
	public void addLayer(String name, Grid layer, LayerDrawable drawable)
	{
		layers.add(layer);
		
		layersDrawables.put(layer, drawable);
		layersNames.put(layer, name);
	}
	
	public void start()
	{
		window.setVisible(true);
	}
	
	public void close()
	{
		window.dispose();
	}
	
	public boolean isVisible()
	{
		return window.isVisible();
	}
}
