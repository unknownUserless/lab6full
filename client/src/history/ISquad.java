package history;

public interface ISquad {
    void explore(Rocket r) throws AllDied;
    void report();
    void putOnSuits();
}