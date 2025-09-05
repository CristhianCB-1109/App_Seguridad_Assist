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
  console.log('Petición de registro recibida.');
  const { email, contrasena, rol, telefono, foto } = req.body;

  // Aseguramos que telefono y foto sean null si no están presentes
  const telefonoFinal = telefono || null;
  const fotoFinal = foto || null;

  console.log('Datos de la petición:', { email, rol, telefono: telefonoFinal, foto: fotoFinal });

  try {
    // 1. Verificar si el usuario ya existe
    const [rows] = await db.execute('SELECT * FROM usuarios WHERE email = ?', [email]);
    if (rows.length > 0) {
      console.log('Intento de registro fallido: el usuario ya existe.');
      return res.status(400).json({ success: false, message: 'El usuario ya existe.' });
    }

    // 2. Encriptar la contraseña
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(contrasena, salt);
    console.log('Contraseña encriptada correctamente.');

    // 3. Guardar el nuevo usuario en la DB
    const query = 'INSERT INTO usuarios (email, contrasena, rol, telefono, foto) VALUES (?, ?, ?, ?, ?)';
    await db.execute(query, [email, hashedPassword, rol, telefonoFinal, fotoFinal]);
    console.log('Usuario registrado en la base de datos:', email);

    res.status(201).json({ success: true, message: 'Usuario registrado exitosamente.' });
    console.log('Petición de registro completada con éxito.');
  } catch (e) {
    console.error('Error en el registro:', e.message);
    res.status(500).json({ success: false, message: 'Error del servidor: ' + e.message });
  }
});

// Ruta para el login de usuario
router.post('/login', async (req, res) => {
  console.log('Petición de login recibida.');
  const { email, contrasena } = req.body;
  console.log('Datos de login recibidos:', { email, contrasena });

  // 1. **Validación de datos de entrada**
  if (!email || !contrasena) {
    console.error('Error: Faltan datos obligatorios para el login.');
    return res.status(400).json({ success: false, message: 'Faltan campos obligatorios: correo y contraseña.' });
  }

  try {
    // 2. Verificar si el usuario existe
    const [rows] = await db.execute('SELECT * FROM usuarios WHERE email = ?', [email]);
    if (rows.length === 0) {
      console.log('Intento de login fallido: el usuario no existe.');
      return res.status(400).json({ success: false, message: 'Credenciales inválidas.' });
    }

    const user = rows[0];
    console.log('Usuario encontrado en la base de datos:', user.email);

    // 3. Comparar la contraseña ingresada con la encriptada
    const isMatch = await bcrypt.compare(contrasena, user.contrasena);
    if (!isMatch) {
      console.log('Intento de login fallido: contraseña incorrecta.');
      return res.status(400).json({ success: false, message: 'Credenciales inválidas.' });
    }
    console.log('Contraseña verificada correctamente.');

    // 4. Generar un token JWT
    const payload = {
      user: {
        id: user.id_usuario,
        rol: user.rol,
        nombre: user.nombre,
        dni: user.dni,
        codigo_estudiante: user.codigo_estudiante,
        carrera: user.carrera,
        telefono: user.telefono
      },
    };
    jwt.sign(payload, JWT_SECRET, { expiresIn: '1h' }, (err, token) => {
      if (err) {
        console.error('Error al generar token:', err.message);
        return res.status(500).json({ success: false, message: 'Error al generar token.' });
      }
      res.json({ success: true, token, message: 'Inicio de sesión exitoso.', rol: user.rol });
      console.log('Token generado y login completado con éxito.');
    });
  } catch (e) {
    console.error('Error en el login:', e.message);
    res.status(500).json({ success: false, message: 'Error del servidor: ' + e.message });
  }
});

// Ruta de ejemplo para probar la autenticación
router.get('/protected', authMiddleware, (req, res) => {
  console.log('Acceso a ruta protegida concedido para:', req.user.email);
  res.json({ success: true, msg: 'Bienvenido! Has accedido a una ruta protegida.', user: req.user });
});

export default router;
