package client;

import java.io.Closeable;
import java.util.Scanner;

public class Console implements Closeable {
    private Scanner scanner;

    public Console() {
        scanner = new Scanner(System.in);
    }

    public String readCommand() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        } else {
            System.err.println("Что-то пошло не так, перезапустите клиент, используя свой id\n" +
                    "Ваше соединение будет восстановлено");
            System.exit(2);
            return "";
        }
    }

    public int readId() {
        System.out.println("Введите число от 1 до 500\n" +
                "Чтобы подробнее узнать об этом введите команду id");
        if (scanner.hasNext()) {
            String str = scanner.nextLine();
            int id = -1;
            try {
                 id = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                if (str.equals("id")){
                    System.out.println("Ваш id используется для авторизации вас на сервере");
                } else if (str.equals("exit")){
                    System.exit(0);
                } else {
                    System.err.println(str + " не является числом или командой id");
                }
                return readId();
            }
            if ( (1 <= id) && (id <= 500)){
                return id;
            } else {
                System.out.println("Введен неверный id, ваше число " + id + " не попадает в диапозон [1; 500]");
                return readId();
            }

        } else {
            System.err.println("Перезапустите клиент и попробуйте еще раз");
            System.exit(2);
            return -1;
        }
    }

    public int readMainPort(){
        System.out.println("Необходимо указать главный порт сервера");
        try{
            String str = scanner.nextLine();
            return Integer.parseInt(str);
        } catch (Exception e){
            System.out.println("Неверное значение");
            System.exit(2);
            return readMainPort();
        }
    }

    @Override
    public void close() {
        this.scanner.close();
    }
}
