package main.model;

import java.util.Collections;
import java.util.List;

public class PetriNet {

    /**
     *  库索列表
     */
    public List<Place> places;
    /**
     * 变迁列表
     */
    private List<Transition> transitions;

    public PetriNet(List<Place> places,List<Transition> transitions){
        this.places = places;
        this.transitions = transitions;

        /**
         *
         * 38数据集需要反转
         *
         * 自己构建就注释掉
         */

        Collections.reverse(transitions);
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }
}
