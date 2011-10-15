package net.sourceforge.stat4j.calculators;

import net.sourceforge.stat4j.Metric;

import java.util.Properties;

/**
 * Error/etc rate in the last N minutes (or whatever period you choose).
 * @author Jakub Holy
 */
public class RunningRate extends CalculatorAdapter {


	protected long starttimestamp;
	protected long 		period;
	protected long		occurances;


	public RunningRate() {
		reset();
	}


	public void applyMetric(Metric metric) {

		++occurances;
		if (metric.isSingle()) {

            long metricCaptureTime = metric.getTimestamp();
            
            long metricCalculationTimeMs = metricCaptureTime - starttimestamp;
            if (metricCalculationTimeMs > period) {
                reset();
                ++occurances;
            }

			setTimestamp(metricCaptureTime);
		}
	}


	public double getResult() {
		return occurances;
	}

	public void reset() {
		occurances = 0;
		starttimestamp = System.currentTimeMillis();
	}

	public void init(String name,Properties properties) {
		// read per
		String periodStr = properties.getProperty("period","60000");
		this.period = Long.parseLong(periodStr);

		super.init(name);
	}
}
