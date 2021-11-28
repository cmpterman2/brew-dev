package com.brew.brewpot;

public class BrewPotConfig {

    //TARGET? or LIMIT?
    private float target;
    private Mode mode;

    public BrewPotConfig(float target, Mode mode) {
        this.target = target;
        this.mode = mode;
    }

    public enum Mode {
        OFF, HIGH, MED, LOW
    }
    
    public BrewPotConfig() {
        
    }

    
    /**
     * @return the target
     */
    public float getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(float target) {
        this.target = target;
    }

    /**
     * @return the mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }


    public boolean compare(BrewPotConfig config) {
        boolean same = true;
        same = same && mode == config.getMode();
        same = same && target == config.getTarget();

        return same;
    }

    public BrewPotConfig duplicate() {
        BrewPotConfig newConfig = new BrewPotConfig();
        newConfig.setMode(mode);
        newConfig.setTarget(target);

        return newConfig;
    }

    public String toString() {
        return "Mode: "+mode+"  Target: "+target;
    }
    
}
