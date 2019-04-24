package history;

public class Spaceship extends Location {
    private PeopleMap crew;
    private Capitan capitan;
    private ISquad squad;

    public Spaceship(String locationName, Human capitan, PeopleMap crew){
        super(locationName);
        this.capitan = new Capitan(capitan);
        this.crew = crew;
    }

    public class Capitan extends Human implements ICapitan {
        @Override
        public void respond(){
            System.out.println("Проверим экипаж, капитан "+getName()+" на месте");
        }
        private Capitan(Human h){
            super(h.getName(), h.getProf());
        }

        @Override
        public Squad getSquad(Squad squad){
            for (Human h:squad.getCosmonauts()){
                System.out.println(h.getName());
            }
            return squad;
        }
        @Override
        public void say(String str){
            System.out.println("Капитан "+getName()+" сказал: "+str);
        }
        @Override
        public void toOrder(String order){
            System.out.println("Капитан "+getName()+" приказал: "+order);
        }
    }

    @Override
    public String toString(){
        StringBuilder strCrew = new StringBuilder("Капитан: ");
        strCrew.append(capitan.getName()).append("; Экипаж: ");
        int i = 0;
        for(Human h:this.crew.values()){
            i = i + 1;
            if (h.getProf()!=null){
                strCrew.append(h.getProf()).append(" ").append(h.getName()).append(", ");
            }else{
                try {
                    strCrew.append(h.getName()).append(", ");
                    throw new NoProf(h.getName());
                }catch(NoProf e){
                    e.whatHappened();
                }
            }
            if (i == 4){
                strCrew.append("\n");
                i = 0;
            }
        }
        strCrew.deleteCharAt(strCrew.length()-1);
        return strCrew.toString();
    }

    public void setSquad(Squad squad){
        capitan.toOrder("Разведовательному отряду приготовиться, а именно:");
        this.squad = capitan.getSquad(squad);
    }


    public void landing(){
        System.out.println("Корабль приземлился");
    }

    public void takeoff(){
        disbandSquad();
        System.out.println("Корабль взлетел");
    }

    public void disbandSquad(){
        squad = null;
    }

    private void preparing(){
        capitan.toOrder("Надеть скафандры");
        squad.putOnSuits();
    }

    public void squadExplore(Rocket rocket){
        preparing();
        try {
            capitan.say("Ваша цель исследовать рокету " + rocket.getName());
            squad.explore(rocket);
            squad.report();
        } catch (AllDied e){
            e.printStackTrace();
            System.err.println("Исследование не удалось(");
        }
    }

}
