/*
 * Sprite Class
 * 		- Provided to us, but I modified it such that the bounds of the sprites are circle.
 */
package spaceIO;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

public class Sprite {
	protected Image img;
	protected int x, y, dx, dy;
	protected boolean visible;
	protected double width;
	protected double height;
	
	public Sprite(int xPos, int yPos){
		this.x = xPos;
		this.y = yPos;
		this.visible = true;
	}
	
	//method to set the object's image
	protected void loadImage(Image img){
		try{
			this.img = img;
	        this.setSize();
		} catch(Exception e){}
	}
	
	//method to set the image to the image view node
    protected void render(GraphicsContext gc){
		gc.drawImage(this.img, this.x - width/2, this.y - width/2);
    }
	
	//method to set the object's width and height properties
	private void setSize(){
		this.width = this.img.getWidth();
	    this.height = this.img.getHeight();
	}
	//method that will check for collision of two sprites
	public boolean collidesWith(Sprite circ2){
		Circle circle1 = this.getBounds();
		Circle circle2 = circ2.getBounds();

		return circle1.intersects(circ2.getBounds().getLayoutBounds());
	}
	//method that will return the bounds of an image
	public Circle getBounds(){
		return new Circle(this.x , this.y, (this.width / 2) * 0.9);
	}

	//method to return the image
	Image getImage(){
		return this.img;
	}

	//getters
	public int getX() {
    	return this.x;
	}

	public int getY() {
    	return this.y;
	}
	
	public boolean getVisible(){
		return visible;	
	}

	public boolean isVisible(){
		if(visible) return true;
		return false;
	}
	
	//setters
	public void setDX(int dx){
		this.dx = dx;
	}
	
	public void setDY(int dy){
		this.dy = dy;
	}
	
	public void setWidth(double val){
		this.width = val;
	}

	public void setHeight(double val){
		this.height = val;
	}
		
	public void setVisible(boolean value){
		this.visible = value;
	}

}
