import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.lang.Object;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
/*
<applet code="Main" width=1000 height=600>
</applet>
*/
class Pair
{
	public int first;
	public int second;
	Pair(int a, int b)
	{
		first = a;
		second = b;
	}
	Pair(String input)
	{
		StringTokenizer st = new StringTokenizer(input, ",");
		first = Integer.parseInt(st.nextToken());
		second = Integer.parseInt(st.nextToken());
	}
	public String toString()
	{
		return new Integer(first).toString()+","+new Integer(second).toString();
	}
}

public class Main extends Applet implements MouseMotionListener,MouseListener,  Runnable
{

	public int padx;
	public int pady;
	public int padLength = 100;
	public int padLeft, padRight;
	public int padTop = 500;
	public int padHeight = 10;
	public int padBottom = padTop + padHeight;
	public int blockLength = 40;
	public int blockHeight = 20;
	public int blockStartX = 100;
	public int blockStartY = 100;
	public int MaxY = 600;
	public int MaxX = 1000;
	public boolean repaintBlocks = false;
	public boolean initialPaintComplete = false;
	public String msg = "Default";
	public Image blockBuffer;
	public Image ballBuffer;
	public Image padBuffer;
	public Image ballBufferPrevious;
	public int blockSetWidth = 800;
	public int blockSetHeight = 200;
	public HashMap <String, Color> blockMap;
	public int ballX;
	public int ballY;
	public int ballPreviousX;
	public int ballPreviousY;
	public boolean ballStarted = false;
	public int ballDiameter = 16;
    public Random generator;
    double angle;
    int unit = 8;
    double pi = 22.0/7;
    boolean leftEdgeActive, rightEdgeActive, topEdgeActive, bottomEdgeActive;
    boolean top, right;
    boolean gameComplete = false;
	public void init() {
		blockMap = new HashMap <String, Color>();
		leftEdgeActive= rightEdgeActive= topEdgeActive= bottomEdgeActive = false;
		this.resize(MaxX,MaxY);
		this.setMaximumSize(new Dimension(MaxX,MaxY));
		addMouseMotionListener(this);	
		addMouseListener(this);
		generator = new Random();
		int x, y;
		int count = 0;
		//angle = generator.nextDouble() * 180; // a random start angle
        angle = 115.0;
		padBuffer=createImage(MaxX,padHeight);
        blockBuffer=createImage(blockSetWidth,blockSetHeight);
        ballBuffer = createImage(ballDiameter, ballDiameter);
        ballBufferPrevious = createImage(ballDiameter, ballDiameter);

        System.out.println("Pairs");
		for(x=0; x< blockSetWidth; x+=blockLength)
		{
			for(y = 0; y<blockSetHeight; y+=blockHeight)
			{
				count ++;
				if(count%7 == 0)
					blockMap.put(new Pair(x,y).toString(), Color.BLUE );
				if(count%7 == 1)
					blockMap.put(new Pair(x,y).toString(), Color.CYAN );
				if(count%7 == 2)
					blockMap.put(new Pair(x,y).toString(), Color.RED );
				if(count%7 == 3)
					blockMap.put(new Pair(x,y).toString(), Color.GREEN );
				if(count%7 == 4)
					blockMap.put(new Pair(x,y).toString(), Color.ORANGE );
				if(count%7 == 5)
					blockMap.put(new Pair(x,y).toString(), Color.MAGENTA );
				if(count%7 == 6)
					blockMap.put(new Pair(x,y).toString(), Color.PINK );
				
				//System.out.println(x+ " "  + y + "#");
			}
			
		}
      //  System.out.println("Route");

		repaintBlocks = true;
		repaint();
	    Thread t = new Thread(this, "Ball thread");
	    t.start();

	}
	@Override
	public void mouseDragged(MouseEvent me) {
		// TODO Auto-generated method stub
		padx = me.getX();
		pady = me.getY();
		repaint();
	}

    public void mouseClicked(MouseEvent e) {
    	ballStarted = true;
		repaint();

    }
    
    public void mousePressed(MouseEvent e) {
    	ballStarted = true;
		repaint();

    }
	@Override
	public void mouseMoved(MouseEvent me) {
		// TODO Auto-generated method stub
		padx = me.getX();
		pady = padTop;
		repaint();
	}
	
