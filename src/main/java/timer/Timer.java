package timer;

import java.time.Duration;

/**
 * Creates a timer that can use milliseconds or nanoseconds. (Milliseconds will be default)
 * You can start, pause and unpause the timer.
 * Timer also starts automatically when you create the object.
 * You can just start the timer again multiple times, no stop method needed.
 * Use the time -method to see what the time of the timer is.
 */
public class Timer {
	public enum Type {
		MILLIS, NANO;
	}
	
	private final Type type;
	private long timeStart;
	private long pauseStart;
	private long pauseTotal = 0;
	private long offset = 0;
	private boolean paused = false;
	
	public Timer() {
		this(Type.MILLIS);
	}
	
	public Timer(Type type) {
		this.type = type;
		timeStart = getCurrentTime(); //if doesn't want to call start separately
	}
	
	public Timer(Type type, long startTime) {
		this.type = type;
		timeStart = getCurrentTime() - startTime; //if doesn't want to call start separately
	}
	
	public Type getType() {
		return type;
	}
	
	public void start() {
		start(0);
	}
	
	public void start(long startTime) {
		timeStart = getCurrentTime() - startTime;
		pauseTotal = 0;
		offset = 0;
		paused = false;
	}
	
	public void startPaused() {
		start();
		pause();
	}
	
	public void startPaused(long startTime) {
		start(startTime);
		pause();
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
	
	public boolean isPaused() {
		return paused;
	}
	
	public long time() {
		if (paused) {
			return timeBetween(timeStart, pauseStart) - pauseTotal + offset; //pauseTotal hasn't been updated yet, so we cant use currentTime
		}
		return timeSince(timeStart) - pauseTotal + offset;
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

	public void skip(long amount) {
		offset += amount;
		if (time() < 0) {
			start(0);
		}
	}
}
