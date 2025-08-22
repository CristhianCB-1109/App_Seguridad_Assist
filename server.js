import app from './app.js';
import 'dotenv/config'; // Usamos esta sintaxis para cargar dotenv

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => console.log(`Servidor iniciado en el puerto ${PORT}`));