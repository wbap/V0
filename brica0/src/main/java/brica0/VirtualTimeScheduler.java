package brica0;

import java.util.PriorityQueue;

public class VirtualTimeScheduler extends Scheduler {

    class Event {
        private double time_;
        private Module module_;
         
        Event(double t, Module m) {
            time_ = t;
            module_ = m;
        }        
        
        public double getTime() {
            return time_;
        }
        
        public Module getModule() {
            return module_;
        }
    }

    
    protected PriorityQueue<Event> eventQueue_;
    
    public VirtualTimeScheduler() {
        eventQueue_ = new PriorityQueue<Event>();
    }

    @Override
    public void update(CognitiveArchitecture ca) {
         super.update(ca);
         
         eventQueue_ = new PriorityQueue<Event>();
         for (Module m: modules) {
             m.input(ca.);
             eventQueue_.add(new Event(m.getLastInputTime() + m.getInterval(), m));
         }
    }
    
    
    @Override
    public double step() {
        // TODO Auto-generated method stub
        return 0;
    }

}
