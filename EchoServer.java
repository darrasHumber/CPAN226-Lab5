import java.net.*;
import java.util.Scanner;

/**
 * CPAN226 - Lab 5
 * Author: Mohammed Darras
 *
 * Part 2: UDP Echo Server
 *   - Binds to port 5000
 *   - Receives a UDP datagram from the client (ncat)
 *   - Prints the received message
 *   - Echoes the same packet back to the sender
 *
 * Part 3: Quote Proxy Server (extends Part 2)
 *   - Same setup as Part 2
 *   - Instead of echoing, fetches a random technology quote
 *     from api.quotable.io via HTTP
 *   - Sends the JSON quote back to the client over UDP
 */
public class EchoServer {

    public static void main(String[] args) throws Exception {

        // ── PART 2: Setup ──────────────────────────────────────────
        // Create a DatagramSocket bound to port 5000
        DatagramSocket socket = new DatagramSocket(5000);
        byte[] buffer = new byte[4096];

        System.out.println("Mohammed's Server is listening on port 5000...");

        while (true) {

            // ── PART 2: Receive ────────────────────────────────────
            // Create an empty packet to hold incoming data
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // TODO (Part 2) — filled in: block until a UDP packet arrives
            socket.receive(packet);

            // Print what we received
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Mohammed's Server Received: " + received.trim());

            // ── PART 3: Fetch a quote from the web ────────────────
            String responseText;
            try {
                // Open HTTP stream to the public quotes API
                URL url = new URL("http://api.quotable.io/random?tags=technology");
                Scanner s = new Scanner(url.openStream());
                // Read entire response in one go
                responseText = s.useDelimiter("\\A").next();
                s.close();
                System.out.println("Fetched quote: " + responseText);
            } catch (Exception e) {
                // Fallback if the API is unreachable
                responseText = "\"Any sufficiently advanced technology is indistinguishable from magic.\" - Arthur C. Clarke";
                System.out.println("API unreachable — sending fallback quote.");
            }

            // ── PART 3: Send quote back to the client ─────────────
            byte[] responseBytes = responseText.getBytes();

            // Build a new packet addressed to whoever sent us the original message
            DatagramPacket response = new DatagramPacket(
                    responseBytes,          // payload
                    responseBytes.length,   // payload length
                    packet.getAddress(),    // client IP  (from incoming packet)
                    packet.getPort()        // client port (from incoming packet)
            );

            // TODO (Part 2/3) — filled in: send the response packet
            socket.send(response);

            System.out.println("Response sent to "
                    + packet.getAddress() + ":" + packet.getPort());
            System.out.println("--------------------------------------------------");
        }
    }
}