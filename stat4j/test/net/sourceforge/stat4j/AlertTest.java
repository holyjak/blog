package net.sourceforge.stat4j;

import junit.framework.TestCase;

import java.util.Properties;

public class AlertTest extends TestCase {

    private static final int QUIET_PERIOD_MS = 1;
    private static final double MEASURE_WARN = 0.0;
    private static final double MEASURE_CRITICAL = 4.0;

    private final Threshold belowOne = Threshold.toThreshold("<1");

    private Alert alert;

    @Override
    protected void setUp() throws Exception {
        alert = new Alert();
        Properties properties = new Properties();
        properties.setProperty("quietperiod", String.valueOf(QUIET_PERIOD_MS));
        properties.setProperty("warn", "<1");
        properties.setProperty("critical", ">3");
        alert.init("test", properties);

    }

    public void test_not_triggered_again_for_quietperiod() throws Exception {
        assertTrue("1st triggering event should pass through", alert.evaluateAlert(MEASURE_WARN));
        assertFalse("Should keep quiet after the first triggering for the length of the quiet period"
                , alert.evaluateAlert(0.0));
    }

    public void test_triggered_again_after_quietperiod_end() throws Exception {
        assertTrue("1st triggering event should pass through", alert.evaluateAlert(MEASURE_WARN));
        simulateQuietPeriodEnded();
        assertTrue("Another event after the quiet period's end should trigger new alert"
                , alert.evaluateAlert(MEASURE_WARN));
    }

    private void simulateQuietPeriodEnded() {
        alert.lastAlertedMs = System.currentTimeMillis() - (QUIET_PERIOD_MS + 1);
    }

    public void test_new_critical_event_triggers_alert_even_in_quiet_period() throws Exception {
        assertTrue("1st triggering event should pass through", alert.evaluateAlert(MEASURE_WARN));
        assertTrue("Following critical alert shall pass through too", alert.evaluateAlert(MEASURE_CRITICAL));
    }

    public void test_reset_after_quiet_period_end() throws Exception {
        alert.evaluateAlert(MEASURE_WARN);      // Start quiet period
        alert.evaluateAlert(MEASURE_CRITICAL);  // Log anyway - critical

        simulateQuietPeriodEnded();

        assertTrue("After reset: 1st triggering event should pass through", alert.evaluateAlert(MEASURE_WARN));
        assertTrue("After reset: Following critical alert shall pass through too", alert.evaluateAlert(MEASURE_CRITICAL));
    }

    public void test_repeated_warn_and_critical_events_ignored_in_quiet_period() throws Exception {
        assertTrue("1st triggering event should pass through", alert.evaluateAlert(MEASURE_WARN));
        assertTrue("Following critical alert shall pass through too", alert.evaluateAlert(MEASURE_CRITICAL));
        assertFalse("Repeated warn shall be ignored in quiet period", alert.evaluateAlert(MEASURE_WARN));
        assertFalse("Repeated critical shall be ignored in quiet period too", alert.evaluateAlert(MEASURE_CRITICAL));
    }
}
