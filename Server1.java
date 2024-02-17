import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server1 implements Runnable {

    private ArrayList<ConnetionHandeler> conections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;

    public Server1() {
        conections = new ArrayList<>();
        done = false;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool();
            while (!done) {
                Socket client = server.accept();
                ConnetionHandeler handeler = new ConnetionHandeler(client);
                conections.add(handeler);
                pool.execute(handeler);
            }

        } catch (IOException e) {
            // TODO : Handle
        }
    }

    public void broadcast(String message) {
        for (ConnetionHandeler ch : conections) {
            if (ch != null) {
                ch.sendMessage(message);
            }
        }
    }

    public void shutdown() {
        done = true;
        pool.shutdown();
        if (!server.isClosed()) {
            try {
                server.close();
                for (ConnetionHandeler ch : conections) {
                    ch.shutdown();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                shutdown();
            }
        }
    }
    public double evaluate(String exp){
       double result = 0;
        String[] inputParts = exp.split(" ");
        if (inputParts.length == 4) {
            String operator = inputParts[2];
            double number1 = Double.parseDouble(inputParts[1]);
            double number2 = Double.parseDouble(inputParts[3]);
            switch (operator) {
                case "+":
                    result = number1 + number2;
                    break;
                case "-":
                    result = number1 - number2;
                    break;
                case "*":
                    result = number1 * number2;
                    break;
                case "/":
		try {
         		if(number2==0) {
            			throw new ArithmeticException();
        		 }
			else{
				result = number1 / number2;
			}
      		} catch (ArithmeticException e) {
         		System.out.println("ArithmeticException caught.");
		}
                case "//":
		try{
                    result = Math.floor(number1 / number2);
		}
		catch(Exception e){
			System.out.println("Zero Division Error")
		}
                    break;
                case "%":
                    result = number1 % number2;
                    break;
                case "^":
                    result = Math.pow(number1, number2);
                    break;
                default:
                    System.out.println("Unsupported operation.");
                    break;
            }
        return result;
        } else if (inputParts.length == 3) {
            String operator = inputParts[1];
            double number = Double.parseDouble(inputParts[2]);
            switch (operator) {
                case "sin":
                    result = Math.sin(number);
                    break;
                case "cos":
                    result = Math.cos(number);
                    break;
                case "tan":
                    result = Math.tan(number);
                    break;
                case "log":
                    result = Math.log(number);
                    break;
                case "ln":
                    result = Math.log10(number);
                    break;
                case "sqrt":
                    result = Math.sqrt(number);
                    break;
		case "e":
			result=Math.exp(number);
			break;
                default:
                    System.out.println("Unsupported operation.");
                    break;
            }
        return result;
        } 

        return -1;
    }

    class ConnetionHandeler implements Runnable {

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String username;

        public ConnetionHandeler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Please enter your username: ");
                username = in.readLine();
                System.out.println(username + " Conected. ");
                broadcast(username + " joined the chat.");
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/quit ")) {
                        String message2 = username + " left the chat";
                        broadcast(message2);
                        shutdown();
                    }
                    else if (message.startsWith("/solve ")){
                        double result = evaluate(message);
                        String sendResult = " " + result + " ";
                        broadcast("Answer for the question " + message.substring(6) + " by " +username + " is " + sendResult);
                    } 
                    else {
                        broadcast(username + " : " + message);
                    }
                }
            } catch (IOException e) {
                // TODO: handle exception
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void shutdown() {

            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }
    public static void main(String[] args) {
        Server1 server = new Server1();
        server.run();
    }
}
