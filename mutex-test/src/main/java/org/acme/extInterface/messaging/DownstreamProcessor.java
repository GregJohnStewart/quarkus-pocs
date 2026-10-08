package org.acme.extInterface.messaging;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.acme.model.AppMessage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
public class DownstreamProcessor {

    @Getter
    private List<AppMessage> received;

    @PostConstruct
    void setup() {
        this.reset();
    }


    public void reset(){
        this.received = new ArrayList<>();
    }
    public void add(AppMessage message){
        this.received.add(message);
    }
    public int getNumReceived(){
        return this.received.size();
    }
}
