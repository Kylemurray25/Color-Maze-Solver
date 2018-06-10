//Kyle Murray
//A program that takes in png images and find the lowesr cost path
//Credits to Mateusz Piekut,Lee Stemkoski and Harmit Minhas 



import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.*; 
import javafx.animation.*;
import javafx.geometry.*;
import java.io.*;
import java.util.*;

public class MazeSolver extends Application 
{
    public static void main(String[] args) 
    {
        // Automatic VM reset, thanks to Joseph Rachmuth.
        try
        {
            launch(args);
            System.exit(0);
        }
        catch (Exception error)
        {
            error.printStackTrace();
            System.exit(0);
        }
    }

    Node[][] nodeGrid;
    Node     rootNode;
    LinkedList<Node> queue;

    public void start(Stage mainStage) 
    {
        mainStage.setTitle("Maze Image");

        Group root = new Group();

        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);

        // custom code below --------------------------------------------
        Scanner sc = new Scanner(System.in);
        //prompts for png file name

        System.out.println("Enter the name of the test maze");
        String file = sc.nextLine();
        
        //catches invalid filename input
        try{
            Image originalImage = new Image(file);
            int imageWidth = (int)originalImage.getWidth();
            int imageHeight = (int)originalImage.getHeight();

            nodeGrid = new Node[imageHeight][imageWidth];
            queue = new LinkedList<Node>();

            int imageScale = 8;

            Canvas canvas = new Canvas(imageWidth * imageScale, imageHeight * imageScale);

            GraphicsContext context = canvas.getGraphicsContext2D();

            context.scale(imageScale, imageScale);

            root.getChildren().add(canvas);

            // code to read/write pixels from image itself

            PixelReader reader = originalImage.getPixelReader();

            WritableImage mazeImage = new WritableImage(reader, imageWidth, imageHeight );

            PixelWriter writer = mazeImage.getPixelWriter();

            // the handle method runs in a separate thread @ 60 FPS
            new AnimationTimer()
            {
                public void handle(long currentTime)
                {
                    context.drawImage( mazeImage, 0, 0 );
                }
            }.start();

            // initialized the nodes

            for (int y = 0; y < imageHeight; y++)
            {
                for (int x = 0; x < imageWidth; x++)
                {
                    Color c = reader.getColor(x,y);

                    if (c.equals(Color.BLACK))
                    {
                        nodeGrid[y][x] = null;
                    }

                    else
                    {
                        Node n = new Node(x,y);
                        
                        //assigns cost values to nodes based on color,
                        //every pixel is a node
                        if (c.equals(Color.WHITE))
                        {
                            n.cost=0;   
                        }
                        if (c.equals(Color.CYAN))
                        {
                            n.cost=1;   
                        }
                        if (c.equals(Color.YELLOW))
                        {
                            n.cost=2;   
                        }
                        if (c.equals(Color.MAGENTA))
                        {
                            n.cost=3;   
                        }
                        //start
                        if (c.equals(Color.LIME))
                        {
                            rootNode = n;
                            n.start = true;
                            n.cost=0;
                        }
                        //target
                        if (c.equals(Color.RED)){
                            n.end = true;
                            n.cost=0;
                        }
                        nodeGrid[y][x] = n;
                    }
                }
            }

            // determine node neighbors

            for (int y = 0; y < imageHeight; y++)
            {
                for (int x = 0; x < imageWidth; x++)
                {
                    Node n = nodeGrid[y][x];

                    if ( n == null )
                        continue;

                    if ( nodeGrid[y-1][x] != null ){//up
                      
                        n.addEdges( new Edge( nodeGrid[y-1][x].cost, nodeGrid[y-1][x] ) );
                    }

                    if ( nodeGrid[y][x+1] != null ){//right
                     
                        n.addEdges( new Edge( nodeGrid[y][x+1].cost, nodeGrid[y][x+1]) );
                    }

                    if ( nodeGrid[y+1][x] != null ){//down
                        
                        n.addEdges( new Edge( nodeGrid[y+1][x].cost, nodeGrid[y+1][x] ) );
                    }

                    if ( nodeGrid[y][x-1] != null ){//left
                       
                        n.addEdges( new Edge( nodeGrid[y][x-1].cost, nodeGrid[y][x-1] ) );
                    }

                    //Diagonals to improve effiency
                    //This will give a different cost than if using only 4 directions
                    if ( nodeGrid[y-1][x-1] != null ){//bottomleft
                        
                        n.addEdges( new Edge( nodeGrid[y-1][x-1].cost, nodeGrid[y-1][x-1] ) );
                    }

                    if ( nodeGrid[y-1][x+1] != null ){//bottomright
                       
                        n.addEdges( new Edge( nodeGrid[y-1][x+1].cost, nodeGrid[y-1][x+1]) );
                    }

                    if ( nodeGrid[y+1][x-1] != null ){//topleft
                        
                        n.addEdges( new Edge( nodeGrid[y+1][x-1].cost, nodeGrid[y+1][x-1] ) );
                    }

                    if ( nodeGrid[y+1][x+1] != null ){//topright
                        
                        n.addEdges( new Edge( nodeGrid[y+1][x+1].cost, nodeGrid[y+1][x+1] ) );
                    }

                }
            }

            // implement breadth-first search to find shortest path

            queue.add(rootNode);
            rootNode.visited = true;
            rootNode.totalCost=0;

            new Thread()
            {
                public void run()
                {
                    while ( !queue.isEmpty() )
                    {
                        Node n = queue.remove();
                        writer.setColor( n.x, n.y, Color.GRAY );

                        try { Thread.sleep(1); }
                        catch (Exception e) {}

                        if ( n.end )
                        {
                            System.out.println("Found the end!");
                            PixelReader reader = originalImage.getPixelReader();

                            WritableImage mazeImage = new WritableImage(reader, imageWidth, imageHeight );

                            PixelWriter writer = mazeImage.getPixelWriter();

                            // the handle method runs in a separate thread @ 60 FPS
                            new AnimationTimer()
                            {
                                public void handle(long currentTime)
                                {
                                    context.drawImage( mazeImage, 0, 0 );
                                }
                            }.start();
                            System.out.println("The shortest cost is: " + n.totalCost);

                            while (n.previous != null)
                            {
                                //draws path
                                writer.setColor( n.x, n.y, Color.BLUE );
                                n = n.previous;

                            }

                            break;
                        }
                        else
                        {
                            //uses Djikstra algorithm
                            for (Edge ed : n.neighbors)
                            {
                                Node next = ed.target;
                                int costToNext = n.totalCost + ed.toll;

                                if ( costToNext < next.totalCost )
                                {
                                    next.totalCost = costToNext;
                                    next.previous  = n;
                                }

                                if ( !next.visited )
                                {
                                    queue.add( next );
                                    next.visited = true;
                                }
                            }
                            Collections.sort(queue);
                        }
                    }
                }
            }.start();

            // custom code above --------------------------------------------
            mainStage.show();
        }
        catch (Exception e){
            System.out.println("Wrong maze file...silly");
            System.exit(0);
        }
        
    }
}