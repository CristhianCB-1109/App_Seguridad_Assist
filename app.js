import express from 'express';
import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import dotenv from 'dotenv';
import { users } from './utils/db.js';
import authenticateToken from './middleware/auth.js';

dotenv.config();

const app = express();
app.use(express.json());

app.post('/register', async (req, res) => {
  const { username, password } = req.body;

  if (!username || !password)
    return res.status(400).json({ error: 'Faltan datos' });

  const userExists = users.find(u => u.username === username);
  if (userExists)
    return res.status(409).json({ error: 'El usuario ya existe' });

  const hashedPassword = await bcrypt.hash(password, 10);
  users.push({ username, password: hashedPassword });

  res.status(201).json({ message: 'Usuario registrado correctamente' });
});

app.post('/login', async (req, res) => {
  const { username, password } = req.body;

  const user = users.find(u => u.username === username);
  if (!user) return res.status(401).json({ error: 'Credenciales inválidas' });

  const valid = await bcrypt.compare(password, user.password);
  if (!valid) return res.status(401).json({ error: 'Credenciales inválidas' });

  const token = jwt.sign({ username: user.username }, process.env.JWT_SECRET, {
    expiresIn: '1h',
  });

  res.json({ token });
});

app.get('/perfil', authenticateToken, (req, res) => {
  res.json({ message: `Hola, ${req.user.username}` });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Servidor corriendo en http://localhost:${PORT}`);
});
