package main.util;

import main.info.Constants;
import main.model.*;

import javax.swing.plaf.nimbus.AbstractRegionPainter;
import java.io.IOException;
import java.util.*;

public class ConformanceChecker {

    public List<Transition> baseTransitions;
    public List<Place> basePlaces;

    public void replay(List<Trace> traceList, PetriNet petriNet){
        for (int i = 0; i < traceList.size(); i++) {
            this.basePlaces = petriNet.getPlaces();
            this.baseTransitions = petriNet.getTransitions();
            System.out.println("-------------------------------------------开始遍历第"+(i+1)+"条轨迹------------------------------------------------");
            System.out.println(traceList.get(i).toString());
            replay(traceList.get(i));
            System.out.println("-------------------------------------------第"+(i+1)+"条轨迹遍历结束------------------------------------------------");

        }

    }

    public void replay(Trace trace){
        List<Event> events = trace.getEvents();
        // 找到初始库所
        for (int i = 0; i < basePlaces.size(); i++) {
            if( basePlaces.get(i).isInitialPlace() ) {
                basePlaces.get(i).setAmountOfTokens(1);
                trace.updateProduced(1);
                break;
            }
        }
        for (int i = 0; i < events.size(); i++) {
            traverseTransition(trace,baseTransitions, Constants.tempStr,events,i,false);
        }
//        int remaining = 0;
//        for( int place = 0; place < basePlaces.size(); ++place) {
//            remaining += basePlaces.get(place).getAmountOfTokens();
//            if(basePlaces.get(place).isFinalPlace()) {
//                trace.updateConsumed(1);
//                remaining -= 1;
//            }
//            basePlaces.get(place).setAmountOfTokens(0);
//        }
//       trace.updateRemaining(remaining);
        trace.updateRemaining(getRemain(baseTransitions,trace));

    }



