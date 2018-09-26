package com.zandernickle.fallproject_pt1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Temporarily contains registration code related to this application's individual modules.
 * At least some of this functionality will eventually be merged with a ViewModel class.
 */
public class ModuleUtil {

    /**
     * Returns a list of modules to show in the master view / navigation menu. Add additional
     * navigable modules inside this method.
     *
     * Important! Call this method from MasterFragment.
     *
     * @return the list of visible / navigable modules.
     */
    public static List<Module> getVisibleModuleList() {
        return new ArrayList<Module>() {{
            add(Module.HEALTH);
            add(Module.WEATHER);
            add(Module.HIKES);
        }};
    }

    /**
     * Maps each of the application's modules as a key:value pair of Module:Class. Register
     * all additional modules within this method.
     *
     * Important! Classes may be Fragments or Activities.
     *
     * @return a HashMap of Modules:Class.
     */
    public static HashMap<Module, Class<?>> mapModuleList() {
        return new HashMap<Module, Class<?>>() {{
            put(Module.SIGN_IN, SignInFragment.class);
            put(Module.HEALTH, BMRFragment.class);
            put(Module.HIKES, HikesFragment.class);
            put(Module.WEATHER, WeatherFragment.class);
            put(Module.FITNESS_INPUT, FitnessInputFragment.class);
            put(Module.PLAYGROUND, PlaygroundFragment.class);
            put(Module.MASTER_LIST, MasterListFragment.class);
            put(Module.UPDATE_GOALS, FitnessInputFragment.class);
        }};
    }
}