	public void update(Graphics g){
        paint(g); //keep what was there before
	}
	
	public void paint(Graphics g) {
		if(!gameComplete)
		{
			if(!initialPaintComplete)
			{
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, MaxX, MaxY);
				initialPaintComplete = true;
			}
	        Graphics blockGraphics=blockBuffer.getGraphics();
	        Graphics padGraphics=padBuffer.getGraphics();
	        Graphics ballGraphics=ballBuffer.getGraphics();
	        Graphics ballPreviousGraphics = ballBufferPrevious.getGraphics();
	
	        padGraphics.setColor(Color.WHITE);
	        padGraphics.fillRect(0, 0, MaxX, blockHeight); // whole line white
	        padGraphics.setColor(Color.BLACK);
	        padLeft  = padx - padLength/2;
	
			if(padLeft >= 0 && padx + padLength/2 < MaxX){
				padGraphics.fillRoundRect(padLeft, 0, padLength, padHeight, padHeight, padHeight);
			}
			if(padLeft < 0){
				padGraphics.fillRoundRect(0, 0, padLength, padHeight, padHeight, padHeight);
			}
			else if(padx + padLength/2 >= MaxX){
				padGraphics.fillRoundRect(MaxX - padLength, 0, padLength, padHeight, padHeight, padHeight);
			}
		    g.drawImage(padBuffer,0,padTop, null);
		   // g.drawString(msg, 50, 50);
			// Drawing Blocks
			
			    Iterator<Entry<String, Color>> it = blockMap.entrySet().iterator();
			    blockGraphics.setColor(Color.WHITE);
		        blockGraphics.fillRect(0, 0, blockSetWidth,blockSetHeight); // whole line white
			    while (it.hasNext()) {
			        Map.Entry<String, Color> pairs = (Map.Entry<String, Color>)it.next();
			        String coordinate = pairs.getKey();
			        blockGraphics.setColor(pairs.getValue());
			        blockGraphics.fillRect(new Pair(coordinate).first, new Pair(coordinate).second, blockLength, blockHeight);
			    }
			    if(repaintBlocks){
			    	g.drawImage(blockBuffer,blockStartX,blockStartY, null);
			    	repaintBlocks = false;
			    }
			
			    ballPreviousGraphics.setColor(Color.WHITE);
			    ballPreviousGraphics.fillRect(0, 0, ballDiameter,ballDiameter);
		    	g.drawImage(ballBufferPrevious,ballPreviousX,ballPreviousY, null);
	
			    ballGraphics.setColor(Color.BLUE);
		        ballGraphics.fillOval(0, 0, ballDiameter,ballDiameter); // whole line white
		    	g.drawImage(ballBuffer,ballX,ballY, null);
	
			    ballGraphics.setColor(Color.RED);
		        ballGraphics.fillOval(4, 4, ballDiameter-8,ballDiameter-8); // whole line white
		    	g.drawImage(ballBuffer,ballX,ballY, null);
		}
		else
		{
			// Black Paint
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, MaxX, MaxY);
			g.setColor(Color.RED);

