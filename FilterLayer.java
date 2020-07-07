/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnn;

import java.util.Random;

/**
 *
 * @author luis_
 */
public class FilterLayer {
    
    
    //50 filters by 6 x 6
 
   
    public double bias;
    
    public double filterArray[][];
    private int filterSize;
    private int colorSize;
    public int row;
    public int height;
    
    
    public FilterLayer(int filterSize, int colorSize, int row, int height)
    {
        this.filterArray = new double[(int) filterSize][(int) filterSize];
        this.filterSize = filterSize;
        this.colorSize = colorSize;
        this.row = row;
        this.height = height;
        
        Random randomWeight = new Random();
        Double weight;
            for(row = 0; row < filterSize; row++)       
            {
            for(height = 0; height < filterSize; height++)       
                {
                    weight = randomWeight.nextDouble();
                    filterArray[row][height] = weight;
                }
            }
        
       
    }
    
    
}
