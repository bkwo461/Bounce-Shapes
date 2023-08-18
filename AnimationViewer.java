/*
 * ==========================================================================================
 * AnimationViewer.java : Moves shapes around on the screen according to different paths.
 * It is the main drawing area where shapes are added and manipulated.
 * YOUR UPI: bkwo461 - Shinbeom Kwon
 * ==========================================================================================
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ListDataListener;
import java.lang.reflect.Field;

class AnimationViewer extends JComponent implements Runnable, TreeModel{
	private Thread animationThread = null;		// the thread for animation
	private static int DELAY = 120;				 // the current animation speed
	private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<>();
	private ShapeType currentShapeType=Shape.DEFAULT_SHAPETYPE; // the current shape type,
	private PathType currentPathType=Shape.DEFAULT_PATHTYPE;	// the current path type
	private Color currentColor=Shape.DEFAULT_COLOR; // the current fill colour of a shape
	private int currentPanelWidth=Shape.DEFAULT_PANEL_WIDTH, currentPanelHeight = Shape.DEFAULT_PANEL_HEIGHT, currentWidth=Shape.DEFAULT_WIDTH, currentHeight=Shape.DEFAULT_HEIGHT;
	
	private NestedShape root;
	public AnimationViewer() {
		root = new NestedShape(currentPanelWidth, currentPanelHeight);
		start();
		addMouseListener(new MyMouseAdapter());
	}
	

	class MyMouseAdapter extends MouseAdapter {
		public void mouseClicked( MouseEvent e ) {
			boolean found = false;
			for (Shape currentShape: root.getAllInnerShapes())
				if ( currentShape.contains( e.getPoint()) ) { // if the mousepoint is within a shape, then set the shape to be selected/deselected
					currentShape.setSelected( ! currentShape.isSelected() );
					found = true;
				}
			if (!found){
				root.createInnerShape(e.getX(), e.getY(), currentWidth, currentHeight, currentColor, currentPathType, currentShapeType);
				insertNodeInto(root.getInnerShapeAt(root.getSize()-1), root);
			}
		}
	}
	public NestedShape getRoot(){
		return root;
	}
	public void setCurrentColor(Color bc) {
		currentColor = bc;
		for (Shape currentShape: root.getAllInnerShapes())
			if ( currentShape.isSelected())
				currentShape.setColor(currentColor);
	}
	public final void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Shape currentShape: root.getAllInnerShapes()) {
		currentShape.move();
		currentShape.draw(g);
		currentShape.drawHandles(g);
		}
	}
	public void resetMarginSize() {
		currentPanelWidth = getWidth();
		currentPanelHeight = getHeight() ;
		for (Shape currentShape: root.getAllInnerShapes())
			currentShape.setPanelSize(currentPanelWidth,currentPanelHeight );
	}
	public boolean isLeaf(Object node){
		return !(node instanceof NestedShape) && (node instanceof Shape);
	}
	public boolean isRoot(Shape selectedNode){
		return (selectedNode.equals(root));
	}
	public Shape getChild(Object parent, int index){
		if(parent instanceof NestedShape && index >= 0 && index <= root.getSize()){
			NestedShape n = (NestedShape)parent;
			if(n.getInnerShapeAt(index) == null){
				return null;
			}else{return n.getInnerShapeAt(index);}
		}else{
			return null;
		}
	}
	public int  getChildCount(Object parent){
		if(parent instanceof NestedShape){
			NestedShape n = (NestedShape)parent;
			return n.getSize();
		}else{
			return 0;
		}
	}
	public int getIndexOfChild(Object parent, Object child){
		if(parent instanceof NestedShape && child instanceof Shape){
			NestedShape n = (NestedShape)parent;
			Shape c = (Shape)child;
			return n.indexOf(c);
		}else{
			return -1;
		}
	}
	public void addTreeModelListener(final TreeModelListener tml){
		treeModelListeners.add(tml);
	}
	public void removeTreeModelListener(final TreeModelListener tml){
		treeModelListeners.remove(tml);
	}
	public void fireTreeNodesInserted(Object source, Object[] path,int[] childIndices,Object[] children){
		TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
		for(TreeModelListener tml : treeModelListeners){
			tml.treeNodesInserted(event);
		}
	}
	public void insertNodeInto(Shape newChild, NestedShape parent){
		int[] intArray = {parent.indexOf(newChild)};
		Object[] children = {newChild};
		fireTreeNodesInserted(this,parent.getPath() , intArray, children);
	}
	public void addShapeNode(NestedShape selectedNode){
		if(selectedNode.equals(root)){
			selectedNode.createInnerShape(0, 0, currentWidth, currentHeight, currentColor, currentPathType, currentShapeType);
		}
		else{
			selectedNode.createInnerShape(0, 0, currentWidth/2, currentHeight/2, currentColor, currentPathType, currentShapeType);
		}
		insertNodeInto(selectedNode.getInnerShapeAt(selectedNode.getSize()-1), selectedNode);
	}
	public void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices,Object[] children){
		TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
		for(TreeModelListener tml : treeModelListeners){
			tml.treeNodesRemoved(event);
		}
	}
	public void removeNodeFromParent(Shape selectedNode){
		NestedShape p = selectedNode.getParent();
		int index = p.indexOf(selectedNode);
		p.removeInnerShape(selectedNode);
		int[] intArray = {index};
		Object[] children = {selectedNode};
		fireTreeNodesRemoved(p, p.getPath(), intArray, children);
	}
	
	public void valueForPathChanged(TreePath path, Object newValue){}
	// you don't need to make any changes after this line ______________
	public void setCurrentShapeType(ShapeType value) { currentShapeType = value; }
	public void setCurrentPathType(PathType value) { currentPathType = value; }
	public ShapeType getCurrentShapeType() { return currentShapeType; }
	public PathType getCurrentPathType() { return currentPathType; }
	public int getCurrentWidth() { return currentWidth; }
	public int getCurrentHeight() { return currentHeight; }
	public Color getCurrentColor() { return currentColor; }
	public void update(Graphics g){ paint(g); }
	public void start() {
		animationThread = new Thread(this);
		animationThread.start();
	}
	public void stop() {
		if (animationThread != null) {
			animationThread = null;
		}
	}
	public void run() {
		Thread myThread = Thread.currentThread();
		while(animationThread==myThread) {
			repaint();
			pause(DELAY);
		}
	}
	private void pause(int milliseconds) {
		try {
			Thread.sleep((long)milliseconds);
		} catch(InterruptedException ie) {}
	}
}
