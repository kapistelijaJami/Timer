package timer;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		Timer timer = new Timer();
		
		for (int i = 0; i < 10; i++) {
			Thread.sleep(500);
			System.out.println(timer.time());
		}
		
		timer.pause();
		System.out.println("paused at " + timer.time());
		Thread.sleep(500);
		System.out.println("still paused at " + timer.time());
		timer.unPause();
		System.out.println("unpaused");
		
		Thread.sleep(500);
		System.out.println(timer.time());
	}
}
