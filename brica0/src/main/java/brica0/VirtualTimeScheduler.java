package brica0;

import java.util.PriorityQueue;
import java.lang.Comparable;

public class VirtualTimeScheduler extends Scheduler {

    class Event implements Comparable<Event> {
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

        @Override
        public int compareTo(Event o) {
            if (this.time_ > o.getTime()) {
                return 1;
            }
            else if (this.time_ < o.getTime()) {
                return -1;
            }
            else // this.time_ == o.getTime()
            {
                return 0;
            }
        }
    }

    
    protected PriorityQueue<Event> eventQueue_;
    
    public VirtualTimeScheduler() {
        eventQueue_ = new PriorityQueue<Event>();
    }
    
    public Event peekNextEvent() {
        return eventQueue_.peek();
    }
    

    @Override
    public void update(CognitiveArchitecture ca) {
         super.update(ca);
         
         eventQueue_ = new PriorityQueue<Event>();
         for (Module m: modules) {
             m.input(this.currentTime);
             m.fire();
             eventQueue_.add(new Event(m.getLastInputTime() + m.getInterval(), m));
         }
    }
    
    
    @Override
    public double step() {
        Event e = eventQueue_.poll();

        assert currentTime <= e.getTime();
        currentTime = e.getTime();

        Module m = e.getModule();
        m.output(currentTime);
        m.input(currentTime);
        m.fire();
        
        eventQueue_.add(new Event(currentTime + m.getInterval(), m));
        
        return currentTime;
    }

}
