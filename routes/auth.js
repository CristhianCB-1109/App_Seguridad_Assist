import express from 'express';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import db from '../utils/db.js';
import authMiddleware from '../middleware/jwt.js';
import 'dotenv/config';

const router = express.Router();
const JWT_SECRET = process.env.JWT_SECRET;
const REGISTRATION_KEY = process.env.REGISTRATION_KEY;

//  RUTA PARA REGISTRAR UN NUEVO USUARIO (CON HASHEO DE CLAVE DE ACCESO)
router.post('/register', async (req, res) => {
  console.log('Petición de registro recibida.');
  const { email, contrasena, rol, nombre, codigo_estudiante, carrera, dni, telefono, foto, clave_acceso, registrationKey } = req.body;

  if (rol === 'seguridad' && registrationKey !== REGISTRATION_KEY) {
    console.log('Intento de registro fallido: clave de acceso incorrecta para el rol de seguridad.');
    return res.status(401).json({ success: false, message: 'Clave de acceso incorrecta.' });
  }

  const telefonoFinal = telefono || null;
  const fotoFinal = foto || null;
  const nombreFinal = nombre || null;
  const codigoEstudianteFinal = codigo_estudiante || null;
  const carreraFinal = carrera || null;
  const dniFinal = dni || null;

  console.log('Datos de la petición:', { email, rol, nombre: nombreFinal, codigo_estudiante: codigoEstudianteFinal, carrera: carreraFinal, dni: dniFinal, telefono: telefonoFinal, foto: fotoFinal, clave_acceso: clave_acceso });
  try {
    const [rows] = await db.execute('SELECT * FROM usuarios WHERE email = ?', [email]);
    if (rows.length > 0) {
      console.log('Intento de registro fallido: el usuario ya existe.');
      return res.status(400).json({ success: false, message: 'El usuario ya existe.' });
    }
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(contrasena, salt);
    console.log('Contraseña encriptada correctamente.');

    let hashedClaveAcceso = null;
    if (clave_acceso) {
      hashedClaveAcceso = await bcrypt.hash(clave_acceso, salt);
      console.log('Clave de acceso encriptada correctamente.');
    }

    const query = 'INSERT INTO usuarios (email, contrasena, rol, nombre, codigo_estudiante, carrera, dni, telefono, foto, clave_acceso) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)';
    await db.execute(query, [email, hashedPassword, rol, nombreFinal, codigoEstudianteFinal, carreraFinal, dniFinal, telefonoFinal, fotoFinal, hashedClaveAcceso]);

    console.log('Usuario registrado en la base de datos:', email);
    res.status(201).json({ success: true, message: 'Usuario registrado exitosamente.' });
    console.log('Petición de registro completada con éxito.');
  } catch (e) {
    console.error('Error en el registro:', e.message);
    res.status(500).json({ success: false, message: 'Error del servidor: ' + e.message });
  }
});

/*

### **Ruta para el Login (CORREGIDA, solo clave de acceso)**/

router.post('/login', async (req, res) => {
  console.log('Petición de login recibida.');
  const { email, contrasena, clave_acceso } = req.body;
  console.log('Datos de login recibidos:', { email, contrasena, clave_acceso });

  try {
    let user;
    let rows;
    let isMatch = false;

    // Opción 1: Login simplificado con clave de acceso
    if (clave_acceso && clave_acceso.length > 0) {
      // Paso 1: Obtener todos los usuarios con una clave de acceso
      // Luego, iterar sobre ellos para encontrar una coincidencia
      const [allUsers] = await db.execute('SELECT * FROM usuarios WHERE clave_acceso IS NOT NULL');

      for (const potentialUser of allUsers) {
        const isClaveAccesoMatch = await bcrypt.compare(clave_acceso, potentialUser.clave_acceso);
        if (isClaveAccesoMatch) {
          user = potentialUser;
          isMatch = true;
          break; // Salir del bucle una vez que se encuentra una coincidencia
        }
      }

      if (!isMatch) {
        console.log('Intento de login fallido: clave de acceso incorrecta o no encontrada.');
        return res.status(400).json({ success: false, message: 'Credenciales inválidas.' });
      }

    }
    // Opción 2: Login tradicional con email y contraseña
    else {
      if (!email || !contrasena) {
        console.error('Error: Faltan datos obligatorios para el login tradicional.');
        return res.status(400).json({ success: false, message: 'Faltan campos obligatorios: correo y contraseña.' });
      }
      [rows] = await db.execute('SELECT * FROM usuarios WHERE email = ?', [email]);

      if (rows.length === 0) {
        console.log('Intento de login fallido: el usuario no existe.');
        return res.status(400).json({ success: false, message: 'Credenciales inválidas.' });
      }
      isMatch = await bcrypt.compare(contrasena, rows[0].contrasena);
      if (!isMatch) {
        console.log('Intento de login fallido: contraseña incorrecta.');
        return res.status(400).json({ success: false, message: 'Credenciales inválidas.' });
      }
      user = rows[0];
    }

    console.log('Usuario encontrado en la base de datos:', user.email);

    const payload = {
      user: {
        id: user.id_usuario,
        rol: user.rol
      },
    };
    jwt.sign(payload, JWT_SECRET, { expiresIn: '1h' }, (err, token) => {
      if (err) {
        console.error('Error al generar token:', err.message);
        return res.status(500).json({ success: false, message: 'Error al generar token.' });
      }
      res.json({
        success: true,
        token,
        message: 'Inicio de sesión exitoso.',
        user: {
          id: user.id_usuario,
          email: user.email,
          rol: user.rol,
          nombre: user.nombre,
          codigo_estudiante: user.codigo_estudiante,
          carrera: user.carrera,
          dni: user.dni,
          telefono: user.telefono
        }
      });
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