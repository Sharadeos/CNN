package cnn;

import java.awt.Color;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    

    private int numberofImages = 1000;
    private int imageSize = 3073;
    private BufferedImage[] imageList = new BufferedImage[numberofImages];
    public byte[] b = new byte[imageSize*numberofImages];
    private byte[] label = new byte[numberofImages];
    
    private int[][][][] input = new int[10000][32][32][3];
            //datastore 
    //Calculations for Conv Layer
    private int imageWidth = 32;
    private int imageHeight = 32;
    private int imageDepth = 3;
    private int convDepthFilter = 50;
    private int filterSize = 6;
    private int padding = 0;
    private int stride = 2;
     //  (W−F+2P)/S+1
    private int filterOutputSize =  (int) ((imageWidth - filterSize + (2*padding))/stride) + 1;
    // must be double to check if it is agreeable
    
    private int numberofThreads = numberofImages * convDepthFilter;
    
    Thread threads[] = new Thread[numberofThreads];
    private double bias[] = new double[convDepthFilter];  
    // 50 depth, 6 x 6
    private FilterLayer filterLayer[][] = new FilterLayer [convDepthFilter][imageDepth];
    private FilterOutput filterOutput[][][] = new FilterOutput[filterOutputSize][filterOutputSize][convDepthFilter];
  
    public int threadCounter = 0; 
    public int xValue = 0;
    public int yValue = 0; 
    

    public static void main(String[] args)
    {
    launch(args);

    }
    
   @Override
    public void start(Stage primaryStage) {
        
    try
    {
    fileReader();
    
    convolutionLayer();
    }
    catch(IOException e)
    {
            System.err.println("Caught IOException: " + e.getMessage());
    }
     primaryStage.setWidth(1600);
        primaryStage.setHeight(900);
             Scene scene = new Scene(new Group());
      VBox root = new VBox();   
      
        primaryStage.setTitle("Drawing Operations Test");
        //Group root = new Group();
        Canvas canvas = new Canvas(1568, 10000);
        GraphicsContext gc = canvas.getGraphicsContext2D();
     
        for(int k = 0; k < numberofImages; k++)
            {
            if(xValue == 49)
            {
               yValue += 32;
               xValue = 0;
            } 
            Image image = SwingFXUtils.toFXImage(imageList[k], null);
            gc.drawImage(image, xValue * 32, yValue);
            xValue++;
            }
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(canvas);
        
        
        root.getChildren().addAll(scrollPane);
        scene.setRoot(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void convolutionLayer()
    {
                
    //Start of convolution


        filterSetter(filterLayer, filterSize, convDepthFilter, imageDepth);
        biasSetter();
        neuronSetter();
        
        convOperation();
     
        System.out.println("Number of Threads Created: " + threadCounter);
          
          
    }
    
    public void biasSetter()
    {
         Random randomWeight= new Random();
         for(int depthSize = 0; depthSize < convDepthFilter; depthSize++ )
       {
            
           bias[depthSize] =  randomWeight.nextInt();
       }
    }
    
    public void filterSetter(FilterLayer input[][], int filterSize, int convDepthFilter, int imageDepth)
    {

       for(int depthSize = 0; depthSize < convDepthFilter; depthSize++ )
       {
         //set weights here...

          for(int color = 0; color < imageDepth; color++)       
            {            
                     input[depthSize][color]  = new FilterLayer(filterSize, imageDepth, filterSize, filterSize);
            } 
        }
    }
    
    public void neuronSetter()
    {
        for(int row = 0; row < filterOutputSize; row++)       
        {
           for(int height = 0; height < filterOutputSize; height++)       
            {
                for(int depthSize = 0; depthSize < convDepthFilter; depthSize++ )
                {
                    filterOutput[row][height][depthSize] = new FilterOutput(0.0);
                }
            } 
        }
    }
    
    public void convOperation()
    {
        //50 threads per image
        threadCounter= 0;
        for(int imageCounter = 0; imageCounter < numberofImages; imageCounter++)
        {
        
            for(int dSize = 0; dSize < convDepthFilter; dSize++)
            {
             
                            threads[threadCounter] = new Thread(new dotProductFilter(
                            input, filterLayer, filterSize, filterOutputSize,  
                            bias, stride, dSize, imageCounter, filterOutput
                            ));
                            threads[threadCounter].start();
                        threadCounter++;
            
                  
            }
        }
         threadCounter= 0;
           for(int imageCounter = 0; imageCounter < numberofImages; imageCounter++)
        {
        
            for(int dSize = 0; dSize < convDepthFilter; dSize++)
            {
                    try
                    {
                    threads[threadCounter].join();
                    threadCounter++;
                    }
                      catch(Exception ex)
                    {
                        System.out.println("Exception has " +
                                            "been caught" + ex);
                    }
            }
        }
      
        
    }
    
    public void fileReader() throws IOException
    {
        
        //Filder Reader
        File testfile = new File("src/cnn/data_batch_1.bin");

        FileInputStream inputStream;
        
          try 
          {
            inputStream = new FileInputStream(testfile);
            inputStream.read(b);
              
        for(int imageIndex = 0; imageIndex < numberofImages; imageIndex++)
        {
              this.imageList[imageIndex] = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                Color color = new Color(
                        b[(1 + imageIndex * imageSize) + 1024 * 0 + row * 32 + col] & 0xFF,
                        b[(1 + imageIndex * imageSize) + 1024 * 1 + row * 32 + col] & 0xFF,
                        b[(1 + imageIndex * imageSize) + 1024 * 2 + row * 32 + col] & 0xFF);
                imageList[imageIndex].setRGB(col, row, color.getRGB());
                
                //converts to proper vector array
                input[imageIndex][row][col][0] = b[(1 + imageIndex * imageSize) + 1024 * 0 + row * 32 + col];
                input[imageIndex][row][col][1] = b[(1 + imageIndex * imageSize) + 1024 * 1 + row * 32 + col];
                input[imageIndex][row][col][2] = b[(1 + imageIndex * imageSize) + 1024 * 2 + row * 32 + col];
            }
              
        }
            label[imageIndex] = b[imageIndex * imageSize];
        }
              
  

        //boolean result = ImageIO.write(image, "jpeg", new FileOutputStream("./out.jpg"));
         

         inputStream.close();
        
           for(int k = 0; k < numberofImages; k++)
    {
        if(k % 49 == 0)
        {
                    System.out.println();
        }
        System.out.print(label[k] + " ");
    }
        System.out.println();    
          System.out.println("Size of Output Volume: " + filterOutputSize + " x " + filterOutputSize);
        
         } 
          catch (FileNotFoundException e) 
          {
      e.printStackTrace(System.err);
    }
    }
 

    
}

