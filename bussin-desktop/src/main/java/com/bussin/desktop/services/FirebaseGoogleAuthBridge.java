package com.bussin.desktop.services;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public final class FirebaseGoogleAuthBridge {

    private static final int PORT = 8765;

    private static final String FIREBASE_CONFIG = """
            {
                "apiKey": "AIzaSyAHq7KEgGZnN6MPImmniDN2MuXgFiaDdSQ",
                "authDomain": "bussinrtdb.firebaseapp.com",
                "projectId": "bussinrtdb",
                "storageBucket": "bussinrtdb.firebasestorage.app",
                "messagingSenderId": "254375491854",
                "appId": "1:254375491854:web:f173fccdb3722de394bf5f"
            }
            """;

    private FirebaseGoogleAuthBridge() {
    }

    public static String authenticate() throws Exception {

        String state = UUID.randomUUID().toString();

        CompletableFuture<String> tokenFuture = new CompletableFuture<>();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(
                        "127.0.0.1",
                        PORT),
                0);

        server.createContext(
                "/",
                exchange -> handlePage(
                        exchange,
                        state));

        server.createContext(
                "/callback",
                exchange -> handleCallback(
                        exchange,
                        state,
                        tokenFuture));

        server.start();

        try {

            Desktop.getDesktop().browse(
                    URI.create(
                            "http://127.0.0.1:"
                                    + PORT
                                    + "/?state="
                                    + state));

            return tokenFuture.get(
                    5,
                    TimeUnit.MINUTES);

        } finally {

            server.stop(0);
        }
    }

    private static void handlePage(
            HttpExchange exchange,
            String state)
            throws IOException {

        String query = exchange.getRequestURI().getQuery();

        if (query == null
                || !query.contains(
                        "state="
                                + state)) {

            sendResponse(
                    exchange,
                    403,
                    "Invalid authentication state.");

            return;
        }

        try {

            String html = createAuthenticationPage(state);

            byte[] bytes = html.getBytes(
                    StandardCharsets.UTF_8);

            exchange.getResponseHeaders()
                    .set(
                            "Content-Type",
                            "text/html; charset=UTF-8");

            exchange.sendResponseHeaders(
                    200,
                    bytes.length);

            try (var output = exchange.getResponseBody()) {

                output.write(bytes);
            }

        } catch (Exception exception) {

            exception.printStackTrace();

            sendResponse(
                    exchange,
                    500,
                    """
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <meta charset="UTF-8">
                                <title>BUSSIN Authentication Error</title>
                            </head>
                            <body>
                                <h2>BUSSIN Authentication Error</h2>
                                <p>The authentication page could not be loaded.</p>
                            </body>
                            </html>
                            """);
        }
    }

    private static void handleCallback(
            HttpExchange exchange,
            String state,
            CompletableFuture<String> tokenFuture)
            throws IOException {

        String query = exchange.getRequestURI().getQuery();

        if (query == null
                || !query.contains(
                        "state="
                                + state)) {

            sendResponse(
                    exchange,
                    403,
                    "Invalid authentication state.");

            return;
        }

        if (!"POST".equalsIgnoreCase(
                exchange.getRequestMethod())) {

            sendResponse(
                    exchange,
                    405,
                    "Method Not Allowed.");

            return;
        }

        String body = new String(
                exchange.getRequestBody()
                        .readAllBytes(),
                StandardCharsets.UTF_8);

        if (body.isBlank()) {

            sendResponse(
                    exchange,
                    400,
                    "Authentication token was not received.");

            return;
        }

        String token = extractToken(body);

        if (token == null
                || token.isBlank()) {

            sendResponse(
                    exchange,
                    400,
                    "Authentication token was invalid.");

            return;
        }

        tokenFuture.complete(token);

        sendResponse(
                exchange,
                200,
                """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <title>BUSSIN Authentication</title>
                        </head>
                        <body>
                            <h2>Google authentication successful.</h2>
                            <p>You may close this browser window and return to BUSSIN.</p>
                        </body>
                        </html>
                        """);
    }

    private static String extractToken(
            String body) {

        String prefix = "{\"idToken\":\"";

        if (!body.startsWith(prefix)
                || !body.endsWith("\"}")) {

            return null;
        }

        return body.substring(
                prefix.length(),
                body.length() - 2);
    }

    private static void sendResponse(
            HttpExchange exchange,
            int status,
            String message)
            throws IOException {

        byte[] bytes = message.getBytes(
                StandardCharsets.UTF_8);

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "text/html; charset=UTF-8");

        exchange.sendResponseHeaders(
                status,
                bytes.length);

        try (var output = exchange.getResponseBody()) {

            output.write(bytes);
        }
    }

    private static String createAuthenticationPage(
            String state) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>BUSSIN Google Sign-In</title>

                    <style>
                        body {
                            margin: 0;
                            min-height: 100vh;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-family: Arial, sans-serif;
                            background: #F3F4F6;
                        }

                        .card {
                            width: 360px;
                            padding: 32px;
                            background: white;
                            border: 1px solid #D1D5DB;
                            border-radius: 16px;
                            text-align: center;
                            box-sizing: border-box;
                        }

                        h1 {
                            margin: 0 0 10px;
                            color: #121212;
                        }

                        p {
                            color: #6B7280;
                            line-height: 1.5;
                        }

                        button {
                            width: 100%%;
                            padding: 13px;
                            margin-top: 18px;
                            border: 1px solid #D1D5DB;
                            border-radius: 8px;
                            background: white;
                            cursor: pointer;
                            font-size: 14px;
                            font-weight: bold;
                        }

                        button:hover {
                            background: #F9FAFB;
                        }

                        button:disabled {
                            cursor: wait;
                            opacity: 0.6;
                        }

                        #status {
                            margin-top: 18px;
                            font-size: 13px;
                            color: #6B7280;
                            word-break: break-word;
                        }
                    </style>
                </head>

                <body>

                    <div class="card">

                        <h1>BUSSIN</h1>

                        <p>
                            Sign in securely using your Google account.
                        </p>

                        <button id="googleButton">
                            Continue with Google
                        </button>

                        <div id="status"></div>

                    </div>

                    <script type="module">

                        import {
                            initializeApp
                        } from "https://www.gstatic.com/firebasejs/12.19.0/firebase-app.js";

                        import {
                            getAuth,
                            signInWithPopup,
                            GoogleAuthProvider
                        } from "https://www.gstatic.com/firebasejs/12.19.0/firebase-auth.js";

                        const firebaseConfig =
                            %s;

                        const app =
                            initializeApp(firebaseConfig);

                        const auth =
                            getAuth(app);

                        const provider =
                            new GoogleAuthProvider();

                        const button =
                            document.getElementById(
                                "googleButton"
                            );

                        const status =
                            document.getElementById(
                                "status"
                            );

                        button.addEventListener(
                            "click",
                            async () => {

                                button.disabled = true;

                                status.textContent =
                                    "Opening Google Sign-In...";

                                try {

                                    const result =
                                        await signInWithPopup(
                                            auth,
                                            provider
                                        );

                                    const idToken =
                                        await result.user.getIdToken();

                                    status.textContent =
                                        "Google authentication successful. Connecting to BUSSIN...";

                                    const response =
                                        await fetch(
                                            "/callback?state=%s",
                                            {
                                                method: "POST",
                                                headers: {
                                                    "Content-Type":
                                                        "application/json"
                                                },
                                                body:
                                                    JSON.stringify({
                                                        idToken:
                                                            idToken
                                                    })
                                            }
                                        );

                                    if (!response.ok) {

                                        throw new Error(
                                            "BUSSIN authentication callback failed. HTTP "
                                            + response.status
                                        );
                                    }

                                    status.textContent =
                                        "Authentication successful. You may close this window.";

                                } catch (error) {

                                    console.error(
                                        "Firebase Google Sign-In error:",
                                        error
                                    );

                                    button.disabled = false;

                                    const code =
                                        error &&
                                        error.code
                                            ? error.code
                                            : "unknown";

                                    const message =
                                        error &&
                                        error.message
                                            ? error.message
                                            : String(error);

                                    status.textContent =
                                        "Unable to sign in with Google: "
                                        + code
                                        + " — "
                                        + message;
                                }
                            }
                        );

                    </script>

                </body>
                </html>
                """.formatted(
                FIREBASE_CONFIG,
                state);
    }
}