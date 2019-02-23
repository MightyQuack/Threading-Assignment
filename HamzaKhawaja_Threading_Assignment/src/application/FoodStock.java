package application;

import static application.Zoo.numOfFeeds;
import static java.lang.System.exit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FoodStock extends Zoo {

	private static Lock lock = new ReentrantLock();
	private static Condition newDeposit = lock.newCondition();
	private int stock = 0;
	private int totalFoodEaten = 0;

	public int getStock() {
		return stock;
	}

	public int getTotalFoodEaten() {
		return totalFoodEaten;
	}

	// Params amount is the amount of food animal is needed to eat and animal is the
	// animal object to be passed
	public void feed(int amount, Animal animal) {
		lock.lock();
		try {
			while (stock < amount) {
				System.out.println("\t\t\tWait For Food\t\t\t" + getStock());
				newDeposit.await();
			}
			stock -= amount;
			totalFoodEaten += amount;
			numOfFeeds++;
			System.out.println("\t\t\tEating\t\t\t\t" + getStock());

			for (Animal a : animals) {
				if (a.getName().equals(animal.getName())) {
					animal.setFoodEaten(amount);
				}
			}
			if (numOfFeeds == 10) {
				System.out.println("Finished Tasks");
				// Perform database operations
				loadDatabase();
				exit(0);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void deposit(int amount) { // deposit amount will be a random number generated in Zoo class
		lock.lock();
		try {
			stock += amount;
			System.out.println("Add " + amount + "kg" + " (" + getStock() + ")");
			newDeposit.signalAll();
		} finally {
			lock.unlock();
		}
	}
}
