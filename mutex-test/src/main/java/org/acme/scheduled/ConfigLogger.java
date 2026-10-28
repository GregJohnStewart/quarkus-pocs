package org.acme.scheduled;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.NoSuchElementException;
import java.util.TreeMap;

@Singleton
@Slf4j
public class ConfigLogger {


    void onStart(
            @Observes
            StartupEvent ev
    ) {
        if (log.isDebugEnabled()) {
            TreeMap<String, String> configMap = new TreeMap<>();

            for(String curProp : ConfigProvider.getConfig().getPropertyNames()){
                String value;
                try {
                    value = ConfigProvider.getConfig().getValue(curProp, String.class);
                } catch(NoSuchElementException e) {
                    value = "";
                }
                configMap.put(curProp, value);
            }

            StringBuilder sb = new StringBuilder();
            for (String curProp : configMap.keySet()) {

                sb.append('\t');
                sb.append(curProp);
                sb.append('=');
                sb.append(configMap.get(curProp));
                sb.append(System.lineSeparator());
            }
            log.debug("Configuration: \n{}", sb);
        }
//        log.info("Got config resolution: {}", ConfigProvider.getConfig().getValue("remote://${amqp-host}:${amqp-port}", String.class));
    }
}
