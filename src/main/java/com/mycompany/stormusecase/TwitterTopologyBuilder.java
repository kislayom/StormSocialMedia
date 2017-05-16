/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.stormusecase;

import com.spout.TwitterSpout;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 *
 * @author impadmin
 */
public class TwitterTopologyBuilder {

    public static void main(String args[]) throws InterruptedException {

        String[] arguments = args.clone();
        Config config = new Config();
        config.setDebug(false);
        TopologyBuilder builder = new TopologyBuilder();
        // builder.
        builder.setSpout("twitter-spout", new TwitterSpout("twitter", "twitter-spout"), 1);

        builder.setBolt("twitter-hashtag-reader-bolt", new FirstBolt(), 1)
                .shuffleGrouping("twitter-spout");
        
        builder.setBolt("AggregateBolt", new TwitterTestAggregate(), 2)
                // .shuffleGrouping("twitter-spout");
                .fieldsGrouping("twitter-hashtag-reader-bolt","stream1",new Fields("hashtag"));

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("TwitterHashtagStorm3", config,
                builder.createTopology());
        Thread.sleep(10000);
        //cluster.shutdown();
    }

}
