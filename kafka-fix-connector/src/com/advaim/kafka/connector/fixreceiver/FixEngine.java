package com.advaim.kafka.connector.fixreceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ConfigError;
import quickfix.DoNotSend;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.UnsupportedMessageType;

public class FixEngine extends quickfix.MessageCracker implements quickfix.Application {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private BlockingQueue<String> events = new LinkedBlockingQueue<String>();

    public FixEngine(SessionSettings settings) throws ConfigError, FieldConvertError { }
    public void onCreate(SessionID sessionID) { }
    public void onLogon(SessionID sessionID) { }
    public void onLogout(SessionID sessionID) { }
    public void toAdmin(quickfix.Message message, SessionID sessionID) { }
    public void toApp(quickfix.Message message, SessionID sessionID) throws DoNotSend { }
    public void fromAdmin(quickfix.Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat,
            IncorrectTagValue, RejectLogon { }

    public void fromApp(quickfix.Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat,
            IncorrectTagValue, UnsupportedMessageType {
        log.debug("{} from {}", message.toString(), sessionID.toString());
        try {
			events.put(message.toString());
		} catch (InterruptedException e) {
			log.error("Error queueing incoming message", e);
		}
    }
    
    public List<String> poll() throws InterruptedException {
    	List<String> retList = new ArrayList<>();
    	events.drainTo(retList);
    	return retList;
    }
}
