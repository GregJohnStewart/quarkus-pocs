package org.acme.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppMessage {
    private String name;
    private short priority;
}
