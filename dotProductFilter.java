/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnn;

/**
 *
 * @author luis_
 */
public class dotProductFilter implements Runnable {
    
    
    int[][][][] input;
    FilterLayer[][] filter;
    FilterOutput[][][] output;
    int filterSize;
    int filterOutputSize;
    double bias[];
    int stride;
    int outputCounter;
    int imageCounter;
    int rgb;
    int dSize;
    int padding;
    
    public dotProductFilter(int[][][][] input, FilterLayer[][] filter, int filterSize, int filterOutputSize, 
            double[] bias, int stride, 
            int dSize, int imageCounter, FilterOutput[][][] output)
    {
        
        
        // set constructor
        // set the output array
        this.input = input;
        this.filter = filter;
        this.padding = stride;
        this.filterSize = filterSize;
        this.filterOutputSize = filterOutputSize;
        this.bias = bias;
        this.stride = stride;
        this.dSize = dSize;
        //this.outputTemp = output;
        //this.outputCounter = outputCounter;
        this.imageCounter = imageCounter;
        //this.rgb = rgb;
        this.output = output;
        
    }
    
    
        public void run() 
        {
            
              double sum = 0;
   // double outputTemp[][] = new double[filterOutputSize][filterOutputSize];
    
   
    int filterCounterHeight = 0;
     int filterCounterRow = 0;
     int strideAdderX = 0;
     int strideAdderY = 0;
            
    for (int height = 0; height < filterOutputSize; height++) //output
    {
      for (int row = 0; row < filterOutputSize; row++) //output
      {
          
             filterCounterHeight = 0;
             for(int filterHeight = 0; filterHeight < filterSize; filterHeight++)
             {
                  filterCounterRow = 0;
                  for(int filterRow = 0 ; filterRow < filterSize; filterRow++)
                 {
                     
                       for( int rgb = 0; rgb < 3; rgb++)
                       {
                          // if(Thread.currentThread().getName() == "Thread-4")
                           
                           /*System.out.format("%s: \n "
                                   + "\n The imagecounter is %d.\n The filter row is %d. "
                                   + "\n The filter height is %d. \n The strideAdderX is %d. "
                                   + "\n The strideAdderY is %d. \n The RGB value is %d \n \n\n ", 
                                   Thread.currentThread().getName(), imageCounter, filterRow + strideAdderX, filterHeight + strideAdderY, strideAdderX, 
                                   strideAdderY, rgb);
                            */        
                            sum += input[imageCounter][filterRow + strideAdderX][filterHeight + strideAdderY][rgb] *
                                    filter[dSize][rgb].filterArray[filterCounterRow][filterCounterHeight];
                       }
                       filterCounterRow++;
                  } 
                   
                     filterCounterHeight++;
                     
                     
             }
             strideAdderX+= padding;
              
             
             //strideAdderY = 0;
             output[row][height][dSize].output = sum + bias[dSize];
             sum = 0;
             //padding+=stride;
             
         }
         strideAdderY+= padding;
         strideAdderX = 0;
    }
         System.out.println("Thread Number: "+ Thread.currentThread().getName() + " has finished."); 
        }
        
        
        
   
} 
  

