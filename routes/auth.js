import express from 'express';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import db from '../utils/db.js';
import authMiddleware from '../middleware/auth.js';
import 'dotenv/config';

const router = express.Router();
const JWT_SECRET = process.env.JWT_SECRET;

// Ruta para registrar un nuevo usuario
// ...
router.post('/register', async (req, res) => {
  const { correo, contrasena, rol, telefono, foto } = req.body;

  // Aseguramos que telefono y foto sean null si no están presentes
  const telefonoFinal = telefono || null;
  const fotoFinal = foto || null;

  try {
    // ...
    const query = 'INSERT INTO usuarios (correo, contrasena, rol, telefono, foto) VALUES (?, ?, ?, ?, ?)';
    await db.execute(query, [correo, hashedPassword, rol, telefonoFinal, fotoFinal]); // <-- ¡Ahora enviamos valores válidos!
    // ...
  } catch (e) {
    console.error(e.message);
    res.status(500).send('Error del servidor');
  }
});

// Ruta para el login de usuario
router.post('/login', async (req, res) => {
  const { correo, contrasena } = req.body;
  try {
    // 1. Verificar si el usuario existe
    const [rows] = await db.execute('SELECT * FROM usuarios WHERE correo = ?', [correo]);
    if (rows.length === 0) {
      return res.status(400).json({ msg: 'Credenciales inválidas' });
    }

    const user = rows[0];

    // 2. Comparar la contraseña ingresada con la encriptada
    const isMatch = await bcrypt.compare(contrasena, user.contrasena);
    if (!isMatch) {
      return res.status(400).json({ msg: 'Credenciales inválidas' });
    }

    // 3. Generar un token JWT
    const payload = {
      user: {
        id: user.id_usuario,
        rol: user.rol
      },
    };
    jwt.sign(payload, JWT_SECRET, { expiresIn: '1h' }, (err, token) => {
      if (err) throw err;
      res.json({ token });
    });
  } catch (e) {
    console.error(e.message);
    res.status(500).send('Error del servidor');
  }
});

// Ruta de ejemplo para probar la autenticación
router.get('/protected', authMiddleware, (req, res) => {
  res.json({ msg: 'Bienvenido! Has accedido a una ruta protegida.', user: req.user });
});

export default router;