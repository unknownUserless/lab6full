package history;

interface ICapitan extends IHuman{
    Squad getSquad(Squad squad);
    void toOrder(String order);
}
