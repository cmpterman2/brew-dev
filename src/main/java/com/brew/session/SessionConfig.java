package com.brew.session;

public class SessionConfig {
    private Phase phase;
    private Mode mode;
    private long phaseTime;

    public enum Mode {
        ACTIVE, PAUSED
    }


    public enum Phase {
        RECIPE_NEW, RECIPE_PLAN, BREW_PLAN, BREW_PREMASH, BREW_MASH, BREW_PREBOIL, BREW_BOIL, BREW_COOL, BREW_DONE, FERMENTATION_PLAN, FERMENTATION_PITCH, FERMENTATION_FERMENT, FERMENTATION_DONE
    }


    public Mode getMode() {
        return mode;
    }



    public long getPhaseTime() {
        return phaseTime;
    }



    public void setPhaseTime(long phaseTime) {
        this.phaseTime = phaseTime;
    }



    public Phase getPhase() {
        return phase;
    }



    public void setPhase(Phase phase) {
        this.phase = phase;
    }



    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String toString() {
        return "Phase: "+phase+"  Mode: "+mode;
    }

    
    public boolean compare(SessionConfig config) {
        boolean same = true;
        same = same && mode == config.getMode();
        same = same && phase == config.getPhase();

        return same;
    }

    public SessionConfig duplicate() {
        SessionConfig newConfig = new SessionConfig();
        newConfig.setMode(mode);
        newConfig.setPhase(phase);
        newConfig.setPhaseTime(phaseTime);

        return newConfig;
    }
}
