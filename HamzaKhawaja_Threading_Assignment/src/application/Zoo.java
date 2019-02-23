package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Zoo {

	// Database Variables
	static Connection connection;
	static Statement statement;
	static ResultSet resultSet;

	// Animal object
	static Animal animal;

	// FoodStock object
	public static FoodStock foodStock = new FoodStock();

	// ArrayList to hold Animal objects
	public static ArrayList<Animal> animals = new ArrayList<>();

	// Int to hold number of times to feed animals
	static int n = 10;
	static int i = 1;

	// Goes up to N
	static int numOfFeeds = 0;

	public static void main(String[] args) {

		System.out.println("Deposit Food \t\tFeed Animals\t\t Stock Status (kg)");

		// Creating Animal Objects to store into ArrayList
		Animal rhino = new Animal("Rhino", 9);
		Animal cow = new Animal("Cow", 7);
		Animal Horse = new Animal("Horse", 5);
		Animal Zebra = new Animal("Zebra", 5);
		Animal Deer = new Animal("Deer", 3);

		// Storing into ArrayList
		animals.add(rhino);
		animals.add(cow);
		animals.add(Horse);
		animals.add(Zebra);
		animals.add(Deer);

		// Executing
		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.execute(new DepositTask());
		executor.execute(new WithdrawTask());

		// Shutdown executor
		executor.shutdown();
	}

	static void loadDatabase() {
		loadDriver();
		createConnection();
		createStatement();
		dropTable(); // Drop table every time you run program
		createTable();
		insert();
		readAllRecords(); // Question 1
		readMaxWithMetaData(); // Question 2
		readTotalWithMetaData(); // Question 3
	}

	// Method to load JDBC driver
	static void loadDriver() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver loaded successfuly");
		} catch (Exception e) {
			System.out.println("Error: " + e + "\n");
		}
	}

	// Method to create connection with database
	static void createConnection() {
		// Database URL
		String databaseURL = "jdbc:mysql://localhost:3306/ZooDB";
		// String variables for testing purposes
		String username = "root";
		String password = "root";

		try {
			connection = DriverManager.getConnection(databaseURL, username, password);
			System.out.println("Connection Established!");
		} catch (SQLException e) {
			System.out.println("There was an error with connecting! " + e + "\n");
		}
	}

	// Method to create statement
	static void createStatement() {
		try {
			statement = connection.createStatement();
			System.out.println("Statement Created!");
		} catch (SQLException e) {
			System.out.println("There was an error with Statement Connection " + e + "\n");
		}
	}

	// Method to create table in database
	static void createTable() {
		String createTable = "create table FeedingData(animalName varchar(50),amountFed int);";

		try {
			statement.executeUpdate(createTable);
		} catch (SQLException e) {
			System.out.println("There was an error with creating a table" + e + "\n");
		}
	}

	// Method to drop existing table in database
	static void dropTable() {
		String dropTable = "drop table feedingdata";

		try {
			statement.executeUpdate(dropTable);
		} catch (SQLException e) {
			System.out.println("Error with dropping table " + e + "\n");
		}
	}

	// Method to insert data into table
	static void insert() {
		for (Animal a : animals) {
			String insert = "insert into FeedingData values ('" + a.getName() + "','" + a.getFoodEaten() + "')";
			try {
				statement.executeUpdate(insert);
				System.out.println("Insertion was made");
			} catch (SQLException e) {
				System.out.println("Error with inserting into table " + e + "\n");
			}
		}
	}

	// Method to read from table
	static void readAllRecords() {
		String read = "select * from FeedingData";
		System.out.println("\n1. All Rows:\n");
		try {
			resultSet = statement.executeQuery(read);
			while (resultSet.next()) {
				String name = resultSet.getString(1);
				int foodEaten = resultSet.getInt(2);
				System.out.println(name + " " + foodEaten);
			}
		} catch (SQLException e) {
			System.out.println("Error with reading from table " + e + "\n");
		}
	}

	// Method to select the animal which consumed the highest amount of food
	static void readMaxWithMetaData() {
		System.out.println("\n2. Reading Max Consumed by Animal");
		// String read = "select animalName,MAX(amountFed) from FeedingData";
		String read = "select MAX(amountFed) as max from FeedingData";
		try {
			resultSet = statement.executeQuery(read);
			// resultSetMetaData will return object of metadata type
			// which contains detailed data about the tables metadata
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

			int noOfCol = resultSetMetaData.getColumnCount();
			for (int i = 1; i <= noOfCol; i++) {
				System.out.print(resultSetMetaData.getColumnName(i) + ": ");
			}
			while (resultSet.next()) {
				for (int i = 1; i <= noOfCol; i++) {
					System.out.print(resultSet.getObject(i) + "\n");
				}
			}
		} catch (SQLException e) {
			System.out.println("Error with reading with metadata " + e);
		}
	}

	// Method to read total amount of food consumed by all animals
	static void readTotalWithMetaData() {
		System.out.println("\n3. Total Consumed ");
		String read = "select sum(amountFed) as sum from FeedingData";
		try {
			resultSet = statement.executeQuery(read);
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData(); // will return object of metadata type
			// contains detailed data about the tables
			// metadata
			int noOfCol = resultSetMetaData.getColumnCount();
			for (int i = 1; i <= noOfCol; i++) {
				System.out.print(resultSetMetaData.getColumnName(i) + ": ");
			}
			while (resultSet.next()) {
				for (int i = 1; i <= noOfCol; i++) {
					System.out.print(resultSet.getObject(i) + "\n");
				}
			}
		} catch (SQLException e) {
			System.out.println("Error with reading total consumed " + e + "\n");
		}

	}

	// Class created to use Runnable interface
	public static class DepositTask implements Runnable {

		public void run() {
			try {
				while (true) {
					// Randomly select a value to deposit
					foodStock.deposit((int) (Math.random() * 10) + 1);
					// Sleep
					Thread.sleep(1000);
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	// Class created to use Runnable interface
	public static class WithdrawTask implements Runnable {

		@Override // Keep subtracting an amount from the account
		public void run() {
			int numOfAnimalsFed = 0;
			java.util.Collections.shuffle(animals);
			// Assign animal the fist index of the animals ArrayList
			animal = animals.get(0);
			System.out.println("Initial Animal: " + animal.toString());
			// Iterate N times
			for (int i = 0; i < n; i++) {
				foodStock.feed(animal.getFoodNeeded(), animal);
				numOfAnimalsFed++;
				java.util.Collections.shuffle(animals);
				animal = animals.get(0);
				System.out.println("Next animal: " + animals.get(0).toString());
				System.out.println("\t\t\tFeed " + animal.getName() + " " + animal.getFoodNeeded());
			}
		}
	}
}