// C:\fluter-proyect\vacaciones\my_first_app\functions\api\index.js
import { https } from 'firebase-functions'; // Esto importa 'https' de Firebase
import app from './app.js';  
import cors from 'cors';               // Esto importa tu aplicación Express desde app.js

// Exporta la aplicación Express como la única función HTTP de Firebase
export const api = https.onRequest(app);