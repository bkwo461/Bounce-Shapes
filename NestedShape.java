/*
 *	===============================================================================
 *	NestedShape.java : A shape that is a Nested. It can create the Inner shape.
 *  YOUR UPI: bkwo461 - Shinbeom Kwon
 *	=============================================================================== */
import java.util.*;
import java.awt.*;

public class NestedShape extends RectangleShape {
    private ArrayList<Shape> innerShapes = new ArrayList<>();
    
    NestedShape(){
        super();
        createInnerShape(0, 0, DEFAULT_WIDTH/2, DEFAULT_HEIGHT/2, this.color, PathType.BOUNCE, ShapeType.RECTANGLE);
    }
    NestedShape(int x, int y, int w, int h, int pw, int ph, Color c, PathType p){
        super(x, y, w, h, pw, ph, c, p);
        createInnerShape(0, 0, w/2, h/2, c, PathType.BOUNCE, ShapeType.RECTANGLE);
    }
    NestedShape(int w, int h){
        this.x = 0;
        this.y = 0;
        this.width = w;
        this.height = h;
        this.panelWidth = DEFAULT_PANEL_WIDTH;
        this.panelHeight = DEFAULT_PANEL_HEIGHT;
        this.color = Color.BLACK;
        this.path = new BouncingPath(1, 2);
    }
    public Shape createInnerShape(int x, int y, int w, int h, Color c, PathType pt, ShapeType st){
        Shape inner;
        if(st.equals(ShapeType.RECTANGLE)){
            inner = new RectangleShape(x, y, w, h, this.width, this.height, c,pt);
            
        }else if(st.equals(ShapeType.OVAL)){
            inner = new OvalShape(x, y, w, h, this.width, this.height, c,pt);
            
        }else{
            inner = new NestedShape(x, y, w, h, this.width, this.height, c,pt);  
            
        }
        inner.setParent(this);
        innerShapes.add(inner);
        return inner;
        
    }
    public Shape getInnerShapeAt(int index){
        return innerShapes.get(index);
    }
    public int getSize(){
        return innerShapes.size();
    }
    public int indexOf(Shape s){
        return innerShapes.indexOf(s);
    }
    public void addInnerShape(Shape s){
        s.setParent(this);
        innerShapes.add(s);
    }
    public void removeInnerShape(Shape s){
        s.setParent(null);
        innerShapes.remove(s);
    }
    public void removeInnerShapeAt(int index){
        innerShapes.get(index).setParent(null);
        innerShapes.remove(index);
    }
    public ArrayList<Shape> getAllInnerShapes(){
        return innerShapes;
    }
    @Override
    public void setColor(Color c){
        this.color = c;
        for(Shape s : innerShapes){
            s.color = c;
        }
    }
    public void draw(Graphics g){
        g.setColor(Color.BLACK);
        g.drawRect(this.x, this.y, this.width, this.height);
        g.translate(x,y);
        for(Shape s : innerShapes){
            s.draw(g);
           
        }
        g.translate(-x,-y);
    }
    @Override
    public void move(){
        this.path.move();
        for(Shape s : innerShapes){
            s.move();
        }
    }
}
