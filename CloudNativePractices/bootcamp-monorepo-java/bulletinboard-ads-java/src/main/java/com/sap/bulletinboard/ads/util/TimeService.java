package com.sap.bulletinboard.ads.util;

import java.time.Instant;

@FunctionalInterface
public interface TimeService {

    Instant now();
}
