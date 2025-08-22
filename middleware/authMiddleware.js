// C:\fluter-proyect\vacaciones\my_first_app\functions\api\middleware\authMiddleware.js
import jwt from 'jsonwebtoken';

const JWT_SECRET = process.env.JWT_SECRET || 'your_jwt_secret_key'; // ¡IMPORTANTE! Debe ser el mismo que en auth.controller.js y seguro en producción.

export const authenticateToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1]; // Espera "Bearer TOKEN"

    if (token == null) {
        console.log("authMiddleware - authenticateToken: No se proporcionó token.");
        return res.status(401).json({ error: "Token de autenticación requerido." });
    }

    jwt.verify(token, JWT_SECRET, (err, user) => {
        if (err) {
            console.error("authMiddleware - authenticateToken: Error de verificación de token:", err.message);
            // Si el token es inválido o expirado, envía un 403
            return res.status(403).json({ error: "Token de autenticación inválido o expirado.", details: err.message });
        }
        // El token es válido, adjunta la información del usuario a la solicitud
        req.user = user;
        console.log(`authMiddleware - authenticateToken: Token verificado para user ID: ${user.id}, Rol: ${user.rol}`);
        next(); // Pasa al siguiente middleware/controlador
    });
};

export const authorizeRole = (roles) => {
    return (req, res, next) => {
        if (!req.user || !req.user.rol) {
            console.log("authMiddleware - authorizeRole: Usuario no autenticado o rol no definido.");
            return res.status(401).json({ error: "No autenticado." });
        }

        if (!roles.includes(req.user.rol)) {
            console.log(`authMiddleware - authorizeRole: Acceso denegado para rol: ${req.user.rol}. Roles requeridos: ${roles.join(', ')}`);
            return res.status(403).json({ error: `Acceso denegado. Se requiere uno de los siguientes roles: ${roles.join(', ')}` });
        }
        console.log(`authMiddleware - authorizeRole: Rol ${req.user.rol} autorizado.`);
        next();
    };
};