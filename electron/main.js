import { app, BrowserWindow, net, protocol } from "electron";
import path from "path";
import { fileURLToPath, pathToFileURL } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const APP_SCHEME = "bussin";

protocol.registerSchemesAsPrivileged([
    {
        scheme: APP_SCHEME,
        privileges: {
            standard: true,
            secure: true,
            supportFetchAPI: true,
            corsEnabled: true
        }
    }
]);

const isDevelopment = !app.isPackaged;

function registerAppProtocol() {
    protocol.handle(APP_SCHEME, (request) => {
        const url = new URL(request.url);
        const requestedPath = decodeURIComponent(url.pathname).replace(/^\/+/, "");
        const distRoot = path.resolve(__dirname, "../dist");
        const filePath = path.resolve(distRoot, requestedPath || "index.html");
        const relativePath = path.relative(distRoot, filePath);

        if (
            relativePath.startsWith("..") ||
            path.isAbsolute(relativePath)
        ) {
            return new Response("Not Found", { status: 404 });
        }

        return net.fetch(pathToFileURL(filePath).toString());
    });
}

function createWindow() {
    const window = new BrowserWindow({
        width: 1440,
        height: 900,
        minWidth: 1100,
        minHeight: 700,

        webPreferences: {
            preload: path.join(__dirname, "preload.js"),
            contextIsolation: true,
            nodeIntegration: false,
            sandbox: true
        }
    });

    if (isDevelopment) {
        window.loadURL("http://localhost:5173");
        window.webContents.openDevTools();
    } else {
        window.loadURL(`${APP_SCHEME}://app/index.html`);
    }
}

app.whenReady().then(() => {
    if (!isDevelopment) {
        registerAppProtocol();
    }

    createWindow();

    app.on("activate", () => {
        if (BrowserWindow.getAllWindows().length === 0) {
            createWindow();
        }
    });
});

app.on("window-all-closed", () => {
    if (process.platform !== "darwin") {
        app.quit();
    }
});
