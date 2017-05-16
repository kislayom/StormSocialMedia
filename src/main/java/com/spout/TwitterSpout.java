/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spout;


import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;



/**
 *
 * @author impadmin
 */
public class TwitterSpout extends KafkaSpout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3720677758471388833L;
	private static final Logger logger= Logger.getLogger(TwitterSpout.class);

    public TwitterSpout(String topic, String id) {
       super(getSpoutConfig(topic, id));
    }

	/**
	 * Constructor method Topic should be received from topology.xml Id can be
	 * kept as constant or can ve received from topology.xml
	 * 
	 * @param topic
	 * @param id


	/**
	 * Method to initialize Spout configuration
	 * 
	 * @param topic
	 * @param id
	 * @return SpoutConfig
	 */
	private static SpoutConfig getSpoutConfig(String topic, String id) {


		SpoutConfig spoutConfig = new SpoutConfig(new ZkHosts("localhost:2181",
				"/brokers"), topic,
				"/consumers/twitter", id);
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
                
		return spoutConfig;
	}

}
