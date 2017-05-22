/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.stormusecase;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.storm.Config;
import org.apache.storm.Constants;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import twitter4j.Status;

/**
 *
 * @author impadmin
 */
public class TwitterTestAggregate implements IRichBolt {

    private static final int MAX_GAP_IN_MINUTES = 15;
    ConcurrentHashMap map;
    private int count;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        map = new ConcurrentHashMap(20000);
    }

    @Override
    public void execute(Tuple input) {
        count++;
        if (!isTickTuple(input)) {
            Status status = (Status) input.getValueByField("status");
            String hashtag = input.getStringByField("hashtag");
            Date createdAt = status.getCreatedAt();

            Calendar creationCal = Calendar.getInstance();
            creationCal.setTime(createdAt);
            int createdHour = creationCal.get(Calendar.HOUR_OF_DAY);
            System.out.println("count "+count);
            if(count>1000){
                count=0;
                System.out.println("Processed 1000 events");
                Set keySet = map.keySet();
                Iterator iterator = keySet.iterator();
                int max=0;
                while(iterator.hasNext()){
                    String key = (String) iterator.next();
                    if(key.contains("_"+createdHour)){
                        int value=(int) map.get(key);
                        if(value>max){
                            System.out.println("Largest found so far is "+ key+" its value is "+ value);
                            max=value;
                        }
                    }
                }
            }

            //if(map.containsKey(map))
            Integer count = (Integer) map.get(hashtag + "_" + createdHour);
            if (count != null) {
                count++;
            } else {
                count = 1;
            }
            map.put(hashtag + "_" + createdHour, count);
        } else {

            try {
                TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

                ObjectMapper mapper = new ObjectMapper();

                Set keySet = map.keySet();
                Iterator iterator = keySet.iterator();
                System.out.println("Tick Tuple");

                while (iterator.hasNext()) {
                    //one hour back calendar
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.HOUR, -1);
                    Date oneHourBack = cal.getTime();

                    //curent hour calendar
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(new Date());

                    String key = (String) iterator.next();
                    int keyHour = Integer.parseInt(key.substring(key.lastIndexOf("_")+1));
                    if (keyHour == cal.get(Calendar.HOUR_OF_DAY)||keyHour==cal1.get(Calendar.HOUR_OF_DAY)) {

                        int count = (int) map.get(key);

                        BeanHash bean = new BeanHash();
                        bean.setDate(oneHourBack);
                        bean.setCount(count);
                        bean.setHasgtag(key.substring(0, key.lastIndexOf("_")));
                        //    System.out.println(mapper.writeValueAsString(bean));

                        IndexResponse response = client.prepareIndex("twitteruse", "tweet")
                                .setSource(mapper.writeValueAsString(bean))
                                .get();
                        map.remove(key);
                        System.out.println("removing "+key);
                        //  System.out.println(response + " " + mapper.writeValueAsString(bean));
                    }else{
                        System.out.println("Removing garbage key");
                        map.remove(key);
                    }
                }
            } catch (UnknownHostException ex) {
                Logger.getLogger(TwitterTestAggregate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TwitterTestAggregate.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    @Override
    public void cleanup() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config conf = new Config();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 160);
        return conf;
    }

    protected static boolean isTickTuple(Tuple tuple) {
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
                && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
    }

}
