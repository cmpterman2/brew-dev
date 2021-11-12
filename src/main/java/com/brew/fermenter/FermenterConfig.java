package com.brew.fermenter;

import java.util.ArrayList;

public class FermenterConfig {
    private Mode mode;
    private ArrayList<Entry> schedule = new ArrayList<Entry>();

    public enum Mode {
        OFF, SCHEDULE, SCHEDULE_PAUSE, TARGET_PITCH
    }

    public FermenterConfig() {
        mode = Mode.OFF;

        //TODO - this is setting a default schedule... bad?
        schedule.add(new Entry(0.0f, 70.0f));
    }


   
    public float calculatePitchTarget() {
        //First one is always the pitch target
        return schedule.get(0).getTarget();
        
    }



    public Mode getMode() {
        return mode;
    }



    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public boolean validateAndSetSchedule(ArrayList<Entry> schedule) {

        if( schedule == null || schedule.size() == 0 ){
            return false;
        }

        for(int x=0;x<schedule.size(); x++ ){

            Entry entry = schedule.get(x);

            for( int y=x+1;y<schedule.size();y++) {
                if( schedule.get(y).getDay() < entry.getDay()) {
                    //Swap
                    schedule.set(x, schedule.get(y));
                    schedule.set(y, entry);
                    entry = schedule.get(x);
                }
            }
        }

        //Check to make sure we have a 0 day for pitch temps
        if( schedule.get(0).getDay() != 0 ) {
            return false;
        }

        setSchedule(schedule);
        return true;

    }

    /**
     * @return the schedule
     */
    public ArrayList<Entry> getSchedule() {
        return schedule;
    }

    /**
     * @param schedule the schedule to set
     */
    public void setSchedule(ArrayList<Entry> schedule) {

        this.schedule = schedule;
    }

    public boolean compare(FermenterConfig config) {
        boolean same = true;
        same = same && mode == config.getMode();
        same = same && schedule.size() == config.getSchedule().size();
        if( same )
        {
            for(int x=0; x<schedule.size(); x++)
            {
                same = same && schedule.get(x).equals(config.getSchedule().get(x));
            }
        }

        return same;
    }

    public FermenterConfig duplicate() {
        FermenterConfig newConfig = new FermenterConfig();
        newConfig.setMode(mode);
        ArrayList<Entry> newSchedule = new ArrayList<Entry>();
        for (Entry entry : schedule) {
            newSchedule.add(new Entry(entry.getDay(), entry.getTarget()));
        }
        newConfig.setSchedule(newSchedule);

        return newConfig;
    }
}
