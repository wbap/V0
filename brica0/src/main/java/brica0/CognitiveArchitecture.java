package brica0;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

public class CognitiveArchitecture extends Module {

    Scheduler scheduler;

    // how does this get connected to sensors / actuators?

    public CognitiveArchitecture(Scheduler s) {
        scheduler = s;
    }

    @Override
    public void addSubModule(String id, Module module) {
        super.addSubModule(id, module);

        updateScheduler();
    }

    public Module getSubModuleRecursive(Module module, String[] list) {
        if(list.length == 0) {
            return module;
        } else {
            String head = list[0];
            String[] copy = new String[list.length - 1];
            System.arraycopy(list, 1, copy, 0, list.length - 1);
            return getSubModuleRecursive(module.getSubModule(head), copy);
        }
    }

    @Override
    public Module getSubModule(String id) {
        String[] list = id.split("\\.");
        String head = list[0];
        String[] copy = new String[list.length - 1];
        System.arraycopy(list, 1, copy, 0, list.length - 1);
        return getSubModuleRecursive(super.getSubModule(head), copy);
    }

    public ArrayList<Module> getAllSubModulesRecursive(Module module) {
        ArrayList<Module> list = module.getAllSubModules();
        ArrayList<Module> subs = new ArrayList<Module>();
        Iterator<Module> iter = list.iterator();
        while(iter.hasNext()) {
            Module item = iter.next();
            subs.addAll(getAllSubModulesRecursive(item));
        }
        list.addAll(subs);
        return list;
    }

    @Override
    public ArrayList<Module> getAllSubModules() {
        ArrayList<Module> list = super.getAllSubModules();
        ArrayList<Module> subs = new ArrayList<Module>();
        Iterator<Module> iter = list.iterator();
        while(iter.hasNext()) {
            Module item = iter.next();
            subs.addAll(getAllSubModulesRecursive(item));
        }
        list.addAll(subs);
        return list;
    }

    @Override
    public void fire() {
        // What to do?
    }

    public double step() {
        return scheduler.step();
    }

    protected void updateScheduler() {
        scheduler.update(this);
    }

}
