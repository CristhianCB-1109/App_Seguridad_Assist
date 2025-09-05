import express from 'express';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import db from '../utils/db.js';
import authMiddleware from '../middleware/jwt.js';
import 'dotenv/config';

const router = express.Router();
const JWT_SECRET = process.env.JWT_SECRET;

//  RUTA PARA REGISTRAR UN NUEVO USUARIO
router.post('/register', async (req, res) => {
    console.log('Petición de registro recibida.');
    const { email, contrasena, rol, nombre, codigo_estudiante, carrera, dni, telefono, foto, clave_acceso } = req.body;
    
    const telefonoFinal = telefono || null;
    const fotoFinal = foto || null;
    const nombreFinal = nombre || null;
    const codigoEstudianteFinal = codigo_estudiante || null;
    const carreraFinal = carrera || null;
    const dniFinal = dni || null;

    console.log('Datos de la petición:', { email, rol, nombre: nombreFinal, codigo_estudiante: codigoEstudianteFinal, carrera: carreraFinal, dni: dniFinal, telefono: telefonoFinal, foto: fotoFinal, clave_acceso: clave_acceso });
    
    try {
        // Paso 1: Verificar si el usuario ya existe
        const [rows] = await db.execute('SELECT * FROM usuarios WHERE email = ?', [email]);
        if (rows.length > 0) {
            console.log('Intento de registro fallido: el usuario ya existe.');
            return res.status(400).json({ success: false, message: 'El usuario ya existe.' });
        }

        // Paso 2: Encriptar la contraseña
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(contrasena, salt);
        console.log('Contraseña encriptada correctamente.');

        // Paso 3: Hashear la clave de acceso si el rol es 'seguridad'
        let hashedClaveAcceso = null;
        if (rol === 'seguridad' && clave_acceso) {
            hashedClaveAcceso = await bcrypt.hash(clave_acceso, salt);
            console.log('Clave de acceso para seguridad encriptada correctamente.');
        }

        // Paso 4: Insertar el nuevo usuario en la base de datos de usuarios
        const query = 'INSERT INTO usuarios (email, contrasena, rol, nombre, codigo_estudiante, carrera, dni, telefono, foto, clave_acceso) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)';
        await db.execute(query, [email, hashedPassword, rol, nombreFinal, codigoEstudianteFinal, carreraFinal, dniFinal, telefonoFinal, fotoFinal, hashedClaveAcceso]);
        
        // 🚨 CAMBIO IMPORTANTE: Si es de seguridad, insertar la clave de acceso en la tabla de activación
        if (rol === 'seguridad' && clave_acceso) {
            await db.execute('INSERT INTO activacion_seguridad (email, clave_acceso) VALUES (?, ?)', [email, clave_acceso]);
            console.log('Registro de clave de acceso creado en la tabla de activación.');
        }

        console.log('Usuario registrado en la base de datos:', email);
        res.status(201).json({ success: true, message: 'Usuario registrado exitosamente.' });
        console.log('Petición de registro completada con éxito.');
    } catch (e) {
        console.error('Error en el registro:', e.message);
        res.status(500).json({ success: false, message: 'Error del servidor: ' + e.message });
    }
});

// 🔑 RUTA PARA EL LOGIN DE USUARIO (CON DOBLE AUTENTICACIÓN)
router.post('/login', async (req, res) => {
    console.log('Petición de login recibida.');
    const { email, contrasena, clave_acceso } = req.body;
    console.log('Datos de login recibidos:', { email, contrasena, clave_acceso });

    try {
        let user;
        
        // Opción 1: Login con clave de acceso (para el rol de 'seguridad')
        if (clave_acceso) {
            // Buscamos al usuario en la tabla de activacion
            const [users] = await db.execute('SELECT u.* FROM usuarios u JOIN activacion_seguridad a ON u.email = a.email WHERE a.clave_acceso = ?', [clave_acceso]);
            
            if (users.length === 0) {
                console.log('Intento de login fallido: clave de acceso incorrecta o no encontrada.');
                return res.status(400).json({ success: false, message: 'Credenciales inválidas.' });
            }
            user = users[0];
            
            // Verificamos si el usuario encontrado es de seguridad, ya que la clave es exclusiva de ellos
            if (user.rol !== 'seguridad') {
                console.log('Intento de login fallido: El usuario no tiene rol de seguridad.');
                return res.status(400).json({ success: false, message: 'Acceso denegado con clave de acceso.' });
            }
        } 
        // Opción 2: Login tradicional con email y contraseña
        else {
            if (!email || !contrasena) {
                console.error('Error: Faltan datos obligatorios para el login tradicional.');
                return res.status(400).json({ success: false, message: 'Faltan campos obligatorios: correo y contraseña.' });
            }
            const [rows] = await db.execute('SELECT * FROM usuarios WHERE email = ?', [email]);
            
            if (rows.length === 0) {
                console.log('Intento de login fallido: el usuario no existe.');
                return res.status(400).json({ success: false, message: 'Credenciales inválidas.' });
            }
            
            const isMatch = await bcrypt.compare(contrasena, rows[0].contrasena);
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

            const userResponse = {
                id: user.id_usuario,
                email: user.email,
                rol: user.rol,
                nombre: user.nombre,
                codigo_estudiante: user.codigo_estudiante,
                carrera: user.carrera,
                dni: user.dni,
                telefono: user.telefono
            };

            res.json({
                success: true,
                token,
                message: 'Inicio de sesión exitoso.',
                user: userResponse
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
