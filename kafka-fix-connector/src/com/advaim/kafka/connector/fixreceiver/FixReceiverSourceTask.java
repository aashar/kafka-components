package com.advaim.kafka.connector.fixreceiver;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.SessionSettings;

public class FixReceiverSourceTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(FixReceiverSourceTask.class);
    public static final String FILENAME_FIELD = "filename";
    public  static final String POSITION_FIELD = "position";
    private static final Schema VALUE_SCHEMA = Schema.STRING_SCHEMA;

    private String filename;
    private FixDriver engine;
    private String topic = null;
    
    private BlockingQueue<String> events = new LinkedBlockingQueue<String>();

    private Long streamOffset;

    @Override
    public String version() {
        return new FixReceiverSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        filename = props.get(FixReceiverSourceConnector.CONFIG_FILE);
        topic = props.get(FixReceiverSourceConnector.TOPIC_CONFIG);
        InputStream inputStream;
		try {
			inputStream = new FileInputStream(filename);
	        SessionSettings settings = new SessionSettings(inputStream);
	        inputStream.close();

	        engine = new FixDriver(settings);
	        engine.start(events);
		} catch (Exception e) {
			log.error("Error starting FIX Engine", e);
		}
		log.debug("Topic:" + topic);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
		log.debug("Topic:" + topic);
        ArrayList<SourceRecord> records = null;

        while (!events.isEmpty()) {
            String line = events.take();
            log.trace("Read a line {}", line);
            if (records == null)
                records = new ArrayList<>();
            records.add(new SourceRecord(offsetKey(filename), offsetValue(streamOffset), topic, null,
                    null, null, VALUE_SCHEMA, line, System.currentTimeMillis()));
        }

        return records;
    }

    @Override
    public void stop() {
        log.trace("Stopping");
        synchronized (this) {
	        engine.stop();
	        this.notify();
        }
    }

    private Map<String, String> offsetKey(String filename) {
        return Collections.singletonMap(FILENAME_FIELD, filename);
    }

    private Map<String, Long> offsetValue(Long pos) {
        return Collections.singletonMap(POSITION_FIELD, pos);
    }
}