    public void traverseTransition(Trace trace,List<Transition> transitionList,String matchWords,
                                   List<Event> events, int eventIndex,boolean isReStart){

        String eventName = events.get(eventIndex).getEventName()+matchWords;
        for( int i = 0; i < transitionList.size(); ++i ){
            List<Place> placeWithoutTokens = transitionList.get(i).getPlacesWithoutTokens();
            String transitionName =  transitionList.get(i).getTransitionName();
            if (transitionName.equals(eventName)){
                HashMap<Integer,Transition> repetitiveTransition = new HashMap<>();
                isExistRepetitiveTransition(repetitiveTransition,i,transitionList,transitionName);
                if (placeWithoutTokens.size()!=0){
                    List<Place> inputPlaces = transitionList.get(i).getInputPlaces();
                    if (repetitiveTransition.size()==1){
                        for( int indexPlacesWithoutTokens = 0; indexPlacesWithoutTokens < placeWithoutTokens.size(); ++indexPlacesWithoutTokens ) {
                            for( int indexInputPlaces = 0; indexInputPlaces < inputPlaces.size(); ++indexInputPlaces ) {
                                if( placeWithoutTokens.get(indexPlacesWithoutTokens) == inputPlaces.get(indexInputPlaces) ) {
                                    placeWithoutTokens.get(indexPlacesWithoutTokens).addToken();
                                }
                            }
                        }
                        if (i!=0){
                            trace.updateMissing(placeWithoutTokens.size());
                            System.out.println("***********************找到缺失任务"+eventName+"******************");
                        }
                    }else {
                        continue;
                    }
                }
                repetitiveTransition.remove(i);
                printCurTransitionInfo(transitionList,transitionList.get(i).getTransitionName()+"-"+i+"重复任务前");
                List<Transition> tempTransitions = null;
                if (!isReStart){
                    try {
                        tempTransitions = CopyFunction.deepCopy(transitionList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    resetStatus(transitionList,events,eventIndex,trace,repetitiveTransition);
                    baseTransitions.clear();
                    baseTransitions.addAll(tempTransitions);

                }

                printCurTransitionInfo(transitionList,"执行"+transitionList.get(i).getTransitionName()+"-"+i+"任务前");
                //System.out.println("执行任务前："+transitionList.get(i).getTransitionName()+" index="+i + " 库所状态："+Arrays.toString(transitionList.get(i).getInputPlaces().toArray()));
                transitionList.get(i).fire();
                printCurTransitionInfo(transitionList,"执行"+transitionList.get(i).getTransitionName()+"-"+i+"任务后");
                //System.out.println("执行任务后："+transitionList.get(i).getTransitionName()+" index="+i+ " 库所状态："+Arrays.toString(transitionList.get(i).getInputPlaces().toArray()));
                trace.updateConsumed(transitionList.get(i).getInputPlaces().size());
                trace.updateProduced(transitionList.get(i).getOutputPlaces().size());
                repetitiveTransition.clear();
                break;
            }
        }
    }

    /**
     * 判断是否存在重复可执行的任务
     * @param repetitiveTransition
     * @param initIndex
     * @param transitionList
     * @param transitionName
     */
    public void isExistRepetitiveTransition(HashMap<Integer,Transition> repetitiveTransition
            ,int initIndex,List<Transition> transitionList
            ,String transitionName){

        repetitiveTransition.put(initIndex,transitionList.get(initIndex));
        for (int j = 0; j < transitionList.size(); j++) {

            //System.out.println("｜查找重复任务时："+transitionList.get(j).getTransitionName()+"index=："+j+"的缺失token的库所数量："+transitionList.get(j).getPlacesWithoutTokens().size());
            if (initIndex!=j && transitionName.equals(transitionList.get(j).getTransitionName())
                    && transitionList.get(j).getPlacesWithoutTokens().size()==0){
                System.out.println("找到可执行重复任务:"+"name="+transitionList.get(j).getTransitionName()+"  初始index = "+initIndex+"  重复任务index = "+j);

                repetitiveTransition.put(j,transitionList.get(j));

            }
        }
        //System.out.println("------------------查找结束---------------------");

    }

    public void resetStatus(List<Transition> transitionList,List<Event> events,int eventIndex,
                            Trace trace,HashMap<Integer,Transition> transitionHashMap){
        int temp=0 ;
        int[] indexArr = new int[transitionHashMap.size()];
        for (Map.Entry<Integer, Transition> entry : transitionHashMap.entrySet()) {
            indexArr[temp] = entry.getKey();
        }
        for (int j = 0; j < indexArr.length; j++) {
            trace.updateProduced(transitionHashMap.size()+1);
            List<Transition> newTransition = null;
            try {
                newTransition = CopyFunction.deepCopy(transitionList);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            int i = eventIndex;
            System.out.println("---------------------------开始执行回调---------------------------------");
            while (i<events.size()){
                if (i == eventIndex){
                    System.out.println("回调首次执行前："+newTransition.get(indexArr[j]).getTransitionName()+" index="+indexArr[j]+" 库所状态："+Arrays.toString(newTransition.get(indexArr[j]).getInputPlaces().toArray()));
                    newTransition.get(indexArr[j]).fire();
                    System.out.println("回调首次执行后："+newTransition.get(indexArr[j]).getTransitionName()+" index="+indexArr[j]+" 库所状态："+Arrays.toString(newTransition.get(indexArr[j]).getInputPlaces().toArray()));

                    trace.updateConsumed(newTransition.get(indexArr[j]).getInputPlaces().size());
                    trace.updateProduced(newTransition.get(indexArr[j]).getOutputPlaces().size());
                }else {
                    traverseTransition(trace,newTransition,Constants.tempStr,events,i,true);
                }
                i++;
            }

            /**
             * todo 遗留的值 待处理  getRemain()
             */
            trace.updateRemaining(getRemain(newTransition,trace));
            System.out.println("---------------------------执行回调结束---------------------------------");

        }
    }


    public void printCurTransitionInfo(List<Transition> transitions,String info){
        System.out.println("--------------------------------"+info+"------------------------------");
        for (int i = 0; i < transitions.size(); i++) {
            System.out.println("|"+transitions.get(i).getTransitionName()+" index="+i + " 库所状态："+Arrays.toString(transitions.get(i).getInputPlaces().toArray())+"|");
        }
        System.out.println("-------------------------------------------------------------------------------------");
    }


    public int getRemain(List<Transition> transitions,Trace trace){
        int count=0;
        for (int i = 0; i < transitions.size(); i++) {
            for (int j = 0; j < transitions.get(i).getInputPlaces().size(); j++) {
                count += transitions.get(i).getInputPlaces().get(j).getAmountOfTokens();
                if(transitions.get(i).getInputPlaces().get(j).isFinalPlace()) {
                    trace.updateConsumed(1);
                    System.out.println("找到最后一个库所");

                    count -= 1;
                }
                transitions.get(i).getInputPlaces().get(j).setAmountOfTokens(0);

            }
            if (i == transitions.size()-1){
                for (int k = 0; k < transitions.get(i).getOutputPlaces().size(); k++) {
                    count += transitions.get(i).getOutputPlaces().get(k).getAmountOfTokens();
                    if(transitions.get(i).getOutputPlaces().get(k).isFinalPlace()) {
                        trace.updateConsumed(1);
                        System.out.println("找到最后一个库所");
                        count -= 1;
                    }
                    transitions.get(i).getOutputPlaces().get(k).setAmountOfTokens(0);
                }
            }
        }
        return count;


    }
}

