package timer;

import java.time.Duration;

/**
 * Creates a timer that can use milliseconds or nanoseconds. (Milliseconds will be default)
 * You can start, pause and unpause the timer.
 * Timer also starts automatically when you create the object.
 * You can just start the timer again multiple times, no stop method needed.
 */
public class Timer {
	public enum Type {
		MILLIS, NANO;
	}
	
	private final Type type;
	private long timeStart;
	private long pauseStart;
	private long pauseTotal = 0;
	private boolean paused = false;
	
	public Timer() {
		this(Type.MILLIS);
	}
	
	public Timer(Type type) {
		this.type = type;
		timeStart = getCurrentTime(); //if doesn't want to call start separately
	}
	
	public Type getType() {
		return type;
	}
	
	public void start() {
		timeStart = getCurrentTime();
		pauseTotal = 0;
		paused = false;
	}
	
	public void pause() {
		if (paused) {
			return;
		}
		pauseStart = getCurrentTime();
		paused = true;
	}
	
	public void unPause() {
		if (paused) {
			pauseTotal += timeSince(pauseStart);
			paused = false;
		}
	}
	
	public long time() {
		if (paused) {
			return timeBetween(timeStart, pauseStart) - pauseTotal; //pauseTotal hasn't been updated yet, so we cant use currentTime
		}
		return timeSince(timeStart) - pauseTotal;
	}
	
	public Duration timeAsDuration() {
		if (type == Type.MILLIS) {
			return Duration.ofMillis(time());
		} else {
			return Duration.ofNanos(time());
		}
	}
	
	private long getCurrentTime() {
		if (type == Type.MILLIS) {
			return System.currentTimeMillis();
		} else {
			return System.nanoTime();
		}
	}
	
	private long timeSince(long time) {
		return getCurrentTime() - time;
	}
	
	private long timeBetween(long first, long second) {
		return second - first;
	}
}
