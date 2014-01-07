/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.utils;

import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author silvandeleemput
 */
public class Tweaker {
    
    private ArrayList<TweakSet> tweaksets;
    
    private TweakSet selectedset;
    private TweakVariable selectedvar;
    
    private float floatdelta;
    private int intdelta;
    
    public Tweaker()
    {
        tweaksets = new ArrayList<TweakSet>();
        floatdelta = 0.1f;
        intdelta = 1;
    }
    
    public void registerTweakable(Tweakable tweak)
    {
        tweaksets.add(tweak.initTweakables());
    }
    
    public ArrayList<TweakSet> getTweakSets()
    {
        return tweaksets;
    }
    
    public TweakSet getSelectedSet()
    {
        return selectedset;
    }
    
    public TweakSet nextSet()
    {
        return getSet(true);
    }
 
    public TweakSet prevSet()
    {
        return getSet(false);
    }    
    
    private TweakSet getSet(boolean inc)
    {
        if (tweaksets.isEmpty())
            return null;
        if (selectedset == null) 
            selectedset = tweaksets.get(0);
        else 
        {
            int index = (tweaksets.indexOf(selectedset) + (inc ? 1 : -1) + tweaksets.size())%tweaksets.size();
            selectedset = tweaksets.get(index);
        }
        selectedvar = (selectedset.isEmpty() ? null : selectedset.get(0));
        return selectedset;
    }
    
    public TweakVariable<?> nextVar()
    {
        return getVar(true);
    }

    public TweakVariable<?> prevVar()
    {
        return getVar(false);
    }
    
    private TweakVariable<?> getVar(boolean inc)
    {
        if (selectedset == null || selectedset.isEmpty())
            return null;
        if (selectedvar == null) 
            selectedvar = selectedset.get(0);
        else 
        {
            int index = (selectedset.indexOf(selectedvar) + (inc ? 1 : -1) + selectedset.size())%selectedset.size();
            selectedvar = selectedset.get(index);
        }
        return selectedvar;
    }   
    
    public Object incDelta()
    {
        return setDelta(true);
    }
    
    public Object decDelta()
    {
        return setDelta(false);
    }
    
    private Object setDelta(boolean inc)
    {
        if (selectedvar != null)
        {
            Object o = selectedvar.getValue();
            if (o instanceof Integer)
            {
                if (inc) 
                    intdelta *= 10; 
                else if (intdelta > 1) 
                    intdelta /=10;
                return intdelta;
            }
            else if (o instanceof Float || o instanceof Vector3f)
            {
                if (inc) 
                    floatdelta *= 10; 
                else 
                    floatdelta /=10;
                return floatdelta;
            }
        }
        return null;
    }
    
    public void saveTweakSets(String pre, String post)
    {
        for (TweakSet set : tweaksets)
            set.save(pre + set.getName() + post);
    }

    public void loadTweakSets(String pre, String post)
    {
        for (TweakSet set : tweaksets)
            set.load(pre + set.getName() + post);
    }

    public TweakVariable setVarValue(int index, boolean inc) {
        if (selectedvar == null)
            return null;
        Object o = selectedvar.getValue();
        if (o instanceof Integer)
            selectedvar.setValue((Integer) o + (inc ? intdelta : -intdelta));
        else if (o instanceof Float)
            selectedvar.setValue((Float) o + (inc ? floatdelta : -floatdelta));
        else if (o instanceof Boolean)
            selectedvar.setValue(!((Boolean) o));    
        else if (o instanceof Vector3f)
        {
            Vector3f vec = ((Vector3f) o).add(
                    (index == 0 ? (inc ? floatdelta : -floatdelta) : 0f),
                    (index == 1 ? (inc ? floatdelta : -floatdelta) : 0f),
                    (index == 2 ? (inc ? floatdelta : -floatdelta) : 0f)
                   );
            selectedvar.setValue(vec);  
        }
        selectedset.updateVariable(selectedvar);
        return selectedvar;
    }
    
}
