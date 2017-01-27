package com.ob.common.collection;

/**
 * Created by boris on 19.04.2016.
 */
public class QueueUtil {

    // How many times should a value be shifted left for a given scale of pointer.
    public static int calculateShiftForScale(int scale){
        switch(scale){
            case 4:
                return 2;
            case 8:
                return 3;
            default:
                throw new IllegalStateException("Unknown pointer size");
        }
    }

    //ex: 4 => 4, 5 => 8, 10 => 16
    public static int findNextPositivePowerOfTwo(int value){
        return 1 << (32 - Integer.numberOfLeadingZeros(value - 1));
    }

    public static int calculateOffset(int index, int arrayBase, int arrayScale){
        return arrayBase + (index << arrayScale);
    }

//    public static void main(String [] args){
//        System.out.println(Math.max(findNextPositivePowerOfTwo(66), 64));
//    }

}
