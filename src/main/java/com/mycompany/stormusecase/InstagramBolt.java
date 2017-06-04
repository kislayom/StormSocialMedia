/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.stormusecase;

import java.net.InetAddress;
import java.util.Map;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import youtube.tests.InstaCall;
import youtube.tests.Youtube;

/**
 *
 * @author impadmin
 */
public class InstagramBolt implements IRichBolt {

    TransportClient client;

    @Override
    public void prepare(Map map, TopologyContext tc, OutputCollector oc) {
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        } catch (Exception exc) {
            System.out.println(exc);
        }
    }

    @Override
    public void execute(Tuple tuple) {
        String hash=tuple.getStringByField("hashtag");
        System.out.println("Insta bolt "+hash);
        try{
        double count = InstaCall.getCount(hash);
        SocialMediaBeans bean= new SocialMediaBeans(hash, count, "instagram");
         ObjectMapper mapper = new ObjectMapper();
         
         IndexResponse response = client.prepareIndex("twitter_integration", "socialmedia")
                                .setSource(mapper.writeValueAsString(bean))
                                .get();
         }catch(Exception exc){
             System.out.println(exc);
         }

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

}
