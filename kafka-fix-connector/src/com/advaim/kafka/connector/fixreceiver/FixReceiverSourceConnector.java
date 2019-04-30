package com.advaim.kafka.connector.fixreceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

public class FixReceiverSourceConnector extends SourceConnector {
    public static final String CONFIG_FILE = "file";
    public static final String TOPIC_CONFIG = "topic";

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(CONFIG_FILE, Type.STRING, null, Importance.HIGH, "QuickFix Config File")
            .define(TOPIC_CONFIG, Type.STRING, null, Importance.HIGH, "topic");

    private String filename;
    private String topic;

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        AbstractConfig parsedConfig = new AbstractConfig(CONFIG_DEF, props);
        filename = parsedConfig.getString(CONFIG_FILE);
        topic = parsedConfig.getString(TOPIC_CONFIG);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return FixReceiverSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();
        Map<String, String> config = new HashMap<>();
        if (filename != null) config.put(CONFIG_FILE, filename);
        if (topic != null) config.put(TOPIC_CONFIG, topic);
        configs.add(config);
        return configs;
    }

    @Override
    public void stop() {
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }
}
