import express from 'express';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import db from '../utils/db.js';
import authMiddleware from '../middleware/auth.js';
import 'dotenv/config';

const router = express.Router();
const JWT_SECRET = process.env.JWT_SECRET;

// Ruta para registrar un nuevo usuario
router.post('/register', async (req, res) => {
  const { correo, contrasena, rol, telefono, foto } = req.body;

  // Aseguramos que telefono y foto sean null si no están presentes
  const telefonoFinal = telefono || null;
  const fotoFinal = foto || null;

  try {
    // 1. Verificar si el usuario ya existe
    const [rows] = await db.execute('SELECT * FROM usuarios WHERE correo = ?', [correo]);
    if (rows.length > 0) {
      return res.status(400).json({ success: false, message: 'El usuario ya existe.' });
    }

    // 2. Encriptar la contraseña
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(contrasena, salt);

    // 3. Guardar el nuevo usuario en la DB
    const query = 'INSERT INTO usuarios (correo, contrasena, rol, telefono, foto) VALUES (?, ?, ?, ?, ?)';
    await db.execute(query, [correo, hashedPassword, rol, telefonoFinal, fotoFinal]);

    res.status(201).json({ success: true, message: 'Usuario registrado exitosamente.' });
  } catch (e) {
    console.error('Error en el registro:', e); // Log en el servidor
    res.status(500).json({ success: false, message: 'Error del servidor: ' + e.message });
  }
});

// Ruta para el login de usuario
router.post('/login', async (req, res) => {
  const { correo, contrasena } = req.body;
  try {
    // 1. Verificar si el usuario existe
    const [rows] = await db.execute('SELECT * FROM usuarios WHERE correo = ?', [correo]);
    if (rows.length === 0) {
      return res.status(400).json({ success: false, message: 'Credenciales inválidas.' });
    }

    const user = rows[0];

    // 2. Comparar la contraseña ingresada con la encriptada
    const isMatch = await bcrypt.compare(contrasena, user.contrasena);
    if (!isMatch) {
      return res.status(400).json({ success: false, message: 'Credenciales inválidas.' });
    }

    // 3. Generar un token JWT
    const payload = {
      user: {
        id: user.id_usuario,
        rol: user.rol
      },
    };
    jwt.sign(payload, JWT_SECRET, { expiresIn: '1h' }, (err, token) => {
      if (err) {
        console.error('Error al generar token:', err);
        return res.status(500).json({ success: false, message: 'Error al generar token.' });
      }
      res.json({ success: true, token, message: 'Inicio de sesión exitoso.' });
    });
  } catch (e) {
    console.error('Error en el login:', e);
    res.status(500).json({ success: false, message: 'Error del servidor: ' + e.message });
  }
});

// Ruta de ejemplo para probar la autenticación
router.get('/protected', authMiddleware, (req, res) => {
  res.json({ success: true, msg: 'Bienvenido! Has accedido a una ruta protegida.', user: req.user });
});

export default router;