package net.sourceforge.stat4j.calculators;

import net.sourceforge.stat4j.Metric;
import net.sourceforge.stat4j.Reading;
import junit.framework.TestCase;

import java.util.Properties;

public class RunningRateTest extends TestCase {

    private static class SettableReading extends Reading {
        public SettableReading withTimestamp(long ms) {
            timestamp = ms;
            return this;
        }
    }

    private RunningRate rate;

    @Override
    protected void setUp() throws Exception {
        rate = new RunningRate();
        Properties properties = new Properties();
        properties.setProperty("period", "1000");   // 1 sec
        rate.init("RunningRate", properties);
    }


    public void testShouldAccumulateOccurrences() throws Exception {

        Metric metric = new Metric("metric", new Reading());

        for (int i = 0; i < 5; i++) {
            rate.applyMetric(metric);
        }

        assertEquals(5, (int) rate.getResult());
    }

    public void testShouldResetWhenLivingLongerThenItsPeriod() throws Exception {

        long timePeriodPlusOne = rate.starttimestamp + rate.period + 1;

        Reading reading = new SettableReading().withTimestamp(timePeriodPlusOne);
        Metric muchYoungerMetric = new Metric("metric", reading);

        // Old period's readings
        Metric oldMetric = new Metric("metric", new Reading());
        rate.applyMetric(oldMetric);
        assertEquals(1, (int) rate.getResult());

        // Force new period by pretending a measure from far future
        rate.applyMetric(muchYoungerMetric);
        assertEquals("Should have been reset!"
                , 1, (int) rate.getResult());
    }
}
