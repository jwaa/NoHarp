/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.utils;

import java.io.Serializable;

/**
 *
 * @author silvandeleemput
 */
public class TweakVariable <T> implements Serializable {
    
    private String name;
    private T value;
    
    public TweakVariable(String name, T value)
    {
        this.name = name;
        this.value = value;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setValue(Object obj)
    {
        this.value = (T) obj;
    }
    
    public T getValue()
    {
        return value;
    }
    
    @Override
    public String toString()
    {
        String[] s = value.getClass().toString().split("\\."); 
        return name + " " + s[s.length-1] + " " + value.toString();
    }
}
