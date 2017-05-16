/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.stormusecase;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.storm.Config;
import org.apache.storm.Constants;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;

/**
 *
 * @author impadmin
 */
public class FirstBolt implements IRichBolt {

    OutputCollector collector;

    @Override
    public void prepare(Map map, TopologyContext tc, OutputCollector oc) {
        this.collector = oc;
    }

    @Override
    public void execute(Tuple tuple) {
        if (isTickTuple(tuple)) //System.out.println("God is great"+tuple.getString(0));
        {
            
        } else {
            String rawJson = tuple.getString(0);
            try {
                Status createStatus = TwitterObjectFactory.createStatus(rawJson);
                HashtagEntity[] hashtagEntities = createStatus.getHashtagEntities();
                if(hashtagEntities.length==0){
                    //System.out.println(new Date()+" Filter: No hashtag discarding event");
                    return;
                }
                for (HashtagEntity hash : hashtagEntities) {
                    
                    collector.emit("stream1", new Values(createStatus,hash.getText()));
                }

                collector.ack(tuple);
                
                // collector.emit(new Values(createStatus));
            } catch (Exception exc) {

            }

        }
    }

    @Override
    public void cleanup() {
        //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
        ofd.declareStream("stream1", new Fields("status", "hashtag"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config conf = new Config();
       // conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 30);
        return conf;
    }

    protected static boolean isTickTuple(Tuple tuple) {
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
                && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
    }

  
}