			ballX = 100;
			ballY = 100;
			int i;
			for(i=0;i<50;i++)
			{
				g.fillOval(ballX + i, (ballY - i/4), ballDiameter, ballDiameter);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			g.setColor(Color.MAGENTA);

			for(i=50;i>=25;i--)
			{
				g.fillOval(ballX + i, (ballY - i/4), ballDiameter, ballDiameter);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			g.setColor(Color.BLUE);

			for(i=0;i<100;i++)
			{
				g.fillOval(ballX +25, (ballY+i), ballDiameter, ballDiameter);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			g.setColor(Color.GREEN);

			for(i=0;i<25;i++)
			{
				g.fillOval(ballX +25 - i, (ballY + 102 + i/4), ballDiameter, ballDiameter);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			g.setColor(Color.pink);

			for(i=0;i<50;i++)
			{
				g.fillOval(ballX + i, (ballY +102 - i/4), ballDiameter, ballDiameter);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			ballX = 200;
			ballY = 100;
			
			
	        
	        
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(gameComplete !=true)
	       {
			 simulateBall();
	         repaint();   
	         try {
	                Thread.sleep(30);
	            } catch (InterruptedException ex) {
	                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
	            }
	       }
		
	}

	public void simulateBall()
	{
		ballPreviousX = ballX;
		ballPreviousY = ballY;
		leftEdgeActive= rightEdgeActive= topEdgeActive= bottomEdgeActive = true;

		if(ballY>=MaxY){
			ballStarted = false;
		}
		
		if(!ballStarted){
			ballX = padx - ballDiameter/2;
			ballY = padTop - ballDiameter;
		}
		else{
				ballX = ballX + (int) (Math.cos((angle * pi)/180.0) * unit);
				ballY = ballY - (int) (Math.sin((angle * pi)/180.0) * unit); // Y increases downward
				if(Math.sin((angle * pi)/180.0) * unit >= 0){
					top = true;
				}
				else
					top = false;
				
				if(Math.cos((angle * pi)/180.0) * unit >= 0){
					right = true;
				}
				else right = false;
				
				if(right) leftEdgeActive = false;
				else rightEdgeActive = false;
				
				if(top) bottomEdgeActive = false;
				else topEdgeActive = false;
				Pair temp;
				if(leftEdgeActive){
					int xBlockLeft = ((ballX- blockStartX) / blockLength)*blockLength ;
					int yBlockLeft = ((ballY - blockStartY + ballDiameter/2) / blockHeight)*blockHeight ;
					
					//Left Boundary Edge
					 temp = new Pair(xBlockLeft, yBlockLeft);
					if((blockMap.containsKey(temp.toString()) && (ballX >= blockStartX ) && (ballY >= blockStartY)  ) || ballX<=0){
						//System.out.println("Left edge");
						angle = 180.0 - angle;
						blockMap.remove(temp.toString());
						repaintBlocks = true;
						return;
					}
				}
				
				if(rightEdgeActive){
					int xBlockRight = ((ballX  - blockStartX + ballDiameter) / blockLength)*blockLength;
					int yBlockRight = ((ballY - blockStartY + ballDiameter/2) / blockHeight)*blockHeight ;
					//Right Boundary Edge
					temp = new Pair(xBlockRight, yBlockRight);
					if((blockMap.containsKey(temp.toString()) && (ballX  - blockStartX + ballDiameter > 0 ) && (ballY - blockStartY + ballDiameter/2 )>=0  ) || (ballX +ballDiameter)  >=MaxX){
						//System.out.println("Right edge");
						angle = 180.0 - angle;
						blockMap.remove(temp.toString());
						repaintBlocks = true;
						return;
					}
				}
				if(topEdgeActive){
					int xBlockTop = ((ballX - blockStartX + ballDiameter/2) / blockLength)*blockLength ;
					int yBlockTop = ((ballY- blockStartY) / blockHeight)*blockHeight ;
					//top Boundary Edge
					temp = new Pair(xBlockTop, yBlockTop);
					System.out.println(temp.toString());
					
					if((blockMap.containsKey(temp.toString()) && (ballX >= blockStartX ) && (ballY >= blockStartY)  ) || (ballY -ballDiameter)  <=0){
						System.out.println("Top edge");
						angle = 360.0 - angle;
						blockMap.remove(temp.toString());
						repaintBlocks = true;
						return;
					}
				}
				if(bottomEdgeActive){
					int xBlockBottom = ((ballX - blockStartX + ballDiameter/2) / blockLength)*blockLength ;
					int yBlockBottom = ((ballY - blockStartY + ballDiameter) / blockHeight)*blockHeight ;
					//top Boundary Edge
					temp = new Pair(xBlockBottom, yBlockBottom);
					if((blockMap.containsKey(temp.toString()) && (ballX - blockStartX + ballDiameter/2 >=0 ) && (ballY - blockStartY + ballDiameter >=0) )  || 
							((( ( (ballY + ballDiameter) >=padTop && (ballY+ballDiameter) <=padBottom) )|| (ballY >=padTop && ballY <=padBottom) ) 
							&& (ballX)>=(padx - padLength/2) && ballX <=(padx+padLength/2)  ) ){
						
						//System.out.println("Top edge");
						angle = 360.0 - angle;
						blockMap.remove(temp.toString());
						repaintBlocks = true;
						return;
					}
				}
		}
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	}

