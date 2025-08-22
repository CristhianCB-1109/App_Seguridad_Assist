// C:\fluter-proyect\vacaciones\my_first_app\functions\api\app.js
import express from 'express';
import cors from 'cors';
import mysql from 'mysql2/promise'; // Aunque no se usa directamente aquí, se pasa via req.dbConfig

// Importa tus rutas modularizadas
import authRoutes from './routes/auth.routes.js';
import usersRoutes from './routes/users.routes.js'; // Asegúrate de que este archivo users.routes.js esté actualizado.

const app = express();

app.use(cors({ origin: true }));
app.use(express.json());

// Middleware para pasar la configuración de la base de datos en req.dbConfig
// Esto es correcto y permite que todos los controladores accedan a la configuración de DB
app.use((req, res, next) => {
    req.dbConfig = {
        host: process.env.DB_HOST,
        user: process.env.DB_USER,
        password: process.env.DB_PASSWORD,
        database: process.env.DB_NAME,
        port: 3306,
    };
    next();
});

// Monta las rutas de autenticación bajo el prefijo /auth
app.use('/auth', authRoutes);

// Monta las rutas de usuarios bajo el prefijo /users
// ¡CRÍTICO! Esto asume que users.routes.js tendrá las rutas como /me, /me/addresses, etc.
app.use('/users', usersRoutes);

// Ruta de prueba básica (opcional, puedes mantenerla si quieres un endpoint raíz)
app.get('/', (req, res) => {
    res.status(200).json({ message: 'API de Delivery funcionando!' });
});

export default app; // Exporta la aplicación Express