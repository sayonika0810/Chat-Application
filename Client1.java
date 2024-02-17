import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client1 implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    @Override
    public void run() {
        try {
            client = new Socket("127.0.0.1",9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandeller inHandeller = new InputHandeller();
            Thread t = new Thread(inHandeller);
            t.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null){
                System.out.println(inMessage);
            }

        } catch (IOException e) {
            // shutdown();
        }

    }

    public void shutdown(){
        done = true;
        try{
            in.close();;
            out.close();
            if(!client.isClosed()){
                client.close();
            }
        }catch (IOException e){
            //ignore
        }
    }
    class InputHandeller implements Runnable{

        @Override
        public void run() {
            BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
            while (!done){
                try{String message = inReader.readLine();
                if (message.equals("/quit")){
                    out.println(message);
                    inReader.close();
                    shutdown();
                }else{
                    out.println(message);
                }
            }catch (IOException e){
                    //TODO: handle
                }
            }
        }

    }
    public static void main(String[] args) {
        Client1 client = new Client1();
        client.run();
    }
}

