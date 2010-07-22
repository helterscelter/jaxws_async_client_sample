package test.client;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class TimingThreadPool extends ThreadPoolExecutor {

	public TimingThreadPool() {
		super(1, 1, 0L, TimeUnit.SECONDS, new LoggingArrayBlockingQueue<Runnable>(100));
	}

	private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();
	private final AtomicLong numTasks = new AtomicLong();
	private final AtomicLong totalTime = new AtomicLong();

	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		startTime.set(System.nanoTime());
		System.out.println(startTime.get() + ":: Runnable " + r + " (" + r.getClass().getCanonicalName()	+ ") starts in " + t);

	}

	protected void afterExecute(Runnable r, Throwable t) {
		try {
			long endTime = System.nanoTime();
			long taskTime = endTime - startTime.get();
			numTasks.incrementAndGet();
			totalTime.addAndGet(taskTime);
			System.out.println(endTime + ":: Runnable " + r + " ends after " + taskTime + "ns.");
			if (t != null) {
				t.getCause().printStackTrace();
			}
		} finally {
			super.afterExecute(r, t);
			
			if (this.getQueue().isEmpty()){
				System.out.println("No Jobs left in Queue. Shutting down TimingThreadPool ");
				this.shutdown();
			}
		}
	}

	protected void terminated() {
		try {
			System.out.println("Terminated: avg time=" + totalTime.get()/ numTasks.get() + "ns.");
		} finally {
			super.terminated();
		}
	}
	
	
	
	private static class LoggingArrayBlockingQueue<T> extends ArrayBlockingQueue<T> {

		public LoggingArrayBlockingQueue(int capacity, boolean fair, Collection<? extends T> c) {
			super(capacity, fair, c);
			// TODO Auto-generated constructor stub
		}

		public LoggingArrayBlockingQueue(int capacity, boolean fair) {
			super(capacity, fair);
			// TODO Auto-generated constructor stub
		}

		public LoggingArrayBlockingQueue(int capacity) {
			super(capacity);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean add(T e) {
			// TODO Auto-generated method stub
			System.out.println("Add: " + e);
			System.out.println(getStackTrace(Thread.currentThread().getStackTrace()));
			return super.add(e);
		}

		@Override
		public boolean offer(T e, long timeout, TimeUnit unit)
				throws InterruptedException {
			System.out.println("Offer: " + timeout + " " + e);
			System.out.println(getStackTrace(Thread.currentThread().getStackTrace()));
			// TODO Auto-generated method stub
			return super.offer(e, timeout, unit);
		}

		@Override
		public boolean offer(T e) {
			// TODO Auto-generated method stub
			System.out.println("Offer: " + e);
			System.out.println(getStackTrace(Thread.currentThread().getStackTrace()));

			return super.offer(e);
		}

		@Override
		public T peek() {
			System.out.println("Peek");
			System.out.println(getStackTrace(Thread.currentThread().getStackTrace()));
			// TODO Auto-generated method stub
			return super.peek();
		}

		@Override
		public T poll() {
			System.out.println("Poll");
			System.out.println(getStackTrace(Thread.currentThread().getStackTrace()));
			// TODO Auto-generated method stub
			return super.poll();
		}

		@Override
		public T poll(long timeout, TimeUnit unit) throws InterruptedException {
			System.out.println("Poll " + timeout);
			System.out.println(getStackTrace(Thread.currentThread().getStackTrace()));
			// TODO Auto-generated method stub
			return super.poll(timeout, unit);
		}

		@Override
		public void put(T e) throws InterruptedException {
			System.out.println("Put " + e);
			System.out.println(getStackTrace(Thread.currentThread().getStackTrace()));
			// TODO Auto-generated method stub
			super.put(e);
		}

		@Override
		public T take() throws InterruptedException {
			System.out.println("Take");
			System.out.println(getStackTrace(Thread.currentThread().getStackTrace()));

			// TODO Auto-generated method stub
			return super.take();
		}

		private String getStackTrace(StackTraceElement[] st) {
			StringBuilder sb = new StringBuilder();
			sb.append("\n");
			for (StackTraceElement e : st) {
				sb.append(e.toString()).append("\n");

			}
			return sb.toString();

		}

	}	
}

