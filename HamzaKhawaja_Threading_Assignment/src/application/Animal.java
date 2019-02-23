package application;

public class Animal {

    private String name;
    private int foodNeeded;
    private int foodEaten;

    public Animal(String name, int foodNeeded) {
        this.name = name;
        this.foodNeeded = foodNeeded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFoodNeeded() {
        return foodNeeded;
    }

    public void setFoodNeeded(int foodNeeded) {
        // Keep on adding total food eaten by animal
        this.foodNeeded = foodNeeded;
    }

    public int getFoodEaten() {
        return foodEaten;
    }

    public void setFoodEaten(int foodEaten) {
        this.foodEaten += foodEaten;
    }

    @Override
    public String toString() {
        return "Animal [name=" + name + ", foodNeeded=" + foodNeeded + "]";
    }

}
