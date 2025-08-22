import app from './app.js';
import 'dotenv/config'; // Usamos esta sintaxis para cargar dotenv

const PORT = process.env.PORT || 3000;

app.get('/', (req, res) => {
  res.send('¡Hola! La API está funcionando.');
});

app.listen(PORT, () => console.log(`Servidor iniciado en el puerto ${PORT}`)); 