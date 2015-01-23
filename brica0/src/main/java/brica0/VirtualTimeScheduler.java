package brica0;

import java.util.PriorityQueue;

public class VirtualTimeScheduler extends Scheduler {

    class Event {
        private double t_;
        private Module module_;
         
        Event(double t, Module module) {
            t_ = t;
            module_ = module;
        }        
        
        public double getTime() {
            return t_;
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
    publid void update(CognitiveArchitecture ca) {
         super.update(ca);
         
         0
    
    }
    
    
    @Override
    public double step() {
        // TODO Auto-generated method stub
        return 0;
    }

}
