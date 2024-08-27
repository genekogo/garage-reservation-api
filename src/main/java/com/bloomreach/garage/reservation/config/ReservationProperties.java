package com.bloomreach.garage.reservation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "garage.reservation")
public class ReservationProperties {

    private int maxAdvanceDays;
    private int minAdvanceMinutes;
    private int defaultSlotDuration;
}
